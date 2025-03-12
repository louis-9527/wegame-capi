package com.wegame.capi.biz.module.ibongadarna;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import static com.wegame.capi.common.enums.SymbolEnum.*;

public class IbongAdarnaGameHelper {

    public static final SymbolDo IBONG_ADARNA_Symbol1001 = SymbolDo.builder()
            .id(Symbol1001.getId())
            .name(Symbol1001.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo IBONG_ADARNA_Symbol1002 = SymbolDo.builder()
            .id(Symbol1002.getId())
            .name(Symbol1002.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo IBONG_ADARNA_Symbol1003 = SymbolDo.builder()
            .id(Symbol1003.getId())
            .name(Symbol1003.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo IBONG_ADARNA_Symbol1004 = SymbolDo.builder()
            .id(Symbol1004.getId())
            .name(Symbol1004.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo IBONG_ADARNA_Symbol1005 = SymbolDo.builder()
            .id(Symbol1005.getId())
            .name(Symbol1005.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo IBONG_ADARNA_Symbol1006 = SymbolDo.builder()
            .id(Symbol1006.getId())
            .name(Symbol1006.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo IBONG_ADARNA_Symbol1007 = SymbolDo.builder()
            .id(Symbol1007.getId())
            .name(Symbol1007.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo IBONG_ADARNA_Wild = SymbolDo.builder()
            .id(Wild.getId())
            .name(Wild.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();
    public static final GameInfoDo IBONG_ADARNA_GAME_INFO = GameInfoDo.builder()
            .id(GameTypeEnum.IBONG_ADARNA.getId())
            .gameType(GameTypeEnum.IBONG_ADARNA)
            .row(3)
            .column(3)
            .symbolMap(new ImmutableMap.Builder<String, SymbolDo>()
                    .put(IBONG_ADARNA_Symbol1001.getId(), IBONG_ADARNA_Symbol1001)
                    .put(IBONG_ADARNA_Symbol1002.getId(), IBONG_ADARNA_Symbol1002)
                    .put(IBONG_ADARNA_Symbol1003.getId(), IBONG_ADARNA_Symbol1003)
                    .put(IBONG_ADARNA_Symbol1004.getId(), IBONG_ADARNA_Symbol1004)
                    .put(IBONG_ADARNA_Symbol1005.getId(), IBONG_ADARNA_Symbol1005)
                    .put(IBONG_ADARNA_Symbol1006.getId(), IBONG_ADARNA_Symbol1006)
                    .put(IBONG_ADARNA_Symbol1007.getId(), IBONG_ADARNA_Symbol1007)
                    .put(IBONG_ADARNA_Wild.getId(), IBONG_ADARNA_Wild)
                    .build())
            .build();
}
