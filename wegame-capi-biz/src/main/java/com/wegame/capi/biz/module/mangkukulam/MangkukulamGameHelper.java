package com.wegame.capi.biz.module.mangkukulam;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import static com.wegame.capi.common.enums.SymbolEnum.*;

public class MangkukulamGameHelper {

    public static final SymbolDo MANGKUKULAM_SymbolL1 = SymbolDo.builder()
            .id(SymbolL1.getId())
            .name(SymbolL1.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MANGKUKULAM_SymbolL2 = SymbolDo.builder()
            .id(SymbolL2.getId())
            .name(SymbolL2.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MANGKUKULAM_SymbolL3 = SymbolDo.builder()
            .id(SymbolL3.getId())
            .name(SymbolL3.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MANGKUKULAM_SymbolL4 = SymbolDo.builder()
            .id(SymbolL4.getId())
            .name(SymbolL4.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MANGKUKULAM_SymbolL5 = SymbolDo.builder()
            .id(SymbolL5.getId())
            .name(SymbolL5.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MANGKUKULAM_SymbolH1 = SymbolDo.builder()
            .id(SymbolH1.getId())
            .name(SymbolH1.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MANGKUKULAM_SymbolH2 = SymbolDo.builder()
            .id(SymbolH2.getId())
            .name(SymbolH2.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MANGKUKULAM_SymbolH3 = SymbolDo.builder()
            .id(SymbolH3.getId())
            .name(SymbolH3.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    private static final SymbolDo MANGKUKULAM_SymbolH4 = SymbolDo.builder()
            .id(SymbolH4.getId())
            .name(SymbolH4.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();

    public static final SymbolDo MANGKUKULAM_SCATTER = SymbolDo.builder()
            .id(SymbolSC.getId())
            .name(SymbolSC.getName())
            .symbolType(SymbolTypeEnum.SCATTER)
            .build();

    public static final SymbolDo MANGKUKULAM_Wild = SymbolDo.builder()
            .id(SymbolWD.getId())
            .name(SymbolWD.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();

    public static final SymbolDo MANGKUKULAM_SPECIAL_WILD = SymbolDo.builder()
            .id(SymbolWD_E.getId())
            .name(SymbolWD_E.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();

    public static final SymbolDo MANGKUKULAM_SYMBOL_PLACEHOLDER = SymbolDo.builder()
            .id(Symbol1000.getId())
            .name(Symbol1000.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();

    public static final GameInfoDo MANGKUKULAM_GAME_INFO = GameInfoDo.builder()
            .id(GameTypeEnum.MANGKUKULAM.getId())
            .gameType(GameTypeEnum.MANGKUKULAM)
            .name(GameTypeEnum.MANGKUKULAM.getName())
            .row(5)
            .column(5)
            .symbolMap(new ImmutableMap.Builder<String, SymbolDo>()
                    .put(MANGKUKULAM_SYMBOL_PLACEHOLDER.getId(), MANGKUKULAM_SYMBOL_PLACEHOLDER)
                    .put(MANGKUKULAM_SymbolL1.getId(), MANGKUKULAM_SymbolL1)
                    .put(MANGKUKULAM_SymbolL2.getId(), MANGKUKULAM_SymbolL2)
                    .put(MANGKUKULAM_SymbolL3.getId(), MANGKUKULAM_SymbolL3)
                    .put(MANGKUKULAM_SymbolL4.getId(), MANGKUKULAM_SymbolL4)
                    .put(MANGKUKULAM_SymbolL5.getId(), MANGKUKULAM_SymbolL5)
                    .put(MANGKUKULAM_SymbolH1.getId(), MANGKUKULAM_SymbolH1)
                    .put(MANGKUKULAM_SymbolH2.getId(), MANGKUKULAM_SymbolH2)
                    .put(MANGKUKULAM_SymbolH3.getId(), MANGKUKULAM_SymbolH3)
                    .put(MANGKUKULAM_SymbolH4.getId(), MANGKUKULAM_SymbolH4)
                    .put(MANGKUKULAM_SCATTER.getId(), MANGKUKULAM_SCATTER)
                    .put(MANGKUKULAM_Wild.getId(), MANGKUKULAM_Wild)
                    .put(MANGKUKULAM_SPECIAL_WILD.getId(), MANGKUKULAM_SPECIAL_WILD)
                    .build())
            .build();
}
