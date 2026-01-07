package com.wegame.capi.biz.domain.biz.symbol;

import com.wegame.capi.biz.module.atlantis.AtlantisGameHelper;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Atlantis游戏元素
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(of = {"id"}, callSuper = true)
public class AtlantisSymbolDo extends SymbolDo {
    private static final long serialVersionUID = -226654678653448L;
    /**
     * 元素赔率,现金symbol可使用
     */
    private BigDecimal symbolOdd;

    /**
     * 嵌套symbol
     * wild 参与现金中奖之后，有值
     */
    private AtlantisSymbolDo nestSymbol;

    public AtlantisSymbolDo copy() {
        return AtlantisSymbolDo.builder()
                .id(getId())
                .name(getName())
                .symbolOdd(getSymbolOdd())
                .symbolType(getSymbolType())
                .nestSymbol(Optional.ofNullable(getNestSymbol()).map(AtlantisSymbolDo::copy).orElse(null))
                .build();
    }

    public boolean isCashSymbol() {
        return true;
    }

    public static AtlantisSymbolDo buildFromSymbol(SymbolDo symbol, BigDecimal symbolMultiple, AtlantisSymbolDo nestSymbol) {
        return AtlantisSymbolDo.builder()
                .id(symbol.getId())
                .name(symbol.getName())
                .symbolType(symbol.getSymbolType())
                .symbolOdd(symbolMultiple)
                .nestSymbol(nestSymbol)
                .build();
    }
}

