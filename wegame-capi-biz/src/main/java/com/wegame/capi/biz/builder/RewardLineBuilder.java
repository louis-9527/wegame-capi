package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.biz.reward.RewardLineDo;
import com.wegame.capi.biz.domain.biz.reward.RewardRuleLineDo;

import java.math.BigDecimal;
import java.util.Objects;

public class RewardLineBuilder {

    public static RewardLineDo build(Long betAmount, RewardRuleLineDo rewardRuleLine, Long rewardAmount, BigDecimal multiple, BigDecimal odds) {
        if (Objects.isNull(rewardRuleLine) || Objects.isNull(rewardAmount) || Objects.isNull(multiple) || Objects.isNull(odds) || Objects.isNull(betAmount)) {
            return null;
        }
        return RewardLineDo.builder()
                .betAmount(betAmount)
                .symbol(rewardRuleLine.getSymbol())
                .rewardAmount(rewardAmount)
                .multiple(multiple)
                .odds(odds)
                .symbolIndexList(rewardRuleLine.getRewardSymbolIndexList())
                .symbolList(rewardRuleLine.getRewardSymbolIdList())
                .build();
    }

}
