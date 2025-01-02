package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.game.RewardHandleDo;
import com.wegame.capi.biz.domain.game.RewardRuleDo;

import java.util.Objects;

public class RewardRuleBuilder {

    public static RewardRuleDo build(RewardHandleDo rewardDo) {
        if (Objects.isNull(rewardDo)) {
            return null;
        }
        return RewardRuleDo.builder()
                .gameType(rewardDo.getGameType())
                .betType(rewardDo.getBetType())
                .betSubType(rewardDo.getBetSubType())
                .symbolArr(rewardDo.getSymbolArr())
                .build();
    }

}
