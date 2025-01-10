package com.wegame.capi.biz.helper;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.OddsTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.common.enums.SymbolRuleTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.wegame.capi.common.enums.SymbolEnum.*;

public class MariaMakilingGameHelper {

    public static final SymbolDo MARIA_MAKILING_Symbol1001 = SymbolDo.builder()
            .id(Symbol1001.getId())
            .name(Symbol1001.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MARIA_MAKILING_Symbol1002 = SymbolDo.builder()
            .id(Symbol1002.getId())
            .name(Symbol1002.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MARIA_MAKILING_Symbol1003 = SymbolDo.builder()
            .id(Symbol1003.getId())
            .name(Symbol1003.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MARIA_MAKILING_Symbol1004 = SymbolDo.builder()
            .id(Symbol1004.getId())
            .name(Symbol1004.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MARIA_MAKILING_Symbol1005 = SymbolDo.builder()
            .id(Symbol1005.getId())
            .name(Symbol1005.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MARIA_MAKILING_Symbol2001 = SymbolDo.builder()
            .id(Symbol2001.getId())
            .name(Symbol2001.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MARIA_MAKILING_Symbol2002 = SymbolDo.builder()
            .id(Symbol2002.getId())
            .name(Symbol2002.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MARIA_MAKILING_Symbol2003 = SymbolDo.builder()
            .id(Symbol2003.getId())
            .name(Symbol2003.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MARIA_MAKILING_Symbol2004 = SymbolDo.builder()
            .id(Symbol2004.getId())
            .name(Symbol2004.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MARIA_MAKILING_Symbol2005 = SymbolDo.builder()
            .id(Symbol2005.getId())
            .name(Symbol2005.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    public static final SymbolDo MARIA_MAKILING_SCATTER = SymbolDo.builder()
            .id(Scatter.getId())
            .name(Scatter.getName())
            .symbolType(SymbolTypeEnum.SCATTER)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();

    private static final SymbolDo MARIA_MAKILING_Wild = SymbolDo.builder()
            .id(Wild.getId())
            .name(Wild.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.MULTIPLE).value(new BigDecimal(0)).build())
            .build();
    private static final SymbolDo MARIA_MAKILING_WildX2 = SymbolDo.builder()
            .id(WildX2.getId())
            .name(WildX2.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.MULTIPLE).value(new BigDecimal(2)).build())
            .build();
    private static final SymbolDo MARIA_MAKILING_WildX3 = SymbolDo.builder()
            .id(WildX3.getId())
            .name(WildX3.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.MULTIPLE).value(new BigDecimal(3)).build())
            .build();
    private static final SymbolDo MARIA_MAKILING_WildX5 = SymbolDo.builder()
            .id(WildX5.getId())
            .name(WildX5.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.MULTIPLE).value(new BigDecimal(5)).build())
            .build();
    private static final SymbolDo MARIA_MAKILING_WildX10 = SymbolDo.builder()
            .id(WildX10.getId())
            .name(WildX10.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.MULTIPLE).value(new BigDecimal(10)).build())
            .build();

    public static final GameInfoDo MARIA_MAKILING_GAME_INFO = GameInfoDo.builder()
            .id(GameTypeEnum.MARIA_MAKILING.getId())
            .gameType(GameTypeEnum.MARIA_MAKILING)
            .row(3)
            .column(5)
            .symbolMap(new ImmutableMap.Builder<String, SymbolDo>()
                    .put(MARIA_MAKILING_Symbol1001.getId(), MARIA_MAKILING_Symbol1001)
                    .put(MARIA_MAKILING_Symbol1002.getId(), MARIA_MAKILING_Symbol1002)
                    .put(MARIA_MAKILING_Symbol1003.getId(), MARIA_MAKILING_Symbol1003)
                    .put(MARIA_MAKILING_Symbol1004.getId(), MARIA_MAKILING_Symbol1004)
                    .put(MARIA_MAKILING_Symbol1005.getId(), MARIA_MAKILING_Symbol1005)
                    .put(MARIA_MAKILING_Symbol2001.getId(), MARIA_MAKILING_Symbol2001)
                    .put(MARIA_MAKILING_Symbol2002.getId(), MARIA_MAKILING_Symbol2002)
                    .put(MARIA_MAKILING_Symbol2003.getId(), MARIA_MAKILING_Symbol2003)
                    .put(MARIA_MAKILING_Symbol2004.getId(), MARIA_MAKILING_Symbol2004)
                    .put(MARIA_MAKILING_Symbol2005.getId(), MARIA_MAKILING_Symbol2005)
                    .put(MARIA_MAKILING_SCATTER.getId(), MARIA_MAKILING_SCATTER)
                    .put(MARIA_MAKILING_Wild.getId(), MARIA_MAKILING_Wild)
                    .put(MARIA_MAKILING_WildX2.getId(), MARIA_MAKILING_WildX2)
                    .put(MARIA_MAKILING_WildX3.getId(), MARIA_MAKILING_WildX3)
                    .put(MARIA_MAKILING_WildX5.getId(), MARIA_MAKILING_WildX5)
                    .put(MARIA_MAKILING_WildX10.getId(), MARIA_MAKILING_WildX10)
                    .build())
            .build();

    /**
     * MARIA_MAKILING免费轮次数概率配置 <br/>
     * Map<次数，次数对应概率>
     */
    public static final Map<Integer, Integer> MARIA_MAKILING_FREE_WHEELS_TIMES_PROBABILISTIC_CONFIG = new ImmutableMap.Builder<Integer, Integer>()
            .put(10, 50)
            .put(11, 35)
            .put(12, 10)
            .put(13, 5)
            .build();

    /**
     * MARIA_MAKILING正常轮元素概率配置
     */
    public static final Map<SymbolEnum, List<BigDecimal>> MARIA_MAKILING_COMMON_WHEEL_PROBABILISTIC_CONFIG = new ImmutableMap.Builder<SymbolEnum, List<BigDecimal>>()
            .put(SymbolEnum.Symbol1001, Arrays.asList(
                    new BigDecimal(15),
                    new BigDecimal(15),
                    new BigDecimal(15.4),
                    new BigDecimal(14),
                    new BigDecimal(15)))
            .put(SymbolEnum.Symbol1002, Arrays.asList(
                    new BigDecimal(15),
                    new BigDecimal(15),
                    new BigDecimal(14),
                    new BigDecimal(14),
                    new BigDecimal(15)))
            .put(SymbolEnum.Symbol1003, Arrays.asList(
                    new BigDecimal(15),
                    new BigDecimal(15),
                    new BigDecimal(14),
                    new BigDecimal(14),
                    new BigDecimal(15)))
            .put(SymbolEnum.Symbol1004, Arrays.asList(
                    new BigDecimal(15),
                    new BigDecimal(14),
                    new BigDecimal(14),
                    new BigDecimal(15),
                    new BigDecimal(15)))
            .put(SymbolEnum.Symbol1005, Arrays.asList(
                    new BigDecimal(15),
                    new BigDecimal(13.8),
                    new BigDecimal(14),
                    new BigDecimal(15),
                    new BigDecimal(15)))
            .put(SymbolEnum.Symbol2001, Arrays.asList(
                    new BigDecimal(5.1),
                    new BigDecimal(5.3),
                    new BigDecimal(5),
                    new BigDecimal(5.3),
                    new BigDecimal(5.1)))
            .put(SymbolEnum.Symbol2002, Arrays.asList(
                    new BigDecimal(5.1),
                    new BigDecimal(5.3),
                    new BigDecimal(5),
                    new BigDecimal(5.3),
                    new BigDecimal(5.1)))
            .put(SymbolEnum.Symbol2003, Arrays.asList(
                    new BigDecimal(5),
                    new BigDecimal(4.5),
                    new BigDecimal(5),
                    new BigDecimal(5.3),
                    new BigDecimal(5)))
            .put(SymbolEnum.Symbol2004, Arrays.asList(
                    new BigDecimal(4.9),
                    new BigDecimal(4.3),
                    new BigDecimal(5),
                    new BigDecimal(4.3),
                    new BigDecimal(4.9)))
            .put(SymbolEnum.Symbol2005, Arrays.asList(
                    new BigDecimal(4.8),
                    new BigDecimal(4.3),
                    new BigDecimal(5),
                    new BigDecimal(4.3),
                    new BigDecimal(4.8)))
            .put(SymbolEnum.Scatter, Arrays.asList(
                    new BigDecimal(0.1),
                    new BigDecimal(0),
                    new BigDecimal(0.1),
                    new BigDecimal(0),
                    new BigDecimal(0.1)))
            .put(SymbolEnum.Wild, Arrays.asList(
                    new BigDecimal(0),
                    new BigDecimal(1.5),
                    new BigDecimal(1.5),
                    new BigDecimal(1.5),
                    new BigDecimal(0)))
            .put(SymbolEnum.WildX2, Arrays.asList(
                    new BigDecimal(0),
                    new BigDecimal(1),
                    new BigDecimal(1),
                    new BigDecimal(1),
                    new BigDecimal(0)))
            .put(SymbolEnum.WildX3, Arrays.asList(
                    new BigDecimal(0),
                    new BigDecimal(0.5),
                    new BigDecimal(0.5),
                    new BigDecimal(0.5),
                    new BigDecimal(0)))
            .put(SymbolEnum.WildX5, Arrays.asList(
                    new BigDecimal(0),
                    new BigDecimal(0.3),
                    new BigDecimal(0.3),
                    new BigDecimal(0.3),
                    new BigDecimal(0)))
            .put(SymbolEnum.WildX10, Arrays.asList(
                    new BigDecimal(0),
                    new BigDecimal(0.2),
                    new BigDecimal(0.2),
                    new BigDecimal(0.2),
                    new BigDecimal(0)))
            .build();
    /**
     * MARIA_MAKILING免费轮元素概率配置
     */
    public static final Map<SymbolEnum, List<BigDecimal>> MARIA_MAKILING_FREE_WHEEL_PROBABILISTIC_CONFIG = new ImmutableMap.Builder<SymbolEnum, List<BigDecimal>>()
            .put(SymbolEnum.Symbol1001, Arrays.asList(
                    new BigDecimal(20),
                    new BigDecimal(17.3),
                    new BigDecimal(17.3),
                    new BigDecimal(17.3),
                    new BigDecimal(20)))
            .put(SymbolEnum.Symbol1002, Arrays.asList(
                    new BigDecimal(14),
                    new BigDecimal(13),
                    new BigDecimal(13),
                    new BigDecimal(13),
                    new BigDecimal(14)))
            .put(SymbolEnum.Symbol1003, Arrays.asList(
                    new BigDecimal(12),
                    new BigDecimal(12),
                    new BigDecimal(12),
                    new BigDecimal(12),
                    new BigDecimal(12)))
            .put(SymbolEnum.Symbol1004, Arrays.asList(
                    new BigDecimal(11),
                    new BigDecimal(11),
                    new BigDecimal(11),
                    new BigDecimal(11),
                    new BigDecimal(11)))
            .put(SymbolEnum.Symbol1005, Arrays.asList(
                    new BigDecimal(10),
                    new BigDecimal(10),
                    new BigDecimal(10),
                    new BigDecimal(10),
                    new BigDecimal(10)))
            .put(SymbolEnum.Symbol2001, Arrays.asList(
                    new BigDecimal(9),
                    new BigDecimal(9),
                    new BigDecimal(9),
                    new BigDecimal(9),
                    new BigDecimal(9)))
            .put(SymbolEnum.Symbol2002, Arrays.asList(
                    new BigDecimal(8),
                    new BigDecimal(8),
                    new BigDecimal(8),
                    new BigDecimal(8),
                    new BigDecimal(8)))
            .put(SymbolEnum.Symbol2003, Arrays.asList(
                    new BigDecimal(7),
                    new BigDecimal(7),
                    new BigDecimal(7),
                    new BigDecimal(7),
                    new BigDecimal(7)))
            .put(SymbolEnum.Symbol2004, Arrays.asList(
                    new BigDecimal(5),
                    new BigDecimal(5),
                    new BigDecimal(5),
                    new BigDecimal(5),
                    new BigDecimal(5)))
            .put(SymbolEnum.Symbol2005, Arrays.asList(
                    new BigDecimal(4),
                    new BigDecimal(4),
                    new BigDecimal(5),
                    new BigDecimal(4),
                    new BigDecimal(4)))
            .put(SymbolEnum.Scatter, Arrays.asList(
                    new BigDecimal(0),
                    new BigDecimal(0),
                    new BigDecimal(0),
                    new BigDecimal(0),
                    new BigDecimal(0)))
            .put(SymbolEnum.Wild, Arrays.asList(
                    new BigDecimal(0),
                    new BigDecimal(1.6),
                    new BigDecimal(1.6),
                    new BigDecimal(1.6),
                    new BigDecimal(0)))
            .put(SymbolEnum.WildX2, Arrays.asList(
                    new BigDecimal(0),
                    new BigDecimal(1.1),
                    new BigDecimal(1.1),
                    new BigDecimal(1.1),
                    new BigDecimal(0)))
            .put(SymbolEnum.WildX3, Arrays.asList(
                    new BigDecimal(0),
                    new BigDecimal(0.5),
                    new BigDecimal(0.5),
                    new BigDecimal(0.5),
                    new BigDecimal(0)))
            .put(SymbolEnum.WildX5, Arrays.asList(
                    new BigDecimal(0),
                    new BigDecimal(0.3),
                    new BigDecimal(0.3),
                    new BigDecimal(0.3),
                    new BigDecimal(0)))
            .put(SymbolEnum.WildX10, Arrays.asList(
                    new BigDecimal(0),
                    new BigDecimal(0.2),
                    new BigDecimal(0.2),
                    new BigDecimal(0.2),
                    new BigDecimal(0)))
            .build();

    public static final Map<SymbolEnum, Map<OddsTypeEnum, BigDecimal>> MARIA_MAKILING_COMMON_ODDS_CONFIG_MAP = new ImmutableMap.Builder<SymbolEnum, Map<OddsTypeEnum, BigDecimal>>()
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
}
