package com.wegame.capi.biz.module.ibongadarna.odds;

import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.domain.biz.symbol.IbongSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.module.base.odds.AbstractOddsCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component("ibongAdarnaOddsCalculateStrategy")
public class IbongAdarnaOddsCalculateStrategy extends AbstractOddsCalculateStrategy<IbongSymbolDo> {

    @Autowired
    IbongAdarnaConfig ibongAdarnaConfig;

    @Override
    public Map<String, Map<String, BigDecimal>> getOddsConfig(OddsCalculateDo<IbongSymbolDo> oddsCalculateDo) {
        return ibongAdarnaConfig.getOddsConfig().getCommonOddsConfig();
    }
}
