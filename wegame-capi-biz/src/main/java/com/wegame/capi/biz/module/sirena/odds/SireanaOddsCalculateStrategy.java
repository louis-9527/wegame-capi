package com.wegame.capi.biz.module.sirena.odds;

import com.wegame.capi.biz.config.domain.SirenaConfig;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.domain.biz.symbol.SirenaSymbolDo;
import com.wegame.capi.biz.module.base.odds.AbstractOddsCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.TreeMap;

@Slf4j
@Component("sirenaOddsCalculateStrategy")
public class SireanaOddsCalculateStrategy extends AbstractOddsCalculateStrategy<SirenaSymbolDo> {

    @Autowired
    SirenaConfig sirenaConfig;

    @Override
    public TreeMap<String, TreeMap<String, BigDecimal>> getOddsConfig(OddsCalculateDo oddsCalculateDo) {
        return sirenaConfig.getOddsConfig().getCommonOddsConfig();
    }
}
