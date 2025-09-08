package com.wegame.capi.biz.innerdos.context;

import com.wegame.capi.biz.domain.metrics.GameMetricsDo;
import com.wegame.capi.biz.domain.metrics.UserPlayMetricsDo;
import com.wegame.capi.biz.innerdos.config.InnerDosCondition;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.Map;

/**
 * 内部干预上下文(默认)
 */
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DefaultInnerDosContext implements InnerDosContext {
    /**
     * 模版权重配置
     */
    private DefaultTemplateRangeConfig defaultTemplateRangeConfig;
    /**
     * 内部干预条件
     * 1.常态条件配置
     * 2.常态条件配置
     * 3.rtp干预配置(不需要)
     */
    private InnerDosCondition innerDosCondition;
    /**
     * 用户指标
     */
    private UserPlayMetricsDo userPlayMetricsDo;
    /**
     * 大盘指标数据
     */
    private GameMetricsDo gameMetricsDo;

    @Override
    public UserPlayMetricsDo getUserPlayMetricsDo() {
        return this.userPlayMetricsDo;
    }

    @Override
    public GameMetricsDo getGameMetricsDo() {
        return this.gameMetricsDo;
    }

    @Override
    public Map<String, Integer> getTemplateRangeMap(Object... args) {
        String typeName = args[0].toString();
        String templateName = args[1].toString();
        return defaultTemplateRangeConfig.getTemplateRangeMap(typeName, templateName);
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class DefaultTemplateRangeConfig {
        /**
         * 类型映射模版
         * 支持两层映射类型配置
         * Map<String, Map<String, Map<String, Integer>>>
         * 如：
         * Map<96.5, Map<Normal, Map<1-10, 100>>>
         * Map<96.5, Map<ex, Map<1-10, 100>>>
         */
        private Map<String, Map<String, Map<String, Integer>>> templateRangeMap;

        public Map<String, Integer> getTemplateRangeMap(String typeName, String templateName) {
            return this.templateRangeMap.get(typeName).get(templateName);
        }
    }


}
