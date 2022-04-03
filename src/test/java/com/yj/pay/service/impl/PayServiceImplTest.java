package com.yj.pay.service.impl;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.yj.pay.PayApplicationTests;
import org.junit.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

import java.math.BigDecimal;

/**
 * create by 86136
 * 2022/3/20 14:41
 */
public class PayServiceImplTest extends PayApplicationTests {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Resource
    private PayServiceImpl payService;

    @Test
    public void create() {
        //也可以用 new BigDecimal("0.01") 千万不要用new BigDecimal(0.01)
        payService.create("12465464653135645613", BigDecimal.valueOf(0.01), BestPayTypeEnum.WXPAY_NATIVE);
    }

    @Test
    public void sendMQMsg(){
        amqpTemplate.convertAndSend("payNotify","hello");
    }
}