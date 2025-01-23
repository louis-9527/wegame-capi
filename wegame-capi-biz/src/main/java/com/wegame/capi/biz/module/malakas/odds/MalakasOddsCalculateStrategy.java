package com.wegame.capi.biz.module.malakas.odds;

import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.module.base.odds.AbstractOddsCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Component("malakasOddsCalculateStrategy")
public class MalakasOddsCalculateStrategy extends AbstractOddsCalculateStrategy {
    // todo
    @Override
    public Map<String, Map<String, BigDecimal>> getOddsConfig(OddsCalculateDo oddsCalculateDo) {
        return Collections.emptyMap();
    }

//    @Autowired
//    MariaMakilingConfig mariaMakilingConfig;
//
//    @Override
//    public Map<String, Map<String, BigDecimal>> getOddsConfig(OddsCalculateDo oddsCalculateDo) {
//        return mariaMakilingConfig.getOddsConfig().getCommonOddsConfig();
//    }
}
