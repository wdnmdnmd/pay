package com.yj.pay.enums;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import lombok.Getter;

/**
 * create by 86136
 * 2022/3/23 13:13
 */
@Getter
public enum PayPlatformEnum {
    //1支付宝2微信
    ALIPAY(1),
    WX(2),
    ;
    Integer code;

    PayPlatformEnum(Integer code) {
        this.code = code;
    }
    public static PayPlatformEnum getByBestPayTypeEnum(BestPayTypeEnum bestPayTypeEnum){
        for (PayPlatformEnum value : PayPlatformEnum.values()) {
            if (bestPayTypeEnum.getPlatform().name().equals(value.name())){
                return value;
            }
        }
        throw new RuntimeException("错误的支付平台"+bestPayTypeEnum.name());
    }
}
