package com.wegame.capi.biz.domain.biz.symbol;

import com.wegame.capi.biz.module.kapre.KapreGameHelper;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Kapre游戏元素
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(of = {"id"}, callSuper = true)
public class KapreSymbolDo extends SymbolDo {
    private static final long serialVersionUID = -226654678653448L;
    /**
     * 元素赔率,现金symbol可使用
     */
    private BigDecimal symbolOdd;

    /**
     * 嵌套symbol
     * wild 参与现金中奖之后，有值
     */
    private KapreSymbolDo nestSymbol;

    public KapreSymbolDo copy() {
        return KapreSymbolDo.builder()
                .id(getId())
                .name(getName())
                .symbolOdd(getSymbolOdd())
                .symbolType(getSymbolType())
                .nestSymbol(Optional.ofNullable(getNestSymbol()).map(KapreSymbolDo::copy).orElse(null))
                .build();
    }

    public boolean isCashSymbol() {
        return StringUtils.equals(this.getId(), KapreGameHelper.KAPRE_CASH_SYMBOL.getId());
    }

    public static KapreSymbolDo buildFromSymbol(SymbolDo symbol, BigDecimal symbolMultiple, KapreSymbolDo nestSymbol) {
        return KapreSymbolDo.builder()
                .id(symbol.getId())
                .name(symbol.getName())
                .symbolType(symbol.getSymbolType())
                .symbolOdd(symbolMultiple)
                .nestSymbol(nestSymbol)
                .build();
    }
}

