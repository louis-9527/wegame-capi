package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.RewardLineDo;
import com.wegame.capi.biz.domain.RewardRuleLineDo;

import java.math.BigDecimal;
import java.util.Objects;

public class RewardLineBuilder {

    public static RewardLineDo build(RewardRuleLineDo rewardRuleLine, Long rewardAmount, BigDecimal multiple, BigDecimal odds) {
        if (Objects.isNull(rewardRuleLine) || Objects.isNull(rewardAmount) || Objects.isNull(multiple) || Objects.isNull(odds)) {
            return null;
        }

        return RewardLineDo.builder()
                .symbolDo(rewardRuleLine.getSymbol())
                .rewardAmount(rewardAmount)
                .rewardMultiple(multiple)
                .odds(odds)
                .rewardSymbolIndexList(rewardRuleLine.getRewardSymbolIndexList())
                .rewardSymbolList(rewardRuleLine.getRewardSymbolIdList())
                .build();
    }

}
