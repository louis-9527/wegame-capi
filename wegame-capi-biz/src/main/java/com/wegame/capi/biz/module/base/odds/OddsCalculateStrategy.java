package com.wegame.capi.biz.module.base.odds;

import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;

import java.math.BigDecimal;

/**
 * 赔率计算策略
 */
public interface OddsCalculateStrategy<T extends SymbolDo> {

    /**
     * 计算赔率
     *
     * @param oddsCalculate oddsCalculate
     * @return 赔率
     */
    BigDecimal calculateOdds(OddsCalculateDo<T> oddsCalculate);
}
