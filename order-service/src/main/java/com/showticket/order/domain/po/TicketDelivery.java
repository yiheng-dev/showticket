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
 * 出票信息，与订单一对一
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ticket_delivery")
public class TicketDelivery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单id，与订单表一对一
     */
    @TableId(value = "order_id", type = IdType.INPUT)
    private Long orderId;

    /**
     * 取票码
     */
    private String pickupCode;

    /**
     * 取票方式：电子票、纸质票
     */
    private String deliveryType;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 联系人手机号
     */
    private String mobile;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String town;

    /**
     * 街道
     */
    private String street;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
