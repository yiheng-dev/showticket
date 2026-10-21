package com.showticket.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "演出票档实体")
public class ShowDTO {
    @ApiModelProperty("演出id")
    private Long id;
    @ApiModelProperty("演出名称")
    private String name;
    @ApiModelProperty("票价（分）")
    private Integer price;
    @ApiModelProperty("余票数量")
    private Integer stock;
    @ApiModelProperty("演出海报")
    private String poster;
    @ApiModelProperty("演出类型")
    private String genre;
    @ApiModelProperty("演出场馆")
    private String venue;
    @ApiModelProperty("票档")
    private String ticketType;
    @ApiModelProperty("场次时间")
    private LocalDateTime showTime;
    @ApiModelProperty("已售数量")
    private Integer sold;
    @ApiModelProperty("评价数")
    private Integer reviewCount;
    @ApiModelProperty("是否推荐位")
    private Boolean isFeatured;
    @ApiModelProperty("演出状态 1-上架，2-下架，3-删除")
    private Integer status;
}
