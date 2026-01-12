package com.wegame.capi.biz.module.atlantis.odds;

import com.wegame.capi.biz.config.domain.AtlantisConfig;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.module.base.odds.AbstractOddsCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.TreeMap;

@Slf4j
@Component("atlantisOddsCalculateStrategy")
public class AtlantisOddsCalculateStrategy extends AbstractOddsCalculateStrategy<SymbolDo> {

    @Autowired
    AtlantisConfig atlantisConfig;

    @Override
    public TreeMap<String, TreeMap<String, BigDecimal>> getOddsConfig(OddsCalculateDo oddsCalculateDo) {
        return null;
    }
}
