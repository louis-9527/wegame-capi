package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.biz.reward.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.common.enums.RewardSubTypeEnum;
import com.wegame.capi.common.enums.RewardTypeEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class RewardResultBuilder {

    public static RewardResultDo build(Long betAmount, Long rewardTotalAmount, List<RewardLineDetailDo> rewardLineList, BigDecimal wildTotalTimes, boolean isFreeSpin) {
        if (Objects.isNull(rewardTotalAmount) || Objects.isNull(rewardLineList)) {
            return null;
        }

        return RewardResultDo.builder()
                .betAmount(betAmount)
                .totalRewardAmount(rewardTotalAmount)
                .rewardLineDetailList(rewardLineList)
                .totalMultiple(wildTotalTimes)
                .rewardType(rewardLineList.size() == 1 ? RewardTypeEnum.SINGLE : RewardTypeEnum.MULTIPLE)
                .hasRewardFreeSpin(isFreeSpin)
                .build();
    }

    public static RewardResultDo build(RewardResultDo rewardResultDo, RewardSubTypeEnum rewardSubType) {
        if (Objects.isNull(rewardResultDo) || Objects.isNull(rewardSubType)) {
            return null;
        }
        rewardResultDo.setRewardSubType(rewardSubType);
        return rewardResultDo;
    }

}
