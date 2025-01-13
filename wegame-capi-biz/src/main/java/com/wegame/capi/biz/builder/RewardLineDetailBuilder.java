package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.biz.reward.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.reward.RewardLineDo;

import java.math.BigDecimal;
import java.util.Objects;

public class RewardLineDetailBuilder {

    public static RewardLineDetailDo build(Long betAmount, RewardLineDo rewardRuleLine, Long rewardAmount, BigDecimal multiple, BigDecimal odds) {
        if (Objects.isNull(rewardRuleLine) || Objects.isNull(rewardAmount) || Objects.isNull(multiple) || Objects.isNull(odds) || Objects.isNull(betAmount)) {
            return null;
        }
        return RewardLineDetailDo.builder()
                .betAmount(betAmount)
                .rewardAmount(rewardAmount)
                .odds(odds)
                .multiple(multiple)
                .rewardLineType(rewardRuleLine.getRewardLineType())
                .rewardLineSubType(rewardRuleLine.getRewardLineSubType())
                .symbol(rewardRuleLine.getRewardSymbol())
                .symbolList(rewardRuleLine.getRewardSymbolList())
                .symbolIndexList(rewardRuleLine.getRewardSymbolIndexList())
                .build();
    }

}
