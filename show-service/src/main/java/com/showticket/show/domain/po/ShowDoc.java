package com.showticket.show.domain.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "演出搜索索引实体")
public class ShowDoc{

    @ApiModelProperty("演出id")
    private String id;

    @ApiModelProperty("演出名称")
    private String name;

    @ApiModelProperty("票价（分）")
    private Integer price;

    @ApiModelProperty("演出海报")
    private String poster;

    @ApiModelProperty("演出类型")
    private String genre;

    @ApiModelProperty("演出场馆")
    private String venue;

    @ApiModelProperty("已售数量")
    private Integer sold;

    @ApiModelProperty("评价数")
    private Integer reviewCount;

    @ApiModelProperty("是否推荐位")
    private Boolean isFeatured;

    @ApiModelProperty("场次时间")
    private LocalDateTime showTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
