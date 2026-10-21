package com.showticket.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.showticket.common.domain.PageDTO;
import com.showticket.common.domain.PageQuery;
import com.showticket.order.domain.dto.OrderFormDTO;
import com.showticket.order.domain.po.Order;
import com.showticket.order.domain.vo.OrderVO;

/**
 * <p>
 *  服务类
 * </p>
 */
public interface IOrderService extends IService<Order> {

    Long createOrder(OrderFormDTO orderFormDTO);

    void markOrderPaySuccess(Long orderId);

    void cancelOrder(Long orderId);

    void cancelOrderByUser(Long orderId);

    PageDTO<OrderVO> queryMyOrders(PageQuery query);

    OrderVO queryOrderDetail(Long orderId);
}
