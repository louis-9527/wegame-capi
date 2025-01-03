package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.domain.OddsCalculateDo;
import com.wegame.capi.biz.domain.RewardHandleDo;
import com.wegame.capi.common.enums.RewardLineTypeEnum;

import java.util.Objects;

public class OddsCalculateBuilder {

    public static OddsCalculateDo build(RewardHandleDo rewardDo, SymbolDo symbolDo, RewardLineTypeEnum rewardLineType) {
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
