package com.wegame.capi.biz.builder;

import com.google.common.collect.Lists;
import com.wegame.capi.biz.domain.game.RewardLineDo;
import com.wegame.capi.biz.domain.game.RewardResultDo;
import com.wegame.capi.biz.domain.game.SymbolIndexDo;
import com.wegame.capi.common.enums.RewardTypeEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RewardResultBuilder {

    public static RewardResultDo build(Long rewardTotalAmount, List<RewardLineDo> rewardLineList, BigDecimal wildTotalTimes, boolean isFreeSpin, Set<SymbolIndexDo> rewardCoordinateSets) {
        if (Objects.isNull(rewardTotalAmount) || Objects.isNull(rewardLineList) || Objects.isNull(rewardCoordinateSets)) {
            return null;
        }

        return RewardResultDo.builder()
                .rewardTotalAmount(rewardTotalAmount)
                .rewardLineList(rewardLineList)
                .wildTotalTimes(wildTotalTimes)
                .rewardType(rewardLineList.size() <= 1 ? RewardTypeEnum.SINGLE.getCode() : RewardTypeEnum.MULTIPLE.getCode())
                .rewardIndexList(Lists.newArrayList(rewardCoordinateSets))
                .isFreeSpin(isFreeSpin)
                .build();
    }

}
