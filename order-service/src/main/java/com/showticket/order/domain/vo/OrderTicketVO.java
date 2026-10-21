package com.showticket.order.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "订单票明细VO")
public class OrderTicketVO {
    @ApiModelProperty("明细id")
    private Long id;
    @ApiModelProperty("演出id")
    private Long showId;
    @ApiModelProperty("演出名称")
    private String name;
    @ApiModelProperty("票档")
    private String ticketType;
    @ApiModelProperty("购票数量")
    private Integer num;
    @ApiModelProperty("票价（分）")
    private Integer price;
    @ApiModelProperty("演出海报")
    private String poster;
    @ApiModelProperty("场次时间")
    private LocalDateTime showTime;
}
