package com.wegame.capi.biz.module.sirena;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.symbol.LamangSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SirenaSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import static com.wegame.capi.common.enums.SymbolEnum.*;

public class SirenaGameHelper {

    public static final SymbolDo SIRENA_Symbol1001 = SymbolDo.builder()
            .id(Symbol1001.getId())
            .name(Symbol1001.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo SIRENA_Symbol1002 = SymbolDo.builder()
            .id(Symbol1002.getId())
            .name(Symbol1002.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo SIRENA_Symbol1003 = SymbolDo.builder()
            .id(Symbol1003.getId())
            .name(Symbol1003.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo SIRENA_Symbol1004 = SymbolDo.builder()
            .id(Symbol1004.getId())
            .name(Symbol1004.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo SIRENA_Symbol1005 = SymbolDo.builder()
            .id(Symbol1005.getId())
            .name(Symbol1005.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo SIRENA_Symbol1006 = SymbolDo.builder()
            .id(Symbol1006.getId())
            .name(Symbol1006.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo SIRENA_Symbol1007 = SymbolDo.builder()
            .id(Symbol1007.getId())
            .name(Symbol1007.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo SIRENA_Symbol1008 = SymbolDo.builder()
            .id(Symbol1008.getId())
            .name(Symbol1008.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo SIRENA_Symbol1009 = SymbolDo.builder()
            .id(Symbol1009.getId())
            .name(Symbol1009.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();

    public static final SymbolDo SIRENA_SCATTER = SymbolDo.builder()
            .id(Scatter.getId())
            .name(Scatter.getName())
            .symbolType(SymbolTypeEnum.SCATTER)
            .build();
    public static final SymbolDo SIRENA_SCATTER1 = SymbolDo.builder()
            .id(Scatter1.getId())
            .name(Scatter1.getName())
            .symbolType(SymbolTypeEnum.SCATTER)
            .build();
    public static final SymbolDo SIRENA_Wild = SymbolDo.builder()
            .id(Wild.getId())
            .name(Wild.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();
    public static final SymbolDo SIRENA_Pearl_Wild = SymbolDo.builder()
            .id(PearlWild.getId())
            .name(PearlWild.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();
    public static final SymbolDo SIRENA_Big_Pearl_Wild = SymbolDo.builder()
            .id(BigPearlWild.getId())
            .name(BigPearlWild.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();

    public static final SirenaSymbolDo SIRENA_SYMBOL_PLACEHOLDER = SirenaSymbolDo.builder()
            .id(Symbol1000.getId())
            .name(Symbol1000.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();

    public static final GameInfoDo SIRENA_GAME_INFO = GameInfoDo.builder()
            .id(GameTypeEnum.SIRENA.getId())
            .gameType(GameTypeEnum.SIRENA)
            .name(GameTypeEnum.SIRENA.getName())
            .row(5)
            .column(5)
            .symbolMap(new ImmutableMap.Builder<String, SymbolDo>()
                    .put(SIRENA_Symbol1001.getId(), SIRENA_Symbol1001)
                    .put(SIRENA_Symbol1002.getId(), SIRENA_Symbol1002)
                    .put(SIRENA_Symbol1003.getId(), SIRENA_Symbol1003)
                    .put(SIRENA_Symbol1004.getId(), SIRENA_Symbol1004)
                    .put(SIRENA_Symbol1005.getId(), SIRENA_Symbol1005)
                    .put(SIRENA_Symbol1006.getId(), SIRENA_Symbol1006)
                    .put(SIRENA_Symbol1007.getId(), SIRENA_Symbol1007)
                    .put(SIRENA_Symbol1008.getId(), SIRENA_Symbol1008)
                    .put(SIRENA_Symbol1009.getId(), SIRENA_Symbol1009)
                    .put(SIRENA_SCATTER.getId(), SIRENA_SCATTER)
                    .put(SIRENA_SCATTER1.getId(), SIRENA_SCATTER1)
                    .put(SIRENA_Wild.getId(), SIRENA_Wild)
                    .put(SIRENA_Pearl_Wild.getId(), SIRENA_Pearl_Wild)
                    .put(SIRENA_Big_Pearl_Wild.getId(), SIRENA_Big_Pearl_Wild)
                    .build())
            .build();
}
