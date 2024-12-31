package com.wegame.capi.biz.module.multiple;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.module.multiple.strategy.MultipleCalculateStrategy;
import com.wegame.capi.common.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 倍率上下文
 */
@Slf4j
@Service
public class MultipleCalculateContext {

    private static final Map<GameTypeEnum, String> strategyNameMap = new ImmutableMap.Builder<GameTypeEnum, String>()
            .put(GameTypeEnum.MARIA_MAKILING, "mariaMakilingOddsCalculateOddsStrategy")
            .build();

    @Autowired
    Map<String, MultipleCalculateStrategy> multipleCalculateStrategyMap;

    public BigDecimal calculateMultiple(GameTypeEnum gameType, SymbolDo[][] symbolArr) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_INTER);
        String strategyName = strategyNameMap.get(gameType);
        MultipleCalculateStrategy strategy = multipleCalculateStrategyMap.get(strategyName);
        AssertUtil.assertNotNull(strategy, ErrorCode.ERR_INTER, "NOT SUPPORT");
        return strategy.calculateMultiple(gameType, symbolArr);
    }
}
