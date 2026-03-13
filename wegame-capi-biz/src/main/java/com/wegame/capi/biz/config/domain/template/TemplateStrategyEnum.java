package com.wegame.capi.biz.config.domain.template;

import com.wegame.capi.common.enums.TemplateTypeEnum;

import java.util.Map;
import java.util.Objects;

/**
 * 模板选择策略枚举
 * <p>
 * 每个枚举项代表一种决策维度，从 {@link TemplateSelectionContext} 中取出对应的 key，
 * 在 mapping 中查找目标模板名称。
 * <ul>
 *   <li>命中（非 null）→ 本层可以决定，返回模板名称字符串</li>
 *   <li>未命中（null）→ 本层无法决定，交由下一优先级处理</li>
 * </ul>
 * </p>
 */
public enum TemplateStrategyEnum {

    /** 按干预类型（promotionType）决定模板 */
    PROMOTION_TYPE {
        @Override
        public TemplateTypeEnum resolve(TemplateSelectionContext ctx, Map<String, TemplateTypeEnum> mapping) {
            if (Objects.isNull(ctx.getPromotionType())) return null;
            return mapping.get(ctx.getPromotionType().name());
        }
    },

    /** 按用户 AB 分组决定模板（通常作为兜底策略） */
    AB_TYPE {
        @Override
        public TemplateTypeEnum resolve(TemplateSelectionContext ctx, Map<String, TemplateTypeEnum> mapping) {
            if (Objects.isNull(ctx.getAbType())) return null;
            return mapping.get(ctx.getAbType().name());
        }
    };

    /**
     * 从 context 中提取 key，在 mapping 中查找对应的模板名称
     *
     * @param ctx     本次投注的选择上下文
     * @param mapping 该策略的 key → 模板名称 映射表
     * @return 命中的模板名称；未命中返回 null
     */
    public abstract TemplateTypeEnum resolve(TemplateSelectionContext ctx, Map<String, TemplateTypeEnum> mapping);
}
