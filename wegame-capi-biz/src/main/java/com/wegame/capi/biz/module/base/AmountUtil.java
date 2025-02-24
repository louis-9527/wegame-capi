package com.wegame.capi.biz.module.base;

/**
 * AmountUtil
 */
public class AmountUtil {

    /**
     * 是否达到最大倍数
     *
     * @param totalAmount
     * @param payAmount
     * @param maxMultiple
     * @return 是/否
     */
    public static boolean reachMaxMultiple(long totalAmount, long payAmount, int maxMultiple) {
        return totalAmount >= payAmount * maxMultiple;
    }

    /**
     * 最大金额控制
     * 支付金额 * 最大倍数，中奖金额， 从其中取最小值
     * @param totalAmount 总金额
     * @param payAmount 支付金额
     * @param maxMultiple 最大倍数
     * @return 实际金额
     */
    public static long maxAmountControl(long totalAmount, long payAmount, int maxMultiple) {
        return Math.min(totalAmount, payAmount * maxMultiple);
    }
}
