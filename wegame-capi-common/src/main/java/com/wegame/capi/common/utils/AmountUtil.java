package com.wegame.capi.common.utils;

import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;

/**
 * 金额工具
 * 保留小数点后4为，为兼容金额进度问题
 */
public class AmountUtil {

    public static BigDecimal toBigDecimal(Long amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return NumberUtil.div(String.valueOf(amount), "10000", 4);
    }

    public static long toLong(BigDecimal amount) {
        if (amount == null) {
            return 0L;
        }
        return NumberUtil.mul(amount, 10000).longValue();
    }
}
