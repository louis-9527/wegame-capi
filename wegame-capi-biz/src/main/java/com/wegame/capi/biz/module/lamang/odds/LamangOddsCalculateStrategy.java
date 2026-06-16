package com.wegame.capi.biz.module.lamang.odds;

import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.module.base.odds.AbstractOddsCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.TreeMap;

import static com.wegame.capi.common.Constants.LAMANG_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Slf4j
@Component("lamangOddsCalculateStrategy")
@ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
public class LamangOddsCalculateStrategy extends AbstractOddsCalculateStrategy {

    @Autowired
    LamangConfig lamangConfig;

    @Override
    public TreeMap<String, TreeMap<String, BigDecimal>> getOddsConfig(OddsCalculateDo oddsCalculateDo) {
        return lamangConfig.getOddsConfig().getCommonOddsConfig();
    }
}
