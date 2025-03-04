package com.wegame.capi.common.utils;

import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;

public class AmountUtil {

    public static BigDecimal toBigDecimal(Long amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return NumberUtil.div(String.valueOf(amount), "100", 2);
    }

    public static long toLong(BigDecimal amount) {
        if (amount == null) {
            return 0L;
        }
        return NumberUtil.mul(amount, 100).longValue();
    }
}
