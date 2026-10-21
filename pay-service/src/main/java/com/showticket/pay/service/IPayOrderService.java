package com.showticket.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.showticket.pay.domain.dto.PayApplyDTO;
import com.showticket.pay.domain.dto.PayOrderFormDTO;
import com.showticket.pay.domain.po.PayOrder;

/**
 * <p>
 * 支付订单 服务类
 * </p>
 *
 * @since 2023-05-16
 */
public interface IPayOrderService extends IService<PayOrder> {

    Long applyPayOrder(PayApplyDTO applyDTO);

    void tryPayOrderByBalance(PayOrderFormDTO payOrderFormDTO);
}
