package com.wegame.capi.biz.module.lamang.bet;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.module.base.bet.AbstractBetContext;
import com.wegame.capi.biz.module.base.bet.strategy.BetStrategy;
import com.wegame.capi.client.enums.BetTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * malakas投注上下文
 */
@Component("malakasBetContext")
public class MalakasBetContext extends AbstractBetContext {

    private static final Map<BetTypeEnum, String> BET_STRATEGY_NAME_MAP = new ImmutableMap.Builder<BetTypeEnum, String>()
            .put(BetTypeEnum.NORMAL_BET, "malakasNormalBetStrategy")
            .put(BetTypeEnum.BUY_BET, "malakasBuyBetStrategy")
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
