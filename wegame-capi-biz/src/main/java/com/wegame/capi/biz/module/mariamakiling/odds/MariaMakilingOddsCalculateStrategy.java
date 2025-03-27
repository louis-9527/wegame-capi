package com.wegame.capi.biz.module.mariamakiling.odds;

import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.module.base.odds.AbstractOddsCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Component("mariaMakilingOddsCalculateStrategy")
public class MariaMakilingOddsCalculateStrategy extends AbstractOddsCalculateStrategy<SymbolDo> {

    @Autowired
    MariaMakilingConfig mariaMakilingConfig;

    @Override
    public TreeMap<String, TreeMap<String, BigDecimal>> getOddsConfig(OddsCalculateDo<SymbolDo> oddsCalculateDo) {
        return mariaMakilingConfig.getOddsConfig().getCommonOddsConfig();
    }
}
