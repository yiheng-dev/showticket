package com.showticket.show.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 演出表，一行代表某场演出的一个票档
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("show_info")
public class Show implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 演出id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 演出名称
     */
    private String name;

    /**
     * 票价（分）
     */
    private Integer price;

    /**
     * 余票数量
     */
    private Integer stock;

    /**
     * 演出海报
     */
    private String poster;

    /**
     * 演出类型：演唱会、音乐剧、话剧、音乐会等
     */
    private String genre;

    /**
     * 演出场馆
     */
    private String venue;

    /**
     * 票档：内场VIP、看台A区等
     */
    private String ticketType;

    /**
     * 场次时间
     */
    private LocalDateTime showTime;

    /**
     * 已售数量
     */
    private Integer sold;

    /**
     * 评价数
     */
    private Integer reviewCount;

    /**
     * 是否推荐位
     */
    @TableField("is_featured")
    private Boolean isFeatured;

    /**
     * 演出状态 1-上架，2-下架，3-删除
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private Long creater;

    /**
     * 修改人
     */
    private Long updater;


}
