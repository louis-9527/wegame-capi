package com.wegame.capi.biz.domain.biz.symbol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wegame.capi.biz.module.diawatamayari.DiwataMayariGameHelper;
import com.wegame.capi.common.enums.SymbolEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;


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
    private Boolean main;
    private static final long serialVersionUID = -226678093710839L;

    public static DiwataMayariSymbolDo buildFromSymbol(SymbolDo symbol) {
        return DiwataMayariSymbolDo.builder()
                .id(symbol.getId())
                .name(symbol.getName())
                .symbolType(symbol.getSymbolType())
                .build();
    }

    /**
     * 判断是否 大宝剑元素
     */
    @JsonIgnore
    public boolean isMainSymbol() {
        return Objects.nonNull(this.main) && this.main;
    }

    /**
     * 判断是否 大宝剑元素
     */
    @JsonIgnore
    public boolean isSword() {
        return StringUtils.equals(getId(), SymbolEnum.Wild_Sword.getId());
    }

    /**
     * 判断是否 棒元素
     */
    @JsonIgnore
    public boolean isStick() {
        return StringUtils.equals(getId(), SymbolEnum.Wild_Stick.getId());
    }

    @JsonIgnore
    public boolean isPlaceholderSymbol() {
        return StringUtils.equals(getId(), DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER.getId());
    }

    @JsonIgnore
    public DiwataMayariSymbolDo copy() {
        return DiwataMayariSymbolDo.builder()
                .id(getId())
                .name(getName())
                .symbolType(getSymbolType())
                .build();
    }
}

