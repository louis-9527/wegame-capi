package com.wegame.capi.biz.domain.biz.symbol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import static com.wegame.capi.biz.module.lamang.LamangGameHelper.LAMANG_SYMBOL_PLACEHOLDER;

/**
 * lamang游戏元素
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(of = {"id"}, callSuper = true)
public class LamangSymbolDo extends SymbolDo {
    private static final long serialVersionUID = -226862643663710848L;

    /**
     * 元素金边
     */
    private boolean symbolGoldBorder;

    /**
     * 是否是新元素
     */
    private boolean newSymbol;

    @JsonIgnore
    public boolean isPlaceholderSymbol() {
        return StringUtils.equals(getId(), LAMANG_SYMBOL_PLACEHOLDER.getId());
    }

    public LamangSymbolDo copy() {
        return LamangSymbolDo.builder()
                .id(getId())
                .name(getName())
                .symbolType(getSymbolType())
                .symbolGoldBorder(isSymbolGoldBorder())
                .newSymbol(false)
                .build();
    }

    public LamangSymbolDo allCopy() {
        return LamangSymbolDo.builder()
                .id(getId())
                .name(getName())
                .symbolType(getSymbolType())
                .symbolGoldBorder(isSymbolGoldBorder())
                .newSymbol(this.newSymbol)
                .build();
    }

    public static LamangSymbolDo buildFromSymbol(SymbolDo symbol, boolean symbolGoldBorder) {
        return LamangSymbolDo.builder()
                .id(symbol.getId())
                .name(symbol.getName())
                .symbolType(symbol.getSymbolType())
                .symbolGoldBorder(symbolGoldBorder)
                .newSymbol(true)
                .build();
    }
}

