package com.wegame.capi.biz.module.malakas;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import static com.wegame.capi.common.enums.SymbolEnum.*;
import static com.wegame.capi.common.enums.SymbolSizeEnum.S;

public class MalakasGameHelper {

    public static final SymbolDo MALAKAS_Symbol1001 = SymbolDo.builder()
            .id(Symbol1001.getId())
            .name(Symbol1001.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MALAKAS_Symbol1002 = SymbolDo.builder()
            .id(Symbol1002.getId())
            .name(Symbol1002.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MALAKAS_Symbol1003 = SymbolDo.builder()
            .id(Symbol1003.getId())
            .name(Symbol1003.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MALAKAS_Symbol1004 = SymbolDo.builder()
            .id(Symbol1004.getId())
            .name(Symbol1004.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MALAKAS_Symbol1005 = SymbolDo.builder()
            .id(Symbol1005.getId())
            .name(Symbol1005.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MALAKAS_Symbol1006 = SymbolDo.builder()
            .id(Symbol1006.getId())
            .name(Symbol1006.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MALAKAS_Symbol1007 = SymbolDo.builder()
            .id(Symbol1007.getId())
            .name(Symbol1007.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MALAKAS_Symbol1008 = SymbolDo.builder()
            .id(Symbol1008.getId())
            .name(Symbol1008.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MALAKAS_Symbol1009 = SymbolDo.builder()
            .id(Symbol1009.getId())
            .name(Symbol1009.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MALAKAS_Symbol1010 = SymbolDo.builder()
            .id(Symbol1010.getId())
            .name(Symbol1010.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo MALAKAS_SCATTER = SymbolDo.builder()
            .id(Scatter.getId())
            .name(Scatter.getName())
            .symbolType(SymbolTypeEnum.SCATTER)
            .build();
    private static final SymbolDo MALAKAS_Wild = SymbolDo.builder()
            .id(Wild.getId())
            .name(Wild.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();
    public static final MalakasSymbolDo MALAKAS_SYMBOL_PLACEHOLDER = MalakasSymbolDo.builder()
            .id(Symbol1000.getId())
            .name(Symbol1000.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .symbolSize(S)
            .symbolGoldBorder(false)
            .build();
    public static final GameInfoDo MALAKAS_GAME_INFO = GameInfoDo.builder()
            .id(GameTypeEnum.MALAKAS.getId())
            .gameType(GameTypeEnum.MALAKAS)
            .name(GameTypeEnum.MALAKAS.getName())
            .row(6)
            .column(6)
            .symbolMap(new ImmutableMap.Builder<String, SymbolDo>()
                    .put(MALAKAS_Symbol1001.getId(), MALAKAS_Symbol1001)
                    .put(MALAKAS_Symbol1002.getId(), MALAKAS_Symbol1002)
                    .put(MALAKAS_Symbol1003.getId(), MALAKAS_Symbol1003)
                    .put(MALAKAS_Symbol1004.getId(), MALAKAS_Symbol1004)
                    .put(MALAKAS_Symbol1005.getId(), MALAKAS_Symbol1005)
                    .put(MALAKAS_Symbol1006.getId(), MALAKAS_Symbol1006)
                    .put(MALAKAS_Symbol1007.getId(), MALAKAS_Symbol1007)
                    .put(MALAKAS_Symbol1008.getId(), MALAKAS_Symbol1008)
                    .put(MALAKAS_Symbol1009.getId(), MALAKAS_Symbol1009)
                    .put(MALAKAS_Symbol1010.getId(), MALAKAS_Symbol1010)
                    .put(MALAKAS_SCATTER.getId(), MALAKAS_SCATTER)
                    .put(MALAKAS_Wild.getId(), MALAKAS_Wild)
                    .build())
            .build();
}
