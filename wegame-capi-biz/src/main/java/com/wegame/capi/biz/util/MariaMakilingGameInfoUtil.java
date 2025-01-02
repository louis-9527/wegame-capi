package com.wegame.capi.biz.util;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.GameInfoDo;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.common.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolRuleTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import java.math.BigDecimal;
import java.util.Map;

import static com.wegame.capi.common.enums.SymbolEnum.*;

public class MariaMakilingGameInfoUtil {

    public static final SymbolDo MARIA_MAKILING_Symbol1001 = SymbolDo.builder()
            .id(Symbol1001.getId())
            .name(Symbol1001.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    public static final SymbolDo MARIA_MAKILING_Symbol1002 = SymbolDo.builder()
            .id(Symbol1002.getId())
            .name(Symbol1002.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    public static final SymbolDo MARIA_MAKILING_Symbol1003 = SymbolDo.builder()
            .id(Symbol1003.getId())
            .name(Symbol1003.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    public static final SymbolDo MARIA_MAKILING_Symbol1004 = SymbolDo.builder()
            .id(Symbol1004.getId())
            .name(Symbol1004.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    public static final SymbolDo MARIA_MAKILING_Symbol1005 = SymbolDo.builder()
            .id(Symbol1005.getId())
            .name(Symbol1005.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    public static final SymbolDo MARIA_MAKILING_Symbol2001 = SymbolDo.builder()
            .id(Symbol2001.getId())
            .name(Symbol2001.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    public static final SymbolDo MARIA_MAKILING_Symbol2002 = SymbolDo.builder()
            .id(Symbol2002.getId())
            .name(Symbol2002.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    public static final SymbolDo MARIA_MAKILING_Symbol2003 = SymbolDo.builder()
            .id(Symbol2003.getId())
            .name(Symbol2003.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    public static final SymbolDo MARIA_MAKILING_Symbol2004 = SymbolDo.builder()
            .id(Symbol2004.getId())
            .name(Symbol2004.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    public static final SymbolDo MARIA_MAKILING_Symbol2005 = SymbolDo.builder()
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

    public static final SymbolDo MARIA_MAKILING_Wild = SymbolDo.builder()
            .id(Wild.getId())
            .name(Wild.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.MULTIPLE).value(new BigDecimal(0)).build())
            .build();
    public static final SymbolDo MARIA_MAKILING_WildX2 = SymbolDo.builder()
            .id(WildX2.getId())
            .name(WildX2.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.MULTIPLE).value(new BigDecimal(2)).build())
            .build();
    public static final SymbolDo MARIA_MAKILING_WildX3 = SymbolDo.builder()
            .id(WildX3.getId())
            .name(WildX3.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.MULTIPLE).value(new BigDecimal(3)).build())
            .build();
    public static final SymbolDo MARIA_MAKILING_WildX5 = SymbolDo.builder()
            .id(WildX5.getId())
            .name(WildX5.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.MULTIPLE).value(new BigDecimal(5)).build())
            .build();
    public static final SymbolDo MARIA_MAKILING_WildX10 = SymbolDo.builder()
            .id(WildX10.getId())
            .name(WildX10.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.MULTIPLE).value(new BigDecimal(10)).build())
            .build();

    private static final Map<String, SymbolDo> SYMBOL_MAP = new ImmutableMap.Builder<String, SymbolDo>()
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
            .build();


    public static final GameInfoDo MARIA_MAKILING_GAME_INFO = GameInfoDo.builder()
            .gameType(GameTypeEnum.MARIA_MAKILING)
            .row(3)
            .cell(5)
            .symbolMap(SYMBOL_MAP).build();
}
