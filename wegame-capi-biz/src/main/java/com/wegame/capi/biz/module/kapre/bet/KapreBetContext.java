package com.wegame.capi.biz.module.kapre.bet;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.module.base.bet.AbstractBetContext;
import com.wegame.capi.biz.module.base.bet.strategy.BetStrategy;
import com.wegame.capi.client.enums.BetTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * kapre投注上下文
 */
@Component("kapreBetContext")
public class KapreBetContext extends AbstractBetContext {

    private static final Map<BetTypeEnum, String> BET_STRATEGY_NAME_MAP = new ImmutableMap.Builder<BetTypeEnum, String>()
            .put(BetTypeEnum.NORMAL_BET, "kapreNormalBetStrategy")
            .put(BetTypeEnum.BUY_BET, "kapreBuyBetStrategy")
            .build();

    @Autowired
    Map<String, BetStrategy> betStrategyMap;

    @Override
    public Map<BetTypeEnum, String> betStrategyNameMap() {
        return BET_STRATEGY_NAME_MAP;
    }

    @Override
    public Map<String, BetStrategy> betStrategyMap() {
        return betStrategyMap;
    }
}
