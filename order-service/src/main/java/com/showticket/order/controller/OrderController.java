package com.showticket.order.controller;

import com.showticket.common.domain.PageDTO;
import com.showticket.common.domain.PageQuery;
import com.showticket.order.domain.dto.OrderFormDTO;
import com.showticket.order.domain.vo.OrderVO;
import com.showticket.order.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "购票订单管理接口")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @ApiOperation("分页查询我的订单")
    @GetMapping
    public PageDTO<OrderVO> queryMyOrders(PageQuery query) {
        return orderService.queryMyOrders(query);
    }

    @ApiOperation("根据id查询订单详情")
    @GetMapping("{id}")
    public OrderVO queryOrderById(@PathVariable("id") Long orderId) {
        return orderService.queryOrderDetail(orderId);
    }

    @ApiOperation("创建订单")
    @PostMapping
    public Long createOrder(@RequestBody OrderFormDTO orderFormDTO){
        return orderService.createOrder(orderFormDTO);
    }

    @ApiOperation("标记订单已支付")
    @ApiImplicitParam(name = "orderId", value = "订单id", paramType = "path")
    @PutMapping("/{orderId}")
    public void markOrderPaySuccess(@PathVariable("orderId") Long orderId) {
        orderService.markOrderPaySuccess(orderId);
    }

    @ApiOperation("取消未支付订单")
    @PutMapping("/{orderId}/cancel")
    public void cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrderByUser(orderId);
    }
}
