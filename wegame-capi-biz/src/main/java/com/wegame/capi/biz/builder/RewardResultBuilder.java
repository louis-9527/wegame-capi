package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.biz.reward.RewardLineDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.common.enums.PrizeTypeEnum;
import com.wegame.capi.common.enums.RewardTypeEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class RewardResultBuilder {

    public static RewardResultDo build(Long rewardTotalAmount, List<RewardLineDo> rewardLineList, BigDecimal wildTotalTimes, boolean isFreeSpin) {
        if (Objects.isNull(rewardTotalAmount) || Objects.isNull(rewardLineList)) {
            return null;
        }

        return RewardResultDo.builder().rewardTotalAmount(rewardTotalAmount).rewardLineList(rewardLineList).wildTotalTimes(wildTotalTimes).rewardType(rewardLineList.size() <= 1 ? RewardTypeEnum.SINGLE.getCode() : RewardTypeEnum.MULTIPLE.getCode()).isFreeSpin(isFreeSpin).build();
    }

    public static RewardResultDo build(RewardResultDo rewardResultDo, PrizeTypeEnum gameGrandPrizeType) {
        if (Objects.isNull(rewardResultDo) || Objects.isNull(gameGrandPrizeType)) {
            return null;
        }
        rewardResultDo.setGrandPrizeType(gameGrandPrizeType);
        return rewardResultDo;
    }

}
