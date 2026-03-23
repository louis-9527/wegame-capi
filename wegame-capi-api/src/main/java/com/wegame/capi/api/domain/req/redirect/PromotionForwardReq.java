package com.wegame.capi.api.domain.req.redirect;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.wegame.capi.api.domain.req.BaseReq;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * Promotion 通用转发请求模型（内存态）：基础字段在 {@link BaseReq}，业务字段在 {@link #data}。
 * 发往第三方时由 {@link com.wegame.capi.api.promotion.RedirectApi} 合并为单层 JSON，勿依赖对本类直接做 Jackson 序列化。
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PromotionForwardReq extends BaseReq {
    private static final long serialVersionUID = 1L;
    @JsonUnwrapped
    private Map<String, Object> data;

    // 关键注解：Jackson 会自动将 Map 键值对平铺到 JSON 顶层

    public Map<String, Object> getData() {
        return this.data;
    }

    // 可选：便捷方法，用于添加业务字段
    public void addData(String key, Object value) {
        this.data.put(key, value);
    }
}
