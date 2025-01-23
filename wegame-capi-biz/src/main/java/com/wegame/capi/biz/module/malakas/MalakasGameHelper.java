package com.wegame.capi.biz.module.malakas;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolRuleTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import java.math.BigDecimal;

import static com.wegame.capi.common.enums.SymbolEnum.*;

public class MalakasGameHelper {

    // todo

    public static final SymbolDo MALAKAS_Symbol1001 = SymbolDo.builder()
            .id(Symbol1001.getId())
            .name(Symbol1001.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MALAKAS_Symbol1002 = SymbolDo.builder()
            .id(Symbol1002.getId())
            .name(Symbol1002.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MALAKAS_Symbol1003 = SymbolDo.builder()
            .id(Symbol1003.getId())
            .name(Symbol1003.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MALAKAS_Symbol1004 = SymbolDo.builder()
            .id(Symbol1004.getId())
            .name(Symbol1004.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MALAKAS_Symbol1005 = SymbolDo.builder()
            .id(Symbol1005.getId())
            .name(Symbol1005.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MALAKAS_Symbol2001 = SymbolDo.builder()
            .id(Symbol2001.getId())
            .name(Symbol2001.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MALAKAS_Symbol2002 = SymbolDo.builder()
            .id(Symbol2002.getId())
            .name(Symbol2002.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MALAKAS_Symbol2003 = SymbolDo.builder()
            .id(Symbol2003.getId())
            .name(Symbol2003.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MALAKAS_Symbol2004 = SymbolDo.builder()
            .id(Symbol2004.getId())
            .name(Symbol2004.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    private static final SymbolDo MALAKAS_Symbol2005 = SymbolDo.builder()
            .id(Symbol2005.getId())
            .name(Symbol2005.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();
    public static final SymbolDo MALAKAS_SCATTER = SymbolDo.builder()
            .id(Scatter.getId())
            .name(Scatter.getName())
            .symbolType(SymbolTypeEnum.SCATTER)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
            .build();

    private static final SymbolDo MALAKAS_Wild = SymbolDo.builder()
            .id(Wild.getId())
            .name(Wild.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(null).build())
            .build();


    public static final GameInfoDo MALAKAS_GAME_INFO = GameInfoDo.builder()
            .id(GameTypeEnum.MALAKAS.getId())
            .gameType(GameTypeEnum.MALAKAS)
            .row(3)
            .column(5)
            .symbolMap(new ImmutableMap.Builder<String, SymbolDo>()
                    .put(MALAKAS_Symbol1001.getId(), MALAKAS_Symbol1001)
                    .put(MALAKAS_Symbol1002.getId(), MALAKAS_Symbol1002)
                    .put(MALAKAS_Symbol1003.getId(), MALAKAS_Symbol1003)
                    .put(MALAKAS_Symbol1004.getId(), MALAKAS_Symbol1004)
                    .put(MALAKAS_Symbol1005.getId(), MALAKAS_Symbol1005)
                    .put(MALAKAS_Symbol2001.getId(), MALAKAS_Symbol2001)
                    .put(MALAKAS_Symbol2002.getId(), MALAKAS_Symbol2002)
                    .put(MALAKAS_Symbol2003.getId(), MALAKAS_Symbol2003)
                    .put(MALAKAS_Symbol2004.getId(), MALAKAS_Symbol2004)
                    .put(MALAKAS_Symbol2005.getId(), MALAKAS_Symbol2005)
                    .put(MALAKAS_SCATTER.getId(), MALAKAS_SCATTER)
                    .put(MALAKAS_Wild.getId(), MALAKAS_Wild)
                    .build())
            .build();
}
