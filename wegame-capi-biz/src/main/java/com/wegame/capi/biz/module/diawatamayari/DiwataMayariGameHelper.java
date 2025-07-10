package com.wegame.capi.biz.module.diawatamayari;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import static com.wegame.capi.common.enums.SymbolEnum.*;

public class DiwataMayariGameHelper {


    public static final SymbolDo DIWATA_MAYARI_Symbol1001 = SymbolDo.builder()
            .id(Symbol1001.getId())
            .name(Symbol1001.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo DIWATA_MAYARI_Symbol1002 = SymbolDo.builder()
            .id(Symbol1002.getId())
            .name(Symbol1002.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo DIWATA_MAYARI_Symbol1003 = SymbolDo.builder()
            .id(Symbol1003.getId())
            .name(Symbol1003.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo DIWATA_MAYARI_Symbol1004 = SymbolDo.builder()
            .id(Symbol1004.getId())
            .name(Symbol1004.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo DIWATA_MAYARI_Symbol1005 = SymbolDo.builder()
            .id(Symbol1005.getId())
            .name(Symbol1005.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo DIWATA_MAYARI_Symbol1006 = SymbolDo.builder()
            .id(Symbol1006.getId())
            .name(Symbol1006.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo DIWATA_MAYARI_Symbol1007 = SymbolDo.builder()
            .id(Symbol1007.getId())
            .name(Symbol1007.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo DIWATA_MAYARI_Symbol1008 = SymbolDo.builder()
            .id(Symbol1008.getId())
            .name(Symbol1008.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo DIWATA_MAYARI_Symbol1009 = SymbolDo.builder()
            .id(Symbol1009.getId())
            .name(Symbol1009.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo DIWATA_MAYARI_Symbol1010 = SymbolDo.builder()
            .id(Symbol1010.getId())
            .name(Symbol1010.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo DIWATA_MAYARI_SCATTER = SymbolDo.builder()
            .id(Scatter.getId())
            .name(Scatter.getName())
            .symbolType(SymbolTypeEnum.SCATTER)
            .build();

    // 大宝剑
    public static final SymbolDo DIWATA_MAYARI_WILD_SWORD = SymbolDo.builder()
            .id(Wild_Sword.getId())
            .name(Wild_Sword.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();
    // 棒子
    public static final SymbolDo DIWATA_MAYARI_WILD_STICK = SymbolDo.builder()
            .id(Wild_Stick.getId())
            .name(Wild_Stick.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();
    public static final SymbolDo DIWATA_MAYARI_WILD_COVER = SymbolDo.builder()
            .id(Wild.getId())
            .name(Wild.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();

    public static final DiwataMayariSymbolDo DIWATA_MAYARI_SYMBOL_PLACEHOLDER = DiwataMayariSymbolDo.builder()
            .id(Symbol1000.getId())
            .name(Symbol1000.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();

    public static final GameInfoDo DIWATA_MAYARI_GAME_INFO = GameInfoDo.builder()
            .id(GameTypeEnum.DIWATA_MAYARI.getId())
            .gameType(GameTypeEnum.DIWATA_MAYARI)
            .name(GameTypeEnum.DIWATA_MAYARI.getName())
            .row(5)
            .column(6)
            .symbolMap(new ImmutableMap.Builder<String, SymbolDo>()
                    .put(DIWATA_MAYARI_Symbol1001.getId(), DIWATA_MAYARI_Symbol1001)
                    .put(DIWATA_MAYARI_Symbol1002.getId(), DIWATA_MAYARI_Symbol1002)
                    .put(DIWATA_MAYARI_Symbol1003.getId(), DIWATA_MAYARI_Symbol1003)
                    .put(DIWATA_MAYARI_Symbol1004.getId(), DIWATA_MAYARI_Symbol1004)
                    .put(DIWATA_MAYARI_Symbol1005.getId(), DIWATA_MAYARI_Symbol1005)
                    .put(DIWATA_MAYARI_Symbol1006.getId(), DIWATA_MAYARI_Symbol1006)
                    .put(DIWATA_MAYARI_Symbol1007.getId(), DIWATA_MAYARI_Symbol1007)
                    .put(DIWATA_MAYARI_Symbol1008.getId(), DIWATA_MAYARI_Symbol1008)
                    .put(DIWATA_MAYARI_Symbol1009.getId(), DIWATA_MAYARI_Symbol1009)
                    .put(DIWATA_MAYARI_Symbol1010.getId(), DIWATA_MAYARI_Symbol1010)
                    .put(DIWATA_MAYARI_SCATTER.getId(), DIWATA_MAYARI_SCATTER)
                    .put(DIWATA_MAYARI_WILD_SWORD.getId(), DIWATA_MAYARI_WILD_SWORD)
                    .put(DIWATA_MAYARI_WILD_STICK.getId(), DIWATA_MAYARI_WILD_STICK)
                    .build())
            .build();
}
