package com.wegame.capi.biz.module.atlantis;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import static com.wegame.capi.common.enums.SymbolEnum.*;

public class AtlantisGameHelper {

    public static final SymbolDo ATLANTIS_Symbol1001 = SymbolDo.builder()
            .id(Symbol1001.getId())
            .name(Symbol1001.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo ATLANTIS_Symbol1002 = SymbolDo.builder()
            .id(Symbol1002.getId())
            .name(Symbol1002.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo ATLANTIS_Symbol1003 = SymbolDo.builder()
            .id(Symbol1003.getId())
            .name(Symbol1003.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo ATLANTIS_Symbol1004 = SymbolDo.builder()
            .id(Symbol1004.getId())
            .name(Symbol1004.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo ATLANTIS_Symbol1005 = SymbolDo.builder()
            .id(Symbol1005.getId())
            .name(Symbol1005.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo ATLANTIS_Symbol1006 = SymbolDo.builder()
            .id(Symbol1006.getId())
            .name(Symbol1006.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo ATLANTIS_Symbol1007 = SymbolDo.builder()
            .id(Symbol1007.getId())
            .name(Symbol1007.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo ATLANTIS_Symbol1008 = SymbolDo.builder()
            .id(Symbol1008.getId())
            .name(Symbol1008.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo ATLANTIS_Symbol1009 = SymbolDo.builder()
            .id(Symbol1009.getId())
            .name(Symbol1009.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo ATLANTIS_Symbol1010 = SymbolDo.builder()
            .id(Symbol1010.getId())
            .name(Symbol1010.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo ATLANTIS_SCATTER = SymbolDo.builder()
            .id(Scatter.getId())
            .name(Scatter.getName())
            .symbolType(SymbolTypeEnum.SCATTER)
            .build();
    public static final SymbolDo ATLANTIS_Wild = SymbolDo.builder()
            .id(Wild.getId())
            .name(Wild.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();
    public static final GameInfoDo ATLANTIS_GAME_INFO = GameInfoDo.builder()
            .id(GameTypeEnum.ATLANTIS.getId())
            .gameType(GameTypeEnum.ATLANTIS)
            .name(GameTypeEnum.ATLANTIS.getName())
            .row(6)
            .column(5)
            .symbolMap(new ImmutableMap.Builder<String, SymbolDo>()
                    .put(ATLANTIS_Symbol1001.getId(), ATLANTIS_Symbol1001)
                    .put(ATLANTIS_Symbol1002.getId(), ATLANTIS_Symbol1002)
                    .put(ATLANTIS_Symbol1003.getId(), ATLANTIS_Symbol1003)
                    .put(ATLANTIS_Symbol1004.getId(), ATLANTIS_Symbol1004)
                    .put(ATLANTIS_Symbol1005.getId(), ATLANTIS_Symbol1005)
                    .put(ATLANTIS_Symbol1006.getId(), ATLANTIS_Symbol1006)
                    .put(ATLANTIS_Symbol1007.getId(), ATLANTIS_Symbol1007)
                    .put(ATLANTIS_Symbol1008.getId(), ATLANTIS_Symbol1008)
                    .put(ATLANTIS_Symbol1009.getId(), ATLANTIS_Symbol1009)
                    .put(ATLANTIS_Symbol1010.getId(), ATLANTIS_Symbol1010)
                    .put(ATLANTIS_SCATTER.getId(), ATLANTIS_SCATTER)
                    .put(ATLANTIS_Wild.getId(), ATLANTIS_Wild)
                    .build())
            .build();
}
