package com.wegame.capi.biz.service.odds.strategy;

import com.wegame.capi.biz.domain.game.OddsCalculateDo;

import java.math.BigDecimal;

/**
 * 赔率计算策略
 */
public interface OddsCalculateStrategy {

    /**
     * 计算赔率
     *
     * @param oddsCalculate oddsCalculate
     * @return 赔率
     */
    BigDecimal calculateOdds(OddsCalculateDo oddsCalculate);


}
