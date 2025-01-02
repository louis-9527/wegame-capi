package com.wegame.capi.biz.module.odds.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.capi.biz.domain.game.OddsCalculateDo;
import com.wegame.capi.infrastructure.config.nacos.NacosMariaMakilingOddsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component("mariaMakilingOddsCalculateStrategy")
public class MariaMakilingOddsCalculateStrategy extends AbstractOddsCalculateStrategy {

    @Autowired
    NacosMariaMakilingOddsConfig nacosMariaMakilingOddsConfig;

    @Override
    public Map<String, Map<String, BigDecimal>> getOddsMap(OddsCalculateDo oddsCalculateDo) {
        return nacosMariaMakilingOddsConfig.getOddsConfigMap();
    }
}
