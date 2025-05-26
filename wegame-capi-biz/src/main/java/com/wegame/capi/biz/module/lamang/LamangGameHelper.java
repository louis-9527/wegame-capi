package com.wegame.capi.biz.module.lamang;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.symbol.LamangSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolSizeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import java.util.Arrays;
import java.util.List;

import static com.wegame.capi.common.enums.SymbolEnum.*;
import static com.wegame.capi.common.enums.SymbolSizeEnum.*;

public class LamangGameHelper {

    public static final SymbolDo LAMANG_Symbol1001 = SymbolDo.builder()
            .id(Symbol1001.getId())
            .name(Symbol1001.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo LAMANG_Symbol1002 = SymbolDo.builder()
            .id(Symbol1002.getId())
            .name(Symbol1002.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo LAMANG_Symbol1003 = SymbolDo.builder()
            .id(Symbol1003.getId())
            .name(Symbol1003.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo LAMANG_Symbol1004 = SymbolDo.builder()
            .id(Symbol1004.getId())
            .name(Symbol1004.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo LAMANG_Symbol1005 = SymbolDo.builder()
            .id(Symbol1005.getId())
            .name(Symbol1005.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo LAMANG_Symbol1006 = SymbolDo.builder()
            .id(Symbol1006.getId())
            .name(Symbol1006.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo LAMANG_Symbol1007 = SymbolDo.builder()
            .id(Symbol1007.getId())
            .name(Symbol1007.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo LAMANG_Symbol1008 = SymbolDo.builder()
            .id(Symbol1008.getId())
            .name(Symbol1008.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo LAMANG_Symbol1009 = SymbolDo.builder()
            .id(Symbol1009.getId())
            .name(Symbol1009.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo LAMANG_Symbol1010 = SymbolDo.builder()
            .id(Symbol1010.getId())
            .name(Symbol1010.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo LAMANG_SCATTER = SymbolDo.builder()
            .id(Scatter.getId())
            .name(Scatter.getName())
            .symbolType(SymbolTypeEnum.SCATTER)
            .build();

    public static final SymbolDo LAMANG_SCATTER1 = SymbolDo.builder()
            .id(Scatter1.getId())
            .name(Scatter1.getName())
            .symbolType(SymbolTypeEnum.SCATTER)
            .build();

    private static final SymbolDo LAMANG_Wild = SymbolDo.builder()
            .id(Wild.getId())
            .name(Wild.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();

    public static final SymbolDo LAMANG_BIG_WILD = SymbolDo.builder()
            .id(BigWild.getId())
            .name(BigWild.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();

    public static final SymbolDo LAMANG_SUPER_WILD = SymbolDo.builder()
            .id(SuperWild.getId())
            .name(SuperWild.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();

    // TODO 注意修改 @jayni
    public static final LamangSymbolDo LAMANG_SYMBOL_PLACEHOLDER = LamangSymbolDo.builder()
            .id(Symbol1000.getId())
            .name(Symbol1000.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .symbolGoldBorder(false)
            .build();

    public static final GameInfoDo LAMANG_GAME_INFO = GameInfoDo.builder()
            .id(GameTypeEnum.LAM_ANG.getId())
            .gameType(GameTypeEnum.LAM_ANG)
            .name(GameTypeEnum.LAM_ANG.getName())
            .row(4)
            .column(5)
            .symbolMap(new ImmutableMap.Builder<String, SymbolDo>()
                    .put(LAMANG_Symbol1001.getId(), LAMANG_Symbol1001)
                    .put(LAMANG_Symbol1002.getId(), LAMANG_Symbol1002)
                    .put(LAMANG_Symbol1003.getId(), LAMANG_Symbol1003)
                    .put(LAMANG_Symbol1004.getId(), LAMANG_Symbol1004)
                    .put(LAMANG_Symbol1005.getId(), LAMANG_Symbol1005)
                    .put(LAMANG_Symbol1006.getId(), LAMANG_Symbol1006)
                    .put(LAMANG_Symbol1007.getId(), LAMANG_Symbol1007)
                    .put(LAMANG_Symbol1008.getId(), LAMANG_Symbol1008)
                    .put(LAMANG_Symbol1009.getId(), LAMANG_Symbol1009)
                    .put(LAMANG_Symbol1010.getId(), LAMANG_Symbol1010)
                    .put(LAMANG_SCATTER.getId(), LAMANG_SCATTER)
                    .put(LAMANG_SCATTER1.getId(), LAMANG_SCATTER1)
                    .put(LAMANG_Wild.getId(), LAMANG_Wild)
                    .put(LAMANG_BIG_WILD.getId(), LAMANG_BIG_WILD)
                    .put(LAMANG_SUPER_WILD.getId(), LAMANG_SUPER_WILD)
                    .build())
            .build();



}
