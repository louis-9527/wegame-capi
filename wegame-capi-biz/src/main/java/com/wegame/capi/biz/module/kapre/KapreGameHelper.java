package com.wegame.capi.biz.module.kapre;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import static com.wegame.capi.common.enums.SymbolEnum.*;

public class KapreGameHelper {

    public static final SymbolDo KAPRE_Symbol1001 = SymbolDo.builder()
            .id(Symbol1001.getId())
            .name(Symbol1001.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo KAPRE_Symbol1002 = SymbolDo.builder()
            .id(Symbol1002.getId())
            .name(Symbol1002.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo KAPRE_Symbol1003 = SymbolDo.builder()
            .id(Symbol1003.getId())
            .name(Symbol1003.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo KAPRE_Symbol1004 = SymbolDo.builder()
            .id(Symbol1004.getId())
            .name(Symbol1004.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo KAPRE_Symbol1005 = SymbolDo.builder()
            .id(Symbol1005.getId())
            .name(Symbol1005.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo KAPRE_Symbol1006 = SymbolDo.builder()
            .id(Symbol1006.getId())
            .name(Symbol1006.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo KAPRE_Symbol1007 = SymbolDo.builder()
            .id(Symbol1007.getId())
            .name(Symbol1007.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo KAPRE_Symbol1008 = SymbolDo.builder()
            .id(Symbol1008.getId())
            .name(Symbol1008.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo KAPRE_Symbol1009 = SymbolDo.builder()
            .id(Symbol1009.getId())
            .name(Symbol1009.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo KAPRE_CASH_SYMBOL = SymbolDo.builder()
            .id(Symbol1010.getId())
            .name(Symbol1010.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo KAPRE_SCATTER = SymbolDo.builder()
            .id(Scatter.getId())
            .name(Scatter.getName())
            .symbolType(SymbolTypeEnum.SCATTER)
            .build();
    private static final SymbolDo KAPRE_Wild = SymbolDo.builder()
            .id(Wild.getId())
            .name(Wild.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();
    public static final SymbolDo KAPRE_Dynamic_Wild = SymbolDo.builder()
            .id(DynamicWild.getId())
            .name(DynamicWild.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();
    public static final GameInfoDo KAPRE_GAME_INFO = GameInfoDo.builder()
            .id(GameTypeEnum.KAPRE.getId())
            .gameType(GameTypeEnum.KAPRE)
            .name(GameTypeEnum.KAPRE.getName())
            .row(4)
            .column(5)
            .symbolMap(new ImmutableMap.Builder<String, SymbolDo>()
                    .put(KAPRE_Symbol1001.getId(), KAPRE_Symbol1001)
                    .put(KAPRE_Symbol1002.getId(), KAPRE_Symbol1002)
                    .put(KAPRE_Symbol1003.getId(), KAPRE_Symbol1003)
                    .put(KAPRE_Symbol1004.getId(), KAPRE_Symbol1004)
                    .put(KAPRE_Symbol1005.getId(), KAPRE_Symbol1005)
                    .put(KAPRE_Symbol1006.getId(), KAPRE_Symbol1006)
                    .put(KAPRE_Symbol1007.getId(), KAPRE_Symbol1007)
                    .put(KAPRE_Symbol1008.getId(), KAPRE_Symbol1008)
                    .put(KAPRE_Symbol1009.getId(), KAPRE_Symbol1009)
                    .put(KAPRE_CASH_SYMBOL.getId(), KAPRE_CASH_SYMBOL)
                    .put(KAPRE_SCATTER.getId(), KAPRE_SCATTER)
                    .put(KAPRE_Wild.getId(), KAPRE_Wild)
                    .put(KAPRE_Dynamic_Wild.getId(), KAPRE_Dynamic_Wild)
                    .build())
            .build();
}
