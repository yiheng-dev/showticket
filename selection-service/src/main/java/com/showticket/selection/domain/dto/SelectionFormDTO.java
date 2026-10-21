package com.showticket.selection.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "加入选票清单的表单实体")
public class SelectionFormDTO {
    @ApiModelProperty("演出id")
    private Long showId;
    @ApiModelProperty("演出名称")
    private String name;
    @ApiModelProperty("票档")
    private String ticketType;
    @ApiModelProperty("价格，单位：分")
    private Integer price;
    @ApiModelProperty("演出海报")
    private String poster;
    @ApiModelProperty("场次时间")
    private LocalDateTime showTime;
    @ApiModelProperty("购票数量")
    private Integer num;
}
