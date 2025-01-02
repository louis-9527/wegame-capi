package com.wegame.capi.biz.module.odds;

import com.wegame.capi.biz.domain.game.OddsCalculateDo;
import com.wegame.capi.infrastructure.config.nacos.NacosMariaMakilingConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component("mariaMakilingOddsCalculateStrategy")
public class MariaMakilingOddsCalculateStrategy extends AbstractOddsCalculateStrategy {

    @Autowired
    NacosMariaMakilingConfig nacosMariaMakilingConfig;

    @Override
    public Map<String, Map<String, BigDecimal>> getOddsMap(OddsCalculateDo oddsCalculateDo) {
        return nacosMariaMakilingConfig.getOddsConfig();
    }
}
