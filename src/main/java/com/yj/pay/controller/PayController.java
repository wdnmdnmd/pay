package com.yj.pay.controller;

import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import com.yj.pay.pojo.PayInfo;
import com.yj.pay.service.impl.PayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * create by 86136
 * 2022/3/20 15:37
 */
@Controller
@RequestMapping("/pay")
@Slf4j
public class PayController {
    @Autowired
    private PayServiceImpl payService;
    @Autowired
    private WxPayConfig wxPayConfig;

    @GetMapping("/create")
    public ModelAndView creat(@RequestParam("orderId") String orderId, @RequestParam("amount") BigDecimal amount
    , @RequestParam("payType")BestPayTypeEnum bestPayTypeEnum){
        PayResponse payResponse = payService.create(orderId, amount,bestPayTypeEnum);
        Map map = new HashMap<>();
        map.put("codeUrl",payResponse.getCodeUrl());
        map.put("orderId",orderId);
        map.put("returnUrl",wxPayConfig.getReturnUrl());
        return new ModelAndView("create",map);
    }
    @PostMapping("/notify")
    @ResponseBody
    public String asyncNotify(@RequestBody String notifyData){
        return payService.asyncNotify(notifyData);
    }

    @GetMapping("/queryByOrderId")
    @ResponseBody
    public PayInfo queryByOrderId(@RequestParam("id") String id){
        log.info("查询支付状态...");
        return payService.selectByOrderNo(id);
    }
}
