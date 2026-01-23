package com.wegame.capi.biz.domain.biz.symbol;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Kapre游戏元素
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(of = {"id"}, callSuper = true)
public class BayanihanSymbolDo extends SymbolDo {
    private static final long serialVersionUID = 6540501277309074773L;
    /**
     * 元素赔率,现金symbol可使用
     */
    private boolean newSymbol;

    /**
     * 元素剩余消除次数（只有wild元素会有消除次数）
     */
    private Integer symbolClearTimes;

    public BayanihanSymbolDo copy() {
        return BayanihanSymbolDo.builder()
                .id(getId())
                .name(getName())
                .newSymbol(false)
                .symbolType(getSymbolType())
                .symbolClearTimes(getSymbolClearTimes())
                .build();
    }

    public static BayanihanSymbolDo buildFromSymbol(SymbolDo symbol) {
        return BayanihanSymbolDo.builder()
                .id(symbol.getId())
                .name(symbol.getName())
                .symbolType(symbol.getSymbolType())
                .newSymbol(true)
                .build();
    }
}

