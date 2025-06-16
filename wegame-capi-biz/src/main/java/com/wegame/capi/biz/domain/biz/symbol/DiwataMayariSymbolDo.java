package com.wegame.capi.biz.domain.biz.symbol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wegame.capi.biz.module.diawatamayari.DiwataMayariGameHelper;
import com.wegame.capi.common.enums.SymbolSizeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

/**
 * DiwataMayari游戏元素
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(of = {"id"}, callSuper = true)
public class DiwataMayariSymbolDo extends SymbolDo {
    private static final long serialVersionUID = -2209824310848L;
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

    @JsonIgnore
    public boolean isPlaceholderSymbol() {
        return StringUtils.equals(getId(), DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER.getId());
    }

    public DiwataMayariSymbolDo copy() {
        return DiwataMayariSymbolDo.builder()
                .id(getId())
                .name(getName())
                .symbolType(getSymbolType())
                .symbolSize(getSymbolSize())
                .symbolGoldBorder(isSymbolGoldBorder())
                .symbolClearTimes(getSymbolClearTimes())
                .build();
    }

    public static DiwataMayariSymbolDo buildFromSymbol(SymbolDo symbol, SymbolSizeEnum symbolSize, boolean symbolGoldBorder) {
        return DiwataMayariSymbolDo.builder()
                .id(symbol.getId())
                .name(symbol.getName())
                .symbolType(symbol.getSymbolType())
                .symbolSize(symbolSize)
                .symbolGoldBorder(symbolGoldBorder)
                .symbolClearTimes(0)
                .build();
    }
}

