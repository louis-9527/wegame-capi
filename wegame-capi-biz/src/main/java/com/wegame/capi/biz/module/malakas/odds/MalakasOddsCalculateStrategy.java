package com.wegame.capi.biz.module.malakas.odds;

import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.module.base.odds.AbstractOddsCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.TreeMap;

@Slf4j
@Component("malakasOddsCalculateStrategy")
public class MalakasOddsCalculateStrategy extends AbstractOddsCalculateStrategy {

    @Autowired
    MalakasConfig malakasConfig;

    @Override
    public TreeMap<String, TreeMap<String, BigDecimal>> getOddsConfig(OddsCalculateDo oddsCalculateDo) {
        return malakasConfig.getOddsConfig().getCommonOddsConfig();
    }
}
