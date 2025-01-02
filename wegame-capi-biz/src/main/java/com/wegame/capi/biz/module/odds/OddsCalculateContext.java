package com.wegame.capi.biz.module.odds;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.game.OddsCalculateDo;
import com.wegame.capi.biz.module.odds.strategy.OddsCalculateStrategy;
import com.wegame.capi.common.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 赔率计算上下文
 */
@Slf4j
@Service
public class OddsCalculateContext {

    private static final Map<GameTypeEnum, String> strategyNameMap = new ImmutableMap.Builder<GameTypeEnum, String>()
            .put(GameTypeEnum.MARIA_MAKILING, "mariaMakilingOddsCalculateStrategy")
            .build();

    @Autowired
    Map<String, OddsCalculateStrategy> oddsCalculateStrategyMap;

    /**
     * 计算赔率
     * @param oddsDo 入参
     * @return 赔率
     */
    public BigDecimal calculateOdds(OddsCalculateDo oddsDo) {
        AssertUtil.assertNotNull(oddsDo, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(oddsDo.getBetType(), ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(oddsDo.getBetSubType(), ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(oddsDo.getGameType(), ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(oddsDo.getOddsType(), ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(oddsDo.getSymbol(), ErrorCode.ERR_INTER);
        String strategyName = strategyNameMap.get(oddsDo.getGameType());
        OddsCalculateStrategy strategy = oddsCalculateStrategyMap.get(strategyName);
        AssertUtil.assertNotNull(strategy, ErrorCode.ERR_INTER, "NOT SUPPORT");
        return strategy.calculateOdds(oddsDo);
    }
}
