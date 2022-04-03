package com.yj.pay.service.impl;

import com.google.gson.Gson;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import com.yj.pay.dao.PayInfoMapper;
import com.yj.pay.enums.PayPlatformEnum;
import com.yj.pay.pojo.PayInfo;
import com.yj.pay.service.IPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * create by 86136
 * 2022/3/20 14:23
 */
@Slf4j
@Service
public class PayServiceImpl implements IPayService {

    private final static String QUEUE_PAY_NOTIFY="payNotify";

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private BestPayService bestPayService;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Override
    public PayResponse create(String orderId, BigDecimal amount,BestPayTypeEnum bestPayTypeEnum) {
        //写入数据库
        PayInfo payInfo = new PayInfo(
                Long.parseLong(orderId),
                PayPlatformEnum.getByBestPayTypeEnum(bestPayTypeEnum).getCode(),
                OrderStatusEnum.NOTPAY.name(),
                amount);
        payInfoMapper.insertSelective(payInfo);
        PayRequest request = new PayRequest();
        //支付方式
        request.setPayTypeEnum(BestPayTypeEnum.WXPAY_NATIVE);
        request.setOrderId(orderId);
        request.setOrderName("微信公众账号支付订单");
        request.setOrderAmount(amount.doubleValue());
        PayResponse response = bestPayService.pay(request);
        log.info("发起支付 response={}",response);
        return response;
    }

    @Override
    public String asyncNotify(String notifyData) {
        //1.签名校验
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("异步通知 payResponse={}",payResponse);
        //2.金额校验 从数据库查订单
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(payResponse.getOrderId()));
        if (payInfo==null){
            //告警 严重的错误建议电话或者钉钉通知
            throw new RuntimeException("通过orderNo查到的结果是null");
        }
        //检查支付状态,如果订单状态不是已经支付
        if (!payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.name())){
            if (payInfo.getPayAmount().compareTo(BigDecimal.valueOf(payResponse.getOrderAmount()))==0){
                //告警
                throw new RuntimeException("异步通知金额和数据库不一致,orderNo="+payResponse.getOrderId());
            }
        }
        //3.修改订单支付状态
        payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
        payInfo.setPlatformNumber(payResponse.getOutTradeNo());
        payInfo.setUpdateTime(null);
        payInfoMapper.updateByPrimaryKeySelective(payInfo);

        //TODO pay发送mq消息,mall接收
        amqpTemplate.convertAndSend(QUEUE_PAY_NOTIFY,new Gson().toJson(payInfo));
        //4.告诉微信不要再通知了
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }

    @Override
    public PayInfo selectByOrderNo(String id) {
        return payInfoMapper.selectByOrderNo(Long.parseLong(id));
    }
}
