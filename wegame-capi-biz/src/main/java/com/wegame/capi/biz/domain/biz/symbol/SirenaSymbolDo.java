package com.wegame.capi.biz.domain.biz.symbol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wegame.capi.biz.module.kapre.KapreGameHelper;
import com.wegame.capi.biz.module.lamang.LamangGameHelper;
import com.wegame.capi.biz.module.sirena.SirenaGameHelper;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import com.wegame.capi.biz.module.sirena.SirenaGameHelper;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Kapre游戏元素
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(of = {"id"}, callSuper = true)
public class SirenaSymbolDo extends SymbolDo {
    private static final long serialVersionUID = -2266546786534428L;
    /**
     * 元素赔率
     */
    private BigDecimal symbolOdd;

    /**
     * 嵌套symbol
     * wild 参与中奖之后，有值
     */
    private SirenaSymbolDo nestSymbol;

    public SirenaSymbolDo copy() {
        return SirenaSymbolDo.builder()
                .id(getId())
                .name(getName())
                .symbolType(getSymbolType())
                .build();
    }

    /**
     * 是否贝壳元素
     * @return
     */
    public boolean isConchSymbol() {
        return StringUtils.equals(this.getId(), SirenaGameHelper.SIRENA_Wild.getId());
    }

    /**
     * 是否scatter symbol
     *
     * @return true/false
     */
    @JsonIgnore
    public boolean isScatterIdSymbol() {
        return StringUtils.equals(this.getId(), SirenaGameHelper.SIRENA_SCATTER.getId());
    }

    /**
     * 是否scatter1 symbol
     *
     * @return true/false
     */
    @JsonIgnore
    public boolean isScatter1IdSymbol() {
        return StringUtils.equals(this.getId(), SirenaGameHelper.SIRENA_SCATTER1.getId());
    }

    /**
     * 是否贝壳元素+珍珠
     * @return
     */
    public boolean isConchAndPearlSymbol() {
        if(StringUtils.equals(this.getId(), SirenaGameHelper.SIRENA_Wild.getId())){
            if(this.nestSymbol !=null){
                return StringUtils.equals(this.nestSymbol.getId(), SirenaGameHelper.SIRENA_Pearl_Wild.getId());
            }
        }
        return false;
    }
    public static SirenaSymbolDo buildFromSymbol(SymbolDo symbol, BigDecimal symbolMultiple, SirenaSymbolDo nestSymbol) {
        return SirenaSymbolDo.builder()
                .id(symbol.getId())
                .name(symbol.getName())
                .symbolType(symbol.getSymbolType())
                .symbolOdd(symbolMultiple)
                .nestSymbol(nestSymbol)
                .build();
    }
    public static SirenaSymbolDo buildPearlWildSymbol(SymbolDo symbol, BigDecimal symbolMultiple) {
        return SirenaSymbolDo.builder()
                .id(symbol.getId())
                .name(symbol.getName())
                .symbolType(symbol.getSymbolType())
                .symbolOdd(symbolMultiple)
                .build();
    }

}

