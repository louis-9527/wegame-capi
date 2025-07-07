package com.wegame.capi.biz.module.mangkukulam;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import static com.wegame.capi.common.enums.SymbolEnum.*;

public class MangkukulamGameHelper {

    public static final SymbolDo MANGKUKULAM_Symbol1001 = SymbolDo.builder()
            .id(Symbol1001.getId())
            .name(Symbol1001.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MANGKUKULAM_Symbol1002 = SymbolDo.builder()
            .id(Symbol1002.getId())
            .name(Symbol1002.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MANGKUKULAM_Symbol1003 = SymbolDo.builder()
            .id(Symbol1003.getId())
            .name(Symbol1003.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MANGKUKULAM_Symbol1004 = SymbolDo.builder()
            .id(Symbol1004.getId())
            .name(Symbol1004.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MANGKUKULAM_Symbol1005 = SymbolDo.builder()
            .id(Symbol1005.getId())
            .name(Symbol1005.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MANGKUKULAM_Symbol1006 = SymbolDo.builder()
            .id(Symbol1006.getId())
            .name(Symbol1006.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MANGKUKULAM_Symbol1007 = SymbolDo.builder()
            .id(Symbol1007.getId())
            .name(Symbol1007.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MANGKUKULAM_Symbol1008 = SymbolDo.builder()
            .id(Symbol1008.getId())
            .name(Symbol1008.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo MANGKUKULAM_SCATTER = SymbolDo.builder()
            .id(Scatter.getId())
            .name(Scatter.getName())
            .symbolType(SymbolTypeEnum.SCATTER)
            .build();

    private static final SymbolDo MANGKUKULAM_Wild = SymbolDo.builder()
            .id(Wild.getId())
            .name(Wild.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();

    public static final SymbolDo MANGKUKULAM_SPECIAL_WILD = SymbolDo.builder()
            .id(SpecialWild.getId())
            .name(SpecialWild.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();

    public static final MangkukulamSymbolDo MANGKUKULAM_SYMBOL_PLACEHOLDER = MangkukulamSymbolDo.builder()
            .id(Symbol1000.getId())
            .name(Symbol1000.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();
    
    public static final GameInfoDo MANGKUKULAM_GAME_INFO = GameInfoDo.builder()
            .id(GameTypeEnum.MANGKUKULAM.getId())
            .gameType(GameTypeEnum.MANGKUKULAM)
            .name(GameTypeEnum.MANGKUKULAM.getName())
            .row(5)
            .column(5)
            .symbolMap(new ImmutableMap.Builder<String, SymbolDo>()
                    .put(MANGKUKULAM_SYMBOL_PLACEHOLDER.getId(), MANGKUKULAM_SYMBOL_PLACEHOLDER)
                    .put(MANGKUKULAM_Symbol1001.getId(), MANGKUKULAM_Symbol1001)
                    .put(MANGKUKULAM_Symbol1002.getId(), MANGKUKULAM_Symbol1002)
                    .put(MANGKUKULAM_Symbol1003.getId(), MANGKUKULAM_Symbol1003)
                    .put(MANGKUKULAM_Symbol1004.getId(), MANGKUKULAM_Symbol1004)
                    .put(MANGKUKULAM_Symbol1005.getId(), MANGKUKULAM_Symbol1005)
                    .put(MANGKUKULAM_Symbol1006.getId(), MANGKUKULAM_Symbol1006)
                    .put(MANGKUKULAM_Symbol1007.getId(), MANGKUKULAM_Symbol1007)
                    .put(MANGKUKULAM_Symbol1008.getId(), MANGKUKULAM_Symbol1008)
                    .put(MANGKUKULAM_SCATTER.getId(), MANGKUKULAM_SCATTER)
                    .put(MANGKUKULAM_Wild.getId(), MANGKUKULAM_Wild)
                    .put(MANGKUKULAM_SPECIAL_WILD.getId(), MANGKUKULAM_SPECIAL_WILD)
                    .build())
            .build();
}
