package com.showticket.selection.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 选票清单视图
 * </p>
 */
@Data
@ApiModel(description = "选票清单VO实体")
public class SelectionVO {
    @ApiModelProperty("选票清单条目id")
    private Long id;
    @ApiModelProperty("演出id")
    private Long showId;
    @ApiModelProperty("购买数量")
    private Integer num;
    @ApiModelProperty("演出名称")
    private String name;
    @ApiModelProperty("票档")
    private String ticketType;
    @ApiModelProperty("价格，单位：分")
    private Integer price;
    @ApiModelProperty("演出最新价格")
    private Integer newPrice;
    @ApiModelProperty("演出最新状态")
    private Integer status = 1;
    @ApiModelProperty("演出最新余票")
    private Integer stock = 10;
    @ApiModelProperty("演出海报")
    private String poster;
    @ApiModelProperty("场次时间")
    private LocalDateTime showTime;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

}
