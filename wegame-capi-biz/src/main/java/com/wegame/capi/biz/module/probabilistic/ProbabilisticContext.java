package com.wegame.capi.biz.module.probabilistic;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.module.probabilistic.strategy.AbstractProbabilisticStrategy;
import com.wegame.capi.common.enums.GameTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 概率上下文
 */
@Component
public class ProbabilisticContext {

    private static final Map<GameTypeEnum, String> strategyNameMap = new ImmutableMap.Builder<GameTypeEnum, String>()
            .put(GameTypeEnum.MARIA_MAKILING, "mariaMakilingProbabilisticStrategy")
            .build();

    @Autowired
    Map<String, AbstractProbabilisticStrategy> probabilisticStrategyMap;

    public String randomOneSymbol(String gameId, int cellNo) {
        GameTypeEnum gameType = GameTypeEnum.valueOfCode(gameId);
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "Not Support Game");
        String strategyName = strategyNameMap.get(gameType);
        AbstractProbabilisticStrategy strategy = probabilisticStrategyMap.get(strategyName);
        AssertUtil.assertNotNull(strategy, ErrorCode.ERR_INTER, "NOT SUPPORT");
        return strategy.randomOneSymbol(gameType, cellNo);
    }

}
