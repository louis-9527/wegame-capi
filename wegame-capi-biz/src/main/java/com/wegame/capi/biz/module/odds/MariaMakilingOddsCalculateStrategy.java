package com.wegame.capi.biz.module.odds;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.common.enums.OddsTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component("mariaMakilingOddsCalculateStrategy")
public class MariaMakilingOddsCalculateStrategy extends AbstractOddsCalculateStrategy {

    private static final Map<SymbolEnum, Map<OddsTypeEnum, BigDecimal>> COMMON_ODDS_CONFIG_MAP = new ImmutableMap.Builder<SymbolEnum, Map<OddsTypeEnum, BigDecimal>>()
            .put(SymbolEnum.Symbol1001, new ImmutableMap.Builder<OddsTypeEnum, BigDecimal>()
                    .put(OddsTypeEnum.THREE, new BigDecimal(0.1))
                    .put(OddsTypeEnum.FOUR, new BigDecimal(0.2))
                    .put(OddsTypeEnum.FIVE, new BigDecimal(0.4))
                    .build())
            .put(SymbolEnum.Symbol1002, new ImmutableMap.Builder<OddsTypeEnum, BigDecimal>()
                    .put(OddsTypeEnum.THREE, new BigDecimal(0.2))
                    .put(OddsTypeEnum.FOUR, new BigDecimal(0.3))
                    .put(OddsTypeEnum.FIVE, new BigDecimal(0.6))
                    .build())
            .put(SymbolEnum.Symbol1003, new ImmutableMap.Builder<OddsTypeEnum, BigDecimal>()
                    .put(OddsTypeEnum.THREE, new BigDecimal(0.3))
                    .put(OddsTypeEnum.FOUR, new BigDecimal(0.5))
                    .put(OddsTypeEnum.FIVE, new BigDecimal(0.9))
                    .build())
            .put(SymbolEnum.Symbol1004, new ImmutableMap.Builder<OddsTypeEnum, BigDecimal>()
                    .put(OddsTypeEnum.THREE, new BigDecimal(0.4))
                    .put(OddsTypeEnum.FOUR, new BigDecimal(0.6))
                    .put(OddsTypeEnum.FIVE, new BigDecimal(1))
                    .build())
            .put(SymbolEnum.Symbol1005, new ImmutableMap.Builder<OddsTypeEnum, BigDecimal>()
                    .put(OddsTypeEnum.THREE, new BigDecimal(0.5))
                    .put(OddsTypeEnum.FOUR, new BigDecimal(0.7))
                    .put(OddsTypeEnum.FIVE, new BigDecimal(1.4))
                    .build())
            .put(SymbolEnum.Symbol2001, new ImmutableMap.Builder<OddsTypeEnum, BigDecimal>()
                    .put(OddsTypeEnum.THREE, new BigDecimal(0.6))
                    .put(OddsTypeEnum.FOUR, new BigDecimal(0.8))
                    .put(OddsTypeEnum.FIVE, new BigDecimal(2))
                    .build())
            .put(SymbolEnum.Symbol2002, new ImmutableMap.Builder<OddsTypeEnum, BigDecimal>()
                    .put(OddsTypeEnum.THREE, new BigDecimal(0.7))
                    .put(OddsTypeEnum.FOUR, new BigDecimal(0.9))
                    .put(OddsTypeEnum.FIVE, new BigDecimal(2.2))
                    .build())
            .put(SymbolEnum.Symbol2003, new ImmutableMap.Builder<OddsTypeEnum, BigDecimal>()
                    .put(OddsTypeEnum.THREE, new BigDecimal(0.8))
                    .put(OddsTypeEnum.FOUR, new BigDecimal(1.2))
                    .put(OddsTypeEnum.FIVE, new BigDecimal(2.4))
                    .build())
            .put(SymbolEnum.Symbol2004, new ImmutableMap.Builder<OddsTypeEnum, BigDecimal>()
                    .put(OddsTypeEnum.THREE, new BigDecimal(0.9))
                    .put(OddsTypeEnum.FOUR, new BigDecimal(1.5))
                    .put(OddsTypeEnum.FIVE, new BigDecimal(2.6))
                    .build())
            .put(SymbolEnum.Symbol2005, new ImmutableMap.Builder<OddsTypeEnum, BigDecimal>()
                    .put(OddsTypeEnum.THREE, new BigDecimal(1))
                    .put(OddsTypeEnum.FOUR, new BigDecimal(2))
                    .put(OddsTypeEnum.FIVE, new BigDecimal(5))
                    .build())
            .put(SymbolEnum.Scatter, new ImmutableMap.Builder<OddsTypeEnum, BigDecimal>()
                    .put(OddsTypeEnum.THREE, new BigDecimal(2))
                    .put(OddsTypeEnum.FOUR, new BigDecimal(0))
                    .put(OddsTypeEnum.FIVE, new BigDecimal(0))
                    .build())
            .put(SymbolEnum.Wild, new ImmutableMap.Builder<OddsTypeEnum, BigDecimal>()
                    .put(OddsTypeEnum.THREE, new BigDecimal(0))
                    .put(OddsTypeEnum.FOUR, new BigDecimal(0))
                    .put(OddsTypeEnum.FIVE, new BigDecimal(0))
                    .build())
            .put(SymbolEnum.WildX2, new ImmutableMap.Builder<OddsTypeEnum, BigDecimal>()
                    .put(OddsTypeEnum.THREE, new BigDecimal(0))
                    .put(OddsTypeEnum.FOUR, new BigDecimal(0))
                    .put(OddsTypeEnum.FIVE, new BigDecimal(0))
                    .build())
            .put(SymbolEnum.WildX3, new ImmutableMap.Builder<OddsTypeEnum, BigDecimal>()
                    .put(OddsTypeEnum.THREE, new BigDecimal(0))
                    .put(OddsTypeEnum.FOUR, new BigDecimal(0))
                    .put(OddsTypeEnum.FIVE, new BigDecimal(0))
                    .build())
            .put(SymbolEnum.WildX5, new ImmutableMap.Builder<OddsTypeEnum, BigDecimal>()
                    .put(OddsTypeEnum.THREE, new BigDecimal(0))
                    .put(OddsTypeEnum.FOUR, new BigDecimal(0))
                    .put(OddsTypeEnum.FIVE, new BigDecimal(0))
                    .build())
            .put(SymbolEnum.WildX10, new ImmutableMap.Builder<OddsTypeEnum, BigDecimal>()
                    .put(OddsTypeEnum.THREE, new BigDecimal(0))
                    .put(OddsTypeEnum.FOUR, new BigDecimal(0))
                    .put(OddsTypeEnum.FIVE, new BigDecimal(0))
                    .build())
            .build();

    @Override
    public Map<SymbolEnum, Map<OddsTypeEnum, BigDecimal>> getOddsMap(OddsCalculateDo oddsCalculateDo) {
        return COMMON_ODDS_CONFIG_MAP;
    }
}
