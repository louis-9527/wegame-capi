package com.wegame.capi.biz.module.bet;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.module.bet.strategy.BetStrategy;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * mariaMakilingBetContext投注上下文
 */
@Component("mariaMakilingBetContext")
public class MariaMakilingBetContext extends AbstractBetContext {

    private static final Map<BetTypeEnum, String> betStrategyNameMap = new ImmutableMap.Builder<BetTypeEnum, String>()
            .put(BetTypeEnum.NORMAL_BET, "mariaMakilingNormalBetStrategy")
            .put(BetTypeEnum.BUY_BET, "mariaMakilingBuyBetStrategy")
            .build();

    @Autowired
    Map<String, BetStrategy> betStrategyMap;

    @Override
    public Map<BetTypeEnum, String> betStrategyNameMap() {
        return betStrategyNameMap;
    }

    @Override
    public Map<String, BetStrategy> betStrategyMap() {
        return betStrategyMap;
    }
}
