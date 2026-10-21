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
 * 购票订单
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ticket_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 总金额，单位为分
     */
    private Integer totalFee;

    /**
     * 支付类型，1、支付宝，2、微信，3、扣减余额
     */
    private Integer paymentType;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单状态：1、待支付 2、已支付待出票 3、已出票 4、已完成 5、已关闭 6、已评价
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 出票时间
     */
    private LocalDateTime issueTime;

    /**
     * 完成时间
     */
    private LocalDateTime endTime;

    /**
     * 关闭时间
     */
    private LocalDateTime closeTime;

    /**
     * 评价时间
     */
    private LocalDateTime reviewTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
