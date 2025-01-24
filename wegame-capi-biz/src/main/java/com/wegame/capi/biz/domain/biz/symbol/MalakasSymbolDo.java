package com.wegame.capi.biz.domain.biz.symbol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wegame.capi.common.enums.SymbolRuleTypeEnum;
import com.wegame.capi.common.enums.SymbolSizeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Malakas游戏元素
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class MalakasSymbolDo extends SymbolDo {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 元素大小
     */
    private SymbolSizeEnum symbolSize;

    public static MalakasSymbolDo buildFromSymbolDo(SymbolDo symbolDo, SymbolSizeEnum symbolSize) {
        return MalakasSymbolDo.builder()
                .id(symbolDo.getId())
                .name(symbolDo.getName())
                .symbolType(symbolDo.getSymbolType())
                .symbolRule(symbolDo.getSymbolRule())
                .symbolSize(symbolSize)
                .build();
    }
}

