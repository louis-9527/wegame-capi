package com.wegame.capi.biz.module.kapre.odds;

import com.wegame.capi.biz.config.domain.KapreConfig;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.module.base.odds.AbstractOddsCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.TreeMap;

@Slf4j
@Component("kapreOddsCalculateStrategy")
public class KapreOddsCalculateStrategy extends AbstractOddsCalculateStrategy {

    @Autowired
    KapreConfig kapreConfig;

    @Override
    public TreeMap<String, TreeMap<String, BigDecimal>> getOddsConfig(OddsCalculateDo oddsCalculateDo) {
        throw new RuntimeException("todo");
    }
}
