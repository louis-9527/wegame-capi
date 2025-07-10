package com.wegame.capi.biz.module.lamang.odds;

import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.module.base.odds.AbstractOddsCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.TreeMap;

@Slf4j
@Component("lamangOddsCalculateStrategy")
public class LamangOddsCalculateStrategy extends AbstractOddsCalculateStrategy {

    @Autowired
    LamangConfig lamangConfig;

    @Override
    public TreeMap<String, TreeMap<String, BigDecimal>> getOddsConfig(OddsCalculateDo oddsCalculateDo) {
        return lamangConfig.getOddsConfig().getCommonOddsConfig();
    }
}
