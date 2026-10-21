package com.showticket.order.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.showticket.api.client.SelectionClient;
import com.showticket.api.client.ShowClient;
import com.showticket.api.client.UserClient;
import com.showticket.api.dto.AddressDTO;
import com.showticket.api.dto.OrderTicketDTO;
import com.showticket.api.dto.ShowDTO;
import com.showticket.common.domain.PageDTO;
import com.showticket.common.domain.PageQuery;
import com.showticket.common.exception.BadRequestException;
import com.showticket.common.exception.BizIllegalException;
import com.showticket.common.utils.BeanUtils;
import com.showticket.common.utils.CollUtils;
import com.showticket.common.utils.UserContext;
import com.showticket.order.constants.MQConstants;
import com.showticket.order.domain.dto.OrderFormDTO;
import com.showticket.order.domain.po.Order;
import com.showticket.order.domain.po.OrderTicket;
import com.showticket.order.domain.po.TicketDelivery;
import com.showticket.order.domain.vo.OrderTicketVO;
import com.showticket.order.domain.vo.OrderVO;
import com.showticket.order.domain.vo.TicketDeliveryVO;
import com.showticket.order.mapper.OrderMapper;
import com.showticket.order.service.IOrderService;
import com.showticket.order.service.IOrderTicketService;
import com.showticket.order.service.ITicketDeliveryService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 购票订单 服务实现类
 * </p>
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Value("${ticket.order.pay-timeout-ms:120000}")
    private long payTimeoutMs;

    private final ShowClient showClient;
    private final IOrderTicketService orderTicketService;
    private final ITicketDeliveryService ticketDeliveryService;
    private final SelectionClient selectionClient;
    private final UserClient userClient;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @GlobalTransactional
    public Long createOrder(OrderFormDTO orderFormDTO) {
        // 0.收票地址
        if (orderFormDTO.getDeliveryAddressId() == null) {
            throw new BadRequestException("请选择收票地址");
        }
        AddressDTO address = userClient.getAddressById(orderFormDTO.getDeliveryAddressId());
        if (address == null) {
            throw new BadRequestException("收票地址不存在");
        }

        // 1.订单数据
        Order order = new Order();
        // 1.1.查询演出票档
        List<OrderTicketDTO> ticketDTOS = orderFormDTO.getTickets();
        // 1.2.获取演出id和购票数量的Map
        Map<Long, Integer> showNumMap = ticketDTOS.stream()
                .collect(Collectors.toMap(OrderTicketDTO::getShowId, OrderTicketDTO::getNum));
        Set<Long> showIds = showNumMap.keySet();
        // 1.3.查询演出
        List<ShowDTO> shows = showClient.queryShowByIds(showIds);
        if (shows == null || shows.size() < showIds.size()) {
            throw new BadRequestException("演出不存在");
        }
        // 1.4.基于票价、购票数量计算订单总价：totalFee
        int total = 0;
        for (ShowDTO show : shows) {
            total += show.getPrice() * showNumMap.get(show.getId());
        }
        order.setTotalFee(total);
        // 1.5.其它属性
        order.setPaymentType(orderFormDTO.getPaymentType());
        order.setUserId(UserContext.getUser());
        order.setStatus(1);
        // 1.6.写入订单
        save(order);

        // 2.保存订单票明细
        List<OrderTicket> tickets = buildOrderTickets(order.getId(), shows, showNumMap);
        orderTicketService.saveBatch(tickets);

        // 3.写出票信息
        saveTicketDelivery(order.getId(), address);

        // 4.清理选票清单
        selectionClient.deleteSelectionByShowIds(showIds);

        // 5.扣减余票
        try {
            showClient.deductTicketStock(ticketDTOS);
        } catch (Exception e) {
            throw new RuntimeException("余票不足！");
        }

        // 6.发送延迟消息，检测订单支付状态
        rabbitTemplate.convertAndSend(
                MQConstants.DELAY_EXCHANGE_NAME,
                MQConstants.DELAY_ORDER_KEY,
                order.getId(),
                message -> {
                    message.getMessageProperties().setDelay((int) payTimeoutMs);
                    return message;
                });

        return order.getId();
    }

    @Override
    public void markOrderPaySuccess(Long orderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(3);
        order.setPayTime(LocalDateTime.now());
        order.setIssueTime(LocalDateTime.now());
        updateById(order);

        // 支付成功后才生成取票码
        TicketDelivery delivery = ticketDeliveryService.getById(orderId);
        if (delivery != null && delivery.getPickupCode() == null) {
            delivery.setPickupCode(buildPickupCode(orderId));
            ticketDeliveryService.updateById(delivery);
        }
    }

    @Override
    public void cancelOrder(Long orderId) {
        // 1.查询订单
        Order order = getById(orderId);
        if (order == null || order.getStatus() != 1) {
            return;
        }
        // 2.原子地把待支付订单关闭，避免用户取消和超时取消重复恢复余票
        boolean closed = lambdaUpdate()
                .eq(Order::getId, orderId)
                .eq(Order::getStatus, 1)
                .set(Order::getStatus, 5)
                .set(Order::getCloseTime, LocalDateTime.now())
                .update();
        if (!closed) {
            return;
        }
        // 3.恢复余票
        List<OrderTicket> tickets = orderTicketService.lambdaQuery()
                .eq(OrderTicket::getOrderId, orderId)
                .list();
        if (CollUtils.isNotEmpty(tickets)) {
            List<OrderTicketDTO> ticketDTOS = BeanUtils.copyList(tickets, OrderTicketDTO.class);
            showClient.restoreTicketStock(ticketDTOS);
        }
    }

    @Override
    public void cancelOrderByUser(Long orderId) {
        Order order = getById(orderId);
        if (order == null || !order.getUserId().equals(UserContext.getUser())) {
            throw new BadRequestException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BizIllegalException("只有待支付订单可以取消");
        }
        cancelOrder(orderId);
    }

    @Override
    public PageDTO<OrderVO> queryMyOrders(PageQuery query) {
        Page<Order> page = lambdaQuery()
                .eq(Order::getUserId, UserContext.getUser())
                .page(query.toMpPage("create_time", false));
        return PageDTO.of(page, OrderVO.class);
    }

    @Override
    public OrderVO queryOrderDetail(Long orderId) {
        Order order = getById(orderId);
        if (order == null || !order.getUserId().equals(UserContext.getUser())) {
            throw new BadRequestException("订单不存在");
        }
        OrderVO vo = BeanUtils.copyBean(order, OrderVO.class);
        List<OrderTicket> tickets = orderTicketService.lambdaQuery()
                .eq(OrderTicket::getOrderId, orderId)
                .list();
        vo.setTickets(BeanUtils.copyList(tickets, OrderTicketVO.class));
        vo.setDelivery(BeanUtils.copyBean(ticketDeliveryService.getById(orderId), TicketDeliveryVO.class));
        return vo;
    }

    private void saveTicketDelivery(Long orderId, AddressDTO address) {
        TicketDelivery delivery = new TicketDelivery();
        delivery.setOrderId(orderId);
        delivery.setDeliveryType("电子票");
        delivery.setContact(address.getContact());
        delivery.setMobile(address.getMobile());
        delivery.setProvince(address.getProvince());
        delivery.setCity(address.getCity());
        delivery.setTown(address.getTown());
        delivery.setStreet(address.getStreet());
        ticketDeliveryService.save(delivery);
    }

    private String buildPickupCode(Long orderId) {
        String orderNo = orderId.toString();
        return "TK" + orderNo.substring(Math.max(0, orderNo.length() - 8));
    }

    private List<OrderTicket> buildOrderTickets(Long orderId, List<ShowDTO> shows, Map<Long, Integer> numMap) {
        List<OrderTicket> tickets = new ArrayList<>(shows.size());
        for (ShowDTO show : shows) {
            OrderTicket ticket = new OrderTicket();
            ticket.setName(show.getName());
            ticket.setTicketType(show.getTicketType());
            ticket.setPrice(show.getPrice());
            ticket.setNum(numMap.get(show.getId()));
            ticket.setShowId(show.getId());
            ticket.setPoster(show.getPoster());
            ticket.setShowTime(show.getShowTime());
            ticket.setOrderId(orderId);
            tickets.add(ticket);
        }
        return tickets;
    }
}
