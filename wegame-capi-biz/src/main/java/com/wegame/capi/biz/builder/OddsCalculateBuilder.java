package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.domain.game.OddsCalculateDo;
import com.wegame.capi.biz.domain.game.RewardDo;
import com.wegame.capi.biz.domain.game.RewardRuleDo;
import com.wegame.capi.common.enums.RewardLineTypeEnum;

import java.util.Objects;

public class OddsCalculateBuilder {

    public static OddsCalculateDo build(RewardDo rewardDo, SymbolDo symbolDo, RewardLineTypeEnum rewardLineType) {
        if (Objects.isNull(rewardDo) || Objects.isNull(symbolDo) || Objects.isNull(rewardLineType)) {
            return null;
        }
        return OddsCalculateDo.builder()
                .gameType(rewardDo.getGameType())
                .betType(rewardDo.getBetType())
                .betSubType(rewardDo.getBetSubType())
                .oddsType(rewardLineType.getOddsType())
                .symbol(symbolDo)
                .build();
    }

}
