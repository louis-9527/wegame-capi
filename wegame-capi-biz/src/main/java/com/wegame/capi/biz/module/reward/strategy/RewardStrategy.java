package com.wegame.capi.biz.module.reward.strategy;

import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.domain.game.RewardResultDo;
import com.wegame.capi.common.enums.BetSubTypeEnum;
import com.wegame.capi.common.enums.BetTypeEnum;
import com.wegame.capi.common.enums.GameTypeEnum;

/**
 * 中奖handler
 */
public interface RewardStrategy {

    // todo
    RewardResultDo handle(GameTypeEnum gameType,
                          BetTypeEnum betType,
                          BetSubTypeEnum betSubType,
                          Long betAmount,
                          SymbolDo[][] symbolArr);

}
