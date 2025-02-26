package com.wegame.capi.biz.module.ibongadarna.odds;

import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.domain.biz.symbol.IbongAdarnaSymbolDo;
import com.wegame.capi.biz.module.base.odds.AbstractOddsCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.TreeMap;

@Slf4j
@Component("ibongAdarnaOddsCalculateStrategy")
public class IbongAdarnaOddsCalculateStrategy extends AbstractOddsCalculateStrategy<IbongAdarnaSymbolDo> {

    @Autowired
    IbongAdarnaConfig ibongAdarnaConfig;

    @Override
    public TreeMap<String, TreeMap<String, BigDecimal>> getOddsConfig(OddsCalculateDo<IbongAdarnaSymbolDo> oddsCalculateDo) {
        return ibongAdarnaConfig.getOddsConfig().getCommonOddsConfig();
    }
}
