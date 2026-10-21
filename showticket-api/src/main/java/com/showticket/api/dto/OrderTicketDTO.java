package com.showticket.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@ApiModel(description = "订单票条目")
@Data
@Accessors(chain = true)
public class OrderTicketDTO {
    @ApiModelProperty("演出id")
    private Long showId;
    @ApiModelProperty("购票数量")
    private Integer num;
}
