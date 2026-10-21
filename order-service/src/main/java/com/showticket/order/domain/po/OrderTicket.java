package com.showticket.order.domain.po;

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
 * 订单票明细
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_ticket")
public class OrderTicket implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单票明细id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单id
     */
    private Long orderId;

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
