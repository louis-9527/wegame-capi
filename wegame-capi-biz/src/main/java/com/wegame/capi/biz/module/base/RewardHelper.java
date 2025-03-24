package com.wegame.capi.biz.module.base;

import cn.hutool.core.util.NumberUtil;
import com.wegame.capi.common.enums.RewardSubTypeEnum;

import java.util.Objects;

/**
 * RewardHelper
 */
public class RewardHelper {
    /**
     * 计算中奖子类型
     * @param betAmount 投注金额
     * @param totalRewardAmount 中奖总金额
     * @return 中奖子类型
     */
    public static RewardSubTypeEnum calculateRewardSubType(Long betAmount, Long totalRewardAmount) {
        if (Objects.isNull(betAmount)
                || Objects.isNull(totalRewardAmount)
                || betAmount <= 0
                || totalRewardAmount <= 0) {
            return RewardSubTypeEnum.NONE;
        }
        double rewardTimes = NumberUtil.div(totalRewardAmount.longValue(), betAmount.longValue());
        if (rewardTimes >= 5 && rewardTimes < 10) {
            return RewardSubTypeEnum.NICE;
        } else if (rewardTimes >= 10 && rewardTimes < 15) {
            return RewardSubTypeEnum.MEGA;
        } else if (rewardTimes >= 15 && rewardTimes < 20) {
            return RewardSubTypeEnum.SUPER;
        } else if (rewardTimes >= 20) {
            return RewardSubTypeEnum.SENSATIONAL;
        } else {
            return RewardSubTypeEnum.OTHER;
        }
    }
}
