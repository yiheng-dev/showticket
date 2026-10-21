package com.showticket.order.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "出票信息VO")
public class TicketDeliveryVO {
    @ApiModelProperty("订单id")
    private Long orderId;
    @ApiModelProperty("取票码")
    private String pickupCode;
    @ApiModelProperty("取票方式")
    private String deliveryType;
    @ApiModelProperty("联系人")
    private String contact;
    @ApiModelProperty("联系人手机号")
    private String mobile;
    @ApiModelProperty("省")
    private String province;
    @ApiModelProperty("市")
    private String city;
    @ApiModelProperty("区")
    private String town;
    @ApiModelProperty("街道")
    private String street;
}
