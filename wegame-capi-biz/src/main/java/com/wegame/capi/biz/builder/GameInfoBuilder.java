package com.wegame.capi.biz.builder;

import com.google.common.collect.Sets;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.capi.biz.domain.GameInfoDo;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.common.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolRuleTypeEnum;

import java.math.BigDecimal;

import static com.wegame.capi.common.enums.SymbolEnum.*;

public class GameInfoBuilder {

    private static final GameInfoDo MARIA_MAKILING_GAME_INFO = GameInfoDo.builder()
            .gameType(GameTypeEnum.MARIA_MAKILING)
            .row(3)
            .cell(3)
            .symbolSet(Sets.newHashSet(
                    SymbolDo.builder()
                            .id(Symbol1001.getId())
                            .name(Symbol1001.getName())
                            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
                            .build(),
                    SymbolDo.builder()
                            .id(Symbol1002.getId())
                            .name(Symbol1002.getName())
                            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
                            .build(),
                    SymbolDo.builder()
                            .id(Symbol1003.getId())
                            .name(Symbol1003.getName())
                            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
                            .build(),
                    SymbolDo.builder()
                            .id(Symbol1004.getId())
                            .name(Symbol1004.getName())
                            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
                            .build(),
                    SymbolDo.builder()
                            .id(Symbol1005.getId())
                            .name(Symbol1005.getName())
                            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
                            .build(),
                    SymbolDo.builder()
                            .id(Symbol2001.getId())
                            .name(Symbol2001.getName())
                            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
                            .build(),
                    SymbolDo.builder()
                            .id(Symbol2002.getId())
                            .name(Symbol2002.getName())
                            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
                            .build(),
                    SymbolDo.builder()
                            .id(Symbol2003.getId())
                            .name(Symbol2003.getName())
                            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
                            .build(),
                    SymbolDo.builder()
                            .id(Symbol2004.getId())
                            .name(Symbol2004.getName())
                            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
                            .build(),
                    SymbolDo.builder()
                            .id(Symbol2005.getId())
                            .name(Symbol2005.getName())
                            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
                            .build(),
                    SymbolDo.builder()
                            .id(Scatter.getId())
                            .name(Scatter.getName())
                            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.COMMON).value(null).build())
                            .build(),
                    SymbolDo.builder()
                            .id(Wild.getId())
                            .name(Wild.getName())
                            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.MULTIPLE).value(new BigDecimal(0)).build())
                            .build(),
                    SymbolDo.builder()
                            .id(WildX2.getId())
                            .name(WildX2.getName())
                            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.MULTIPLE).value(new BigDecimal(2)).build())
                            .build(),
                    SymbolDo.builder()
                            .id(WildX3.getId())
                            .name(WildX3.getName())
                            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.MULTIPLE).value(new BigDecimal(3)).build())
                            .build(),
                    SymbolDo.builder()
                            .id(WildX5.getId())
                            .name(WildX5.getName())
                            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.MULTIPLE).value(new BigDecimal(5)).build())
                            .build(),
                    SymbolDo.builder()
                            .id(WildX10.getId())
                            .name(WildX10.getName())
                            .symbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(SymbolRuleTypeEnum.MULTIPLE).value(new BigDecimal(10)).build())
                            .build()
            )).build();

    public static GameInfoDo build(GameTypeEnum gameType) {
        switch (gameType) {
            case MARIA_MAKILING:
                return MARIA_MAKILING_GAME_INFO;
            default:
                throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT");
        }
    }

}
