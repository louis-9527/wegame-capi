package com.wegame.capi.biz.config.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class KapreConfig {
    /**
     * 中奖通知最小倍数
     */
    private Integer rewardNotifyMinMultiple;
    /**
     * 中奖最大倍数
     */
    private Integer rewardMaxMultiple;
    /**
     * 概率配置
     */
    private ProbabilisticConfig probabilisticConfig;
    /**
     * 轮带表配置
     */
    private KapreConfig.WheelChart wheelChart;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProbabilisticConfig implements Serializable {
        private static final long serialVersionUID = -3127327864121298415L;
        /**
         * 轮带表概率配置
         */
        private WheelChartProbabilisticConfig wheelChartProbabilisticConfig;
        /**
         * 倍率概率配置
         */
        private MultipleProbabilisticConfig multipleProbabilisticConfig;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WheelChartProbabilisticConfig implements Serializable {
        private static final long serialVersionUID = -1979032185209671306L;
        /**
         * 免费轮轮带表概率
         */
        private Map<String, BigDecimal> freeProbabilisticConfig;
        /**
         * 付费轮轮带表概率
         */
        private Map<String, BigDecimal> payProbabilisticConfig;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MultipleProbabilisticConfig implements Serializable {
        private static final long serialVersionUID = -1979032185209671306L;
        /**
         * 免费轮轮带表倍率概率
         */
        private TreeMap<BigDecimal, BigDecimal> freeProbabilisticConfig;
        /**
         * 付费轮轮带表倍率概率
         */
        private TreeMap<BigDecimal, BigDecimal> payProbabilisticConfig;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WheelChart implements Serializable {
        private static final long serialVersionUID = -4274036157496212845L;
        private Map<String, List<List<String>>> normal;
        private Map<String, List<List<String>>> buy;
    }
}
