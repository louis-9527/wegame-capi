package com.wegame.capi.biz.module.base.reward;

/**
 * 中奖handler
 */
public class RewardUtil {


    public static boolean reachMaxRewardMultiple(long totalRewardAmount, Long betAmount, int maxMultiple) {
        return totalRewardAmount >= betAmount * maxMultiple;
    }

    public static long getOverallRewardAmount(long currentTotalAmount, Long payAmount, int rewardMaxMultiple) {
        return Math.min(currentTotalAmount, payAmount * rewardMaxMultiple);
    }
}
