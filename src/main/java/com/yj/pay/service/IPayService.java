package com.yj.pay.service;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import com.yj.pay.pojo.PayInfo;

import java.math.BigDecimal;

/**
 * create by 86136
 * 2022/3/20 14:20
 */
public interface IPayService {
    /**
     * 创建/发起支付
     */
    PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum);

    /**
     * 异步通知处理
     * @param notifyData
     */
    String asyncNotify(String notifyData);

    /**
     * 查询支付记录通过订单号
     * @param id
     * @return
     */
    PayInfo selectByOrderNo(String id);
}
