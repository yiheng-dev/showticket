package com.showticket.show.domain.query;

import com.showticket.common.domain.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "演出分页查询条件")
public class ShowPageQuery extends PageQuery {
    @ApiModelProperty("搜索关键字")
    private String key;
    @ApiModelProperty("演出类型")
    private String genre;
    @ApiModelProperty("演出场馆")
    private String venue;
    @ApiModelProperty("价格最小值")
    private Integer minPrice;
    @ApiModelProperty("价格最大值")
    private Integer maxPrice;
}
