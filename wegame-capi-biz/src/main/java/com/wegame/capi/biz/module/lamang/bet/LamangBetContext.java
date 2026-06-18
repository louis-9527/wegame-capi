package com.wegame.capi.biz.module.lamang.bet;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.module.base.bet.AbstractBetContext;
import com.wegame.capi.biz.module.base.bet.strategy.BetStrategy;
import com.wegame.capi.client.enums.BetTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.wegame.capi.common.Constants.LAMANG_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * lamang投注上下文
 */
@Component("lamangBetContext")
@ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
public class LamangBetContext extends AbstractBetContext {

    private static final Map<BetTypeEnum, String> BET_STRATEGY_NAME_MAP = new ImmutableMap.Builder<BetTypeEnum, String>()
            .put(BetTypeEnum.NORMAL_BET, "lamangNormalBetStrategy")
            .put(BetTypeEnum.BUY_BET, "lamangBuyBetStrategy")
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
