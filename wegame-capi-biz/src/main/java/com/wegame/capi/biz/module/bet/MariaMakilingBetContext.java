package com.wegame.capi.biz.module.bet;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.module.bet.strategy.BetStrategy;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * mariaMakilingBetContext投注上下文
 */
@Component("mariaMakilingBetContext")
public class MariaMakilingBetContext extends AbstractBetContext {

    private static final Map<BetSubTypeEnum, String> betStrategyNameMap = new ImmutableMap.Builder<BetSubTypeEnum, String>()
            .put(BetSubTypeEnum.NORMAL_BET, "mariaMakilingNormalBetStrategy")
            .put(BetSubTypeEnum.BUY_FREE_SPINS_BET, "mariaMakilingBuyFreeSpinsBetStrategy")
            .put(BetSubTypeEnum.NON_BUY_FREE_SPINS_BET, "mariaMakilingNonBuyFreeSpinsBetStrategy")
            .build();

    @Autowired
    Map<String, BetStrategy> betStrategyMap;

    @Override
    public Map<BetSubTypeEnum, String> betStrategyNameMap() {
        return betStrategyNameMap;
    }

    @Override
    public Map<String, BetStrategy> betStrategyMap() {
        return betStrategyMap;
    }
}
