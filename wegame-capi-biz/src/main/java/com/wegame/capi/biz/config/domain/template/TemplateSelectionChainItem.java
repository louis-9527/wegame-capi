package com.wegame.capi.biz.config.domain.template;

import com.wegame.capi.common.enums.TemplateTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 模板选择链中的单个节点
 * <p>
 * priority 越小优先级越高；strategy 标识决策维度并持有解析逻辑；
 * mapping 定义该维度的 key → 模板类型名称的映射表。
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateSelectionChainItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 优先级，数字越小越先执行 */
    private int priority;

    /** 决策策略 */
    private TemplateStrategyEnum strategy;

    /** key（enum name）→ 模板类型名称 的映射表 */
    private Map<String, TemplateTypeEnum> mapping;

    /**
     * 委托 strategy 解析当前 context，返回命中的模板名称
     *
     * @param ctx 本次投注的选择上下文
     * @return 命中的模板名称；未命中返回 null（交由下一优先级处理）
     */
    public TemplateTypeEnum resolve(TemplateSelectionContext ctx) {
        return strategy.resolve(ctx, mapping);
    }
}
