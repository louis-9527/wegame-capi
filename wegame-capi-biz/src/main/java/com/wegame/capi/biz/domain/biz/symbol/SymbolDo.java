package com.wegame.capi.biz.domain.biz.symbol;

import com.wegame.capi.common.enums.SymbolRuleTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

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
    /**
     * 元素规则：倍数（目前只支持）
     */
    private SymbolRuleDo symbolRule;

    /**
     * 元素规则
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class SymbolRuleDo implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * 规则类型
         */
        private SymbolRuleTypeEnum symbolRuleType;
        /**
         * 值
         */
        private BigDecimal value;
    }
}

