package com.showticket.order.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(description = "购票订单VO")
public class OrderVO {
    @ApiModelProperty("订单id")
    private Long id;
    @ApiModelProperty("总金额，单位为分")
    private Integer totalFee;
    @ApiModelProperty("支付类型，1、支付宝，2、微信，3、扣减余额")
    private Integer paymentType;
    @ApiModelProperty("用户id")
    private Long userId;
    @ApiModelProperty("订单状态：1待支付 2已支付待出票 3已出票 4已完成 5已关闭 6已评价")
    private Integer status;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty("支付时间")
    private LocalDateTime payTime;
    @ApiModelProperty("出票时间")
    private LocalDateTime issueTime;
    @ApiModelProperty("完成时间")
    private LocalDateTime endTime;
    @ApiModelProperty("关闭时间")
    private LocalDateTime closeTime;
    @ApiModelProperty("评价时间")
    private LocalDateTime reviewTime;
    @ApiModelProperty("票明细")
    private List<OrderTicketVO> tickets;
    @ApiModelProperty("出票信息")
    private TicketDeliveryVO delivery;
}
