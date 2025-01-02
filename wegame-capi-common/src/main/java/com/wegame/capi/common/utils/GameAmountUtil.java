package com.wegame.capi.common.utils;

import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;

public class GameAmountUtil {
    public static BigDecimal toBigDecimal(Long amount){
        if(amount == null){
            return BigDecimal.ZERO;
        }
       return NumberUtil.div(String.valueOf(amount), "100", 2);
    }
}
