package com.wegame.capi.biz.module.bayanihan.odds;

import com.wegame.capi.biz.config.domain.BayanihanConfig;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.domain.biz.symbol.BayanihanSymbolDo;
import com.wegame.capi.biz.module.base.odds.AbstractOddsCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.TreeMap;

@Slf4j
@Component("bayanihanOddsCalculateStrategy")
public class BayanihanOddsCalculateStrategy extends AbstractOddsCalculateStrategy<BayanihanSymbolDo> {

    @Autowired
    BayanihanConfig bayanihanConfig;

    @Override
    public TreeMap<String, TreeMap<String, BigDecimal>> getOddsConfig(OddsCalculateDo oddsCalculateDo) {
        return bayanihanConfig.getOddsConfig().getCommonOddsConfig();
    }
}
