package com.showticket.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.showticket.order.domain.po.OrderTicket;
import com.showticket.order.mapper.OrderTicketMapper;
import com.showticket.order.service.IOrderTicketService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单票明细表 服务实现类
 * </p>
 *
 * @since 2023-05-05
 */
@Service
public class OrderTicketServiceImpl extends ServiceImpl<OrderTicketMapper, OrderTicket> implements IOrderTicketService {

}
