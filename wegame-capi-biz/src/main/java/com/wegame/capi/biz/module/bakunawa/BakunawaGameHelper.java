package com.wegame.capi.biz.module.bakunawa;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import static com.wegame.capi.common.enums.SymbolEnum.*;

/**
 * BakunawaGameHelper
 */
public class BakunawaGameHelper {

    public static final SymbolDo BAKUNAWA_Symbol0 = SymbolDo.builder()
            .id(Symbol0.getId())
            .name(Symbol0.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo BAKUNAWA_Symbol1 = SymbolDo.builder()
            .id(Symbol1.getId())
            .name(Symbol1.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo BAKUNAWA_Symbol2 = SymbolDo.builder()
            .id(Symbol2.getId())
            .name(Symbol2.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo BAKUNAWA_Symbol3 = SymbolDo.builder()
            .id(Symbol3.getId())
            .name(Symbol3.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo BAKUNAWA_Symbol5 = SymbolDo.builder()
            .id(Symbol5.getId())
            .name(Symbol5.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo BAKUNAWA_Symbol10 = SymbolDo.builder()
            .id(Symbol10.getId())
            .name(Symbol10.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    public static final SymbolDo BAKUNAWA_SymbolNull = SymbolDo.builder()
            .id(SymbolNull.getId())
            .name(SymbolNull.getName())
            .symbolType(SymbolTypeEnum.COMMON)
            .build();
    
    public static final GameInfoDo BAKUNAWA_GAME_INFO = GameInfoDo.builder()
            .id(GameTypeEnum.BAKUNAWA.getId())
            .gameType(GameTypeEnum.BAKUNAWA)
            .row(1)
            .column(3)
            .symbolMap(new ImmutableMap.Builder<String, SymbolDo>()
                    .put(BAKUNAWA_Symbol0.getId(), BAKUNAWA_Symbol0)
                    .put(BAKUNAWA_Symbol1.getId(), BAKUNAWA_Symbol1)
                    .put(BAKUNAWA_Symbol5.getId(), BAKUNAWA_Symbol5)
                    .put(BAKUNAWA_Symbol10.getId(), BAKUNAWA_Symbol10)
                    .put(BAKUNAWA_SymbolNull.getId(), BAKUNAWA_SymbolNull)
                    .build())
            .build();
}
