package com.wegame.capi.biz.module.bayanihan;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import static com.wegame.capi.common.enums.SymbolEnum.*;

public class BayanihanGameHelper {

    public static final SymbolDo BAYANIHAN_SymbolL1 = SymbolDo.builder()
            .id(SymbolL1.getId())
            .name(SymbolL1.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo BAYANIHAN_SymbolL2 = SymbolDo.builder()
            .id(SymbolL2.getId())
            .name(SymbolL2.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo BAYANIHAN_SymbolL3 = SymbolDo.builder()
            .id(SymbolL3.getId())
            .name(SymbolL3.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo BAYANIHAN_SymbolL4 = SymbolDo.builder()
            .id(SymbolL4.getId())
            .name(SymbolL4.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();

    public static final SymbolDo BAYANIHAN_SymbolH1 = SymbolDo.builder()
            .id(SymbolH1.getId())
            .name(SymbolH1.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo BAYANIHAN_SymbolH2 = SymbolDo.builder()
            .id(SymbolH2.getId())
            .name(SymbolH2.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo BAYANIHAN_SymbolH3 = SymbolDo.builder()
            .id(SymbolH3.getId())
            .name(SymbolH3.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo BAYANIHAN_SymbolH4 = SymbolDo.builder()
            .id(SymbolH4.getId())
            .name(SymbolH4.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo BAYANIHAN_SymbolH5 = SymbolDo.builder()
            .id(SymbolH5.getId())
            .name(SymbolH5.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();

    public static final SymbolDo BAYANIHAN_SCATTER = SymbolDo.builder()
            .id(SymbolSC.getId())
            .name(SymbolSC.getName())
            .symbolType(SymbolTypeEnum.SCATTER)
            .build();

    public static final SymbolDo BAYANIHAN_Wild = SymbolDo.builder()
            .id(SymbolWD.getId())
            .name(SymbolWD.getName())
            .symbolType(SymbolTypeEnum.WILD)
            .build();


    public static final GameInfoDo BAYANIHAN_GAME_INFO = GameInfoDo.builder()
            .id(GameTypeEnum.BAYANIHAN.getId())
            .gameType(GameTypeEnum.BAYANIHAN)
            .name(GameTypeEnum.BAYANIHAN.getName())
            .row(4)
            .column(5)
            .symbolMap(new ImmutableMap.Builder<String, SymbolDo>()
                    .put(BAYANIHAN_SymbolL1.getId(), BAYANIHAN_SymbolL1)
                    .put(BAYANIHAN_SymbolL2.getId(), BAYANIHAN_SymbolL2)
                    .put(BAYANIHAN_SymbolL3.getId(), BAYANIHAN_SymbolL3)
                    .put(BAYANIHAN_SymbolL4.getId(), BAYANIHAN_SymbolL4)
                    .put(BAYANIHAN_SymbolH1.getId(), BAYANIHAN_SymbolH1)
                    .put(BAYANIHAN_SymbolH2.getId(), BAYANIHAN_SymbolH2)
                    .put(BAYANIHAN_SymbolH3.getId(), BAYANIHAN_SymbolH3)
                    .put(BAYANIHAN_SymbolH4.getId(), BAYANIHAN_SymbolH4)
                    .put(BAYANIHAN_SymbolH5.getId(), BAYANIHAN_SymbolH5)
                    .put(BAYANIHAN_SCATTER.getId(), BAYANIHAN_SCATTER)
                    .put(BAYANIHAN_Wild.getId(), BAYANIHAN_Wild)
                    .build())
            .build();
}
