package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.biz.reward.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.reward.RewardLineDo;

import java.math.BigDecimal;
import java.util.Objects;

public class RewardLineDetailBuilder {

    public static RewardLineDetailDo build(Long betAmount,
                                           RewardLineDo rewardLine,
                                           Long rewardAmount,
                                           BigDecimal multiple,
                                           BigDecimal odds) {
        if (Objects.isNull(rewardLine) || Objects.isNull(rewardAmount) || Objects.isNull(multiple) || Objects.isNull(odds) || Objects.isNull(betAmount)) {
            return null;
        }
        return RewardLineDetailDo.builder()
                .betAmount(betAmount)
                .rewardAmount(rewardAmount)
                .odds(odds)
                .multiple(multiple)
                .rewardLineType(rewardLine.getRewardLineType())
                .rewardLineSubType(rewardLine.getRewardLineSubType())
                .symbol(rewardLine.getRewardSymbol())
                .symbolList(rewardLine.getRewardSymbolList())
                .symbolIndexList(rewardLine.getRewardSymbolIndexList())
                .build();
    }

}
