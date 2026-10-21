package com.showticket.order.domain.dto;

import com.showticket.api.dto.OrderTicketDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "提交购票订单的表单实体")
public class OrderFormDTO {
    @ApiModelProperty("收票地址id")
    private Long deliveryAddressId;
    @ApiModelProperty("支付类型")
    private Integer paymentType;
    @ApiModelProperty("下单票列表")
    private List<OrderTicketDTO> tickets;
}
