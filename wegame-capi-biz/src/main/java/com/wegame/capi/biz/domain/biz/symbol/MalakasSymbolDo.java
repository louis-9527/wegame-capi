package com.wegame.capi.biz.domain.biz.symbol;

import com.wegame.capi.common.enums.SymbolSizeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Malakas游戏元素
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = true)
public class MalakasSymbolDo extends SymbolDo {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 元素大小
     */
    private SymbolSizeEnum symbolSize;

    /**
     * 元素金边
     */
    private boolean symbolGoldBorder;

    /**
     * 元素剩余消除次数（只有wild元素会有消除次数）
     */
    private Integer symbolClearTimes;

    public MalakasSymbolDo copy() {
        return MalakasSymbolDo.builder()
                .id(getId())
                .name(getName())
                .symbolType(getSymbolType())
                .symbolSize(getSymbolSize())
                .symbolGoldBorder(isSymbolGoldBorder())
                .symbolClearTimes(getSymbolClearTimes())
                .build();
    }

    public static MalakasSymbolDo buildFromSymbol(SymbolDo symbol, SymbolSizeEnum symbolSize, boolean symbolGoldBorder) {
        return MalakasSymbolDo.builder()
                .id(symbol.getId())
                .name(symbol.getName())
                .symbolType(symbol.getSymbolType())
                .symbolSize(symbolSize)
                .symbolGoldBorder(symbolGoldBorder)
                .symbolClearTimes(0)
                .build();
    }
}

