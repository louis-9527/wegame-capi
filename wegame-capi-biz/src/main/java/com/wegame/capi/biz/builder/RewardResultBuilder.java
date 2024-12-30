package com.wegame.capi.biz.builder;

import com.google.common.collect.Lists;
import com.wegame.capi.biz.domain.game.CoordinateDo;
import com.wegame.capi.biz.domain.game.RewardLineDo;
import com.wegame.capi.biz.domain.game.RewardResultDo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RewardResultBuilder {

    public static RewardResultDo build(Long rewardTotalAmount, List<RewardLineDo> rewardLineList, BigDecimal wildTotalTimes, boolean isFreeSpin, Set<int[]> rewardCoordinateList) {
        if (Objects.isNull(rewardTotalAmount) || Objects.isNull(rewardLineList) || Objects.isNull(rewardCoordinateList)) {
            return null;
        }

        return RewardResultDo.builder()
                .rewardTotalAmount(rewardTotalAmount)
                .rewardLineList(rewardLineList)
                .wildTotalTimes(wildTotalTimes)
                .rewardType(rewardLineList.size() <= 1 ? 1 : 2)
                .rewardCoordinateList(Lists.newArrayList(rewardCoordinateList))
                .isFreeSpin(isFreeSpin)
                .build();
    }

}
