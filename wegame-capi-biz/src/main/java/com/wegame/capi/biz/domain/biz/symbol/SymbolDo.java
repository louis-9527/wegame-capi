package com.wegame.capi.biz.domain.biz.symbol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wegame.capi.common.enums.SymbolRuleTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 游戏元素
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class SymbolDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 元素id
     */
    private String id;
    /**
     * 元素名称
     */
    private String name;
    /**
     * 元素类型：普通元素/特殊元素
     */
    private SymbolTypeEnum symbolType;

    @JsonIgnore
    public boolean isCommonSymbol() {
        return StringUtils.equals(symbolType.getCode(), SymbolTypeEnum.COMMON.getCode());
    }

    @JsonIgnore
    public boolean isScatterSymbol() {
        return StringUtils.equals(symbolType.getCode(), SymbolTypeEnum.SCATTER.getCode());
    }

    @JsonIgnore
    public boolean isWildSymbol() {
        return StringUtils.equals(symbolType.getCode(), SymbolTypeEnum.WILD.getCode());
    }

    public SymbolDo copy() {
        return SymbolDo.builder()
                .id(id)
                .name(name)
                .symbolType(symbolType)
                .build();
    }
}

