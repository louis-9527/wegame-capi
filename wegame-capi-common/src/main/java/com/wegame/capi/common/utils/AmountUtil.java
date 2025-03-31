package com.wegame.capi.common.utils;

import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 金额工具
 * 保留小数点后3为，为兼容金额进度问题
 */
public class AmountUtil {

    public static BigDecimal toBigDecimal(Long amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return NumberUtil.div(String.valueOf(amount), "1000", 3);
    }

    public static long toLong(BigDecimal amount) {
        if (amount == null) {
            return 0L;
        }
        return NumberUtil.mul(amount, 1000).longValue();
    }

    /**
     * 根据patten将金额转换为decimal string
     *
     * @param patten       patten 参考 {@link DecimalFormat}
     * @param amount       amount
     * @param roundingMode 取模模式
     * @return string decimal
     */
    public static String decimalFormat(String patten, Number amount, RoundingMode roundingMode) {
        return NumberUtil.decimalFormat(patten, amount);
    }

    /**
     * 根据patten将金额转换为decimal string
     * 多余位数直接去除
     *
     * @param patten patten 参考 {@link DecimalFormat}
     * @param amount amount
     * @return string decimal
     */
    public static String decimalFormat(String patten, Number amount) {
        return NumberUtil.decimalFormat(patten, amount, RoundingMode.FLOOR);
    }
}
