package com.wegame.capi.biz.module.bet;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.module.bet.strategy.BetStrategy;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Mysterious投注上下文
 */
@Component("mysteriousBetContext")
public class MysteriousBetContext extends AbstractBetContext {

    private static final Map<BetSubTypeEnum, String> betStrategyNameMap = new ImmutableMap.Builder<BetSubTypeEnum, String>()
            .put(BetSubTypeEnum.NORMAL_BET, "mysteriousNormalBetStrategy")
            .put(BetSubTypeEnum.BUY_FREE_SPINS_BET, "mysteriousBuyFreeSpinsBetStrategy")
            .put(BetSubTypeEnum.NON_BUY_FREE_SPINS_BET, "mysteriousNonBuyFreeSpinsBetStrategy")
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
