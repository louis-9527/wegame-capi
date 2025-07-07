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
public class MangkukulamSymbolDo extends SymbolDo {
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

    /**
     * 复制元素，将newSymbol置为false
     * 新回合复制元素使用该方法
     *
     * @return LamangSymbolDo
     */
    @Override
    public MangkukulamSymbolDo copy() {
        return MangkukulamSymbolDo.builder()
                .id(getId())
                .name(getName())
                .symbolType(getSymbolType())
                .symbolGoldBorder(isSymbolGoldBorder())
                .newSymbol(false)
                .build();
    }

    /**
     * 复制元素，并且不改变newSymbol状态
     * eventPlay 从模版中复制元素时使用该方法
     *
     * @return LamangSymbolDo
     */
    public MangkukulamSymbolDo copyWithOriginalNewSymbol() {
        return MangkukulamSymbolDo.builder()
                .id(getId())
                .name(getName())
                .symbolType(getSymbolType())
                .symbolGoldBorder(isSymbolGoldBorder())
                .newSymbol(this.newSymbol)
                .build();
    }

    /**
     * 从symbol产生一个新元素
     *
     * @param symbol           元素
     * @param symbolGoldBorder 是否有金边
     * @return LamangSymbolDo
     */
    public static MangkukulamSymbolDo buildFromSymbol(SymbolDo symbol, boolean symbolGoldBorder) {
        return MangkukulamSymbolDo.builder()
                .id(symbol.getId())
                .name(symbol.getName())
                .symbolType(symbol.getSymbolType())
                .symbolGoldBorder(symbolGoldBorder)
                .newSymbol(true)
                .build();
    }
}

