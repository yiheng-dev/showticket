package com.showticket.selection.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 选票清单条目
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ticket_selection")
public class Selection implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 选票清单条目id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 演出id
     */
    private Long showId;

    /**
     * 购买数量
     */
    private Integer num;

    /**
     * 演出名称
     */
    private String name;

    /**
     * 票档
     */
    private String ticketType;

    /**
     * 价格，单位：分
     */
    private Integer price;

    /**
     * 演出海报
     */
    private String poster;

    /**
     * 场次时间
     */
    private LocalDateTime showTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
