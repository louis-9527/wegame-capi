package com.wegame.capi.biz.module.odds;

import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.common.enums.OddsTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

import static com.wegame.capi.biz.helper.MariaMakilingGameHelper.MARIA_MAKILING_COMMON_ODDS_CONFIG_MAP;

@Slf4j
@Component("mariaMakilingOddsCalculateStrategy")
public class MariaMakilingOddsCalculateStrategy extends AbstractOddsCalculateStrategy {

    @Override
    public Map<SymbolEnum, Map<OddsTypeEnum, BigDecimal>> getOddsMap(OddsCalculateDo oddsCalculateDo) {
        return MARIA_MAKILING_COMMON_ODDS_CONFIG_MAP;
    }
}
