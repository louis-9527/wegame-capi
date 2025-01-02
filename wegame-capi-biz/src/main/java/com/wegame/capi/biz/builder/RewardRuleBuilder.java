package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.game.RewardDo;
import com.wegame.capi.biz.domain.game.RewardLineDo;
import com.wegame.capi.biz.domain.game.RewardResultDo;
import com.wegame.capi.biz.domain.game.RewardRuleDo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RewardRuleBuilder {

    public static RewardRuleDo build(RewardDo rewardDo) {
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
