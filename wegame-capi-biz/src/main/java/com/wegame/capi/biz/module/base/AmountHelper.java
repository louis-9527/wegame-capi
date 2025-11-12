package com.wegame.capi.biz.module.base;

import com.wegame.capi.client.enums.GamePlayTypeEnum;

import java.util.Objects;

/**
 * AmountHelper
 */
public class AmountHelper {

    /**
     * 是否达到最大倍数
     * @param totalAmount 总金额
     * @param betAmount 投注金额
     * @param maxMultiple 最大倍数
     * @return 是/否
     */
    public static boolean reachMaxMultiple(long totalAmount, long betAmount, int maxMultiple) {
        return totalAmount >= betAmount * maxMultiple;
    }
    /**
     * 是否达到最大倍数
     * @param gamePlayTypeEnum 投注类型
     * @param totalAmount 总金额
     * @param betAmount 投注金额
     * @param maxMultiple 最大倍数
     * @return 是/否
     */
    public static boolean reachMaxMultiple(GamePlayTypeEnum gamePlayTypeEnum, long totalAmount, long betAmount, int maxMultiple) {
        if (Objects.equals(gamePlayTypeEnum, GamePlayTypeEnum.EVENT_PLAY)) {
            return false;
        }
        return totalAmount >= betAmount * maxMultiple;
    }

    /**
     * 最大金额控制
     * 支付金额 * 最大倍数，中奖金额， 从其中取最小值
     * @param totalAmount 总金额
     * @param betAmount 投注金额
     * @param maxMultiple 最大倍数
     * @return 实际金额
     */
    public static long maxAmountControl(long totalAmount, long betAmount, int maxMultiple) {
        return Math.min(totalAmount, betAmount * maxMultiple);
    }
}
