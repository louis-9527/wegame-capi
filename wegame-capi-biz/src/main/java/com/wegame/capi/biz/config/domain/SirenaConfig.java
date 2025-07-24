package com.wegame.capi.biz.config.domain;

import cn.hutool.core.lang.Assert;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.module.kapre.KapreGameHelper;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SirenaConfig implements Serializable {
    private static final long serialVersionUID = 5550628498801680050L;
    /**
     * 中奖最大倍数
     */
    private Integer rewardMaxMultiple;
    /**
     * 免费次数
     */
    private Integer freeWheelTimes;
    /**
     * 赔率配置
     */
    private OddsConfig oddsConfig;
    /**
     * 概率配置
     */
    private ProbabilisticConfig probabilisticConfig;
    /**
     * 轮带表配置
     */
    private SirenaConfig.WheelChart wheelChart;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class OddsConfig implements Serializable {
        private static final long serialVersionUID = 8658064994692389675L;
        /**
         * TreeMap<元素ID，<赔率类型，具体赔率>>
         */
        private TreeMap<String, TreeMap<String, BigDecimal>> commonOddsConfig;
    }

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
        private TreeMap<String, BigDecimal> freeProbabilisticConfig;
        /**
         * 付费轮轮带表概率
         */
        private TreeMap<String, BigDecimal> normalBetPayProbabilisticConfig;
        /**
         * 付费轮轮带表概率
         */
        private TreeMap<String, BigDecimal> buyBetPayProbabilisticConfig;
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
        private TreeMap<String, List<List<String>>> normal;
        private TreeMap<String, List<List<String>>> buy;
    }

    public void validate() {
        rewardMaxMultipleValidate();
        freeWheelTimesValidate();
        oddsConfigValidate();
        probabilisticConfigValidate();
        wheelChartValidate();
    }

    private void rewardMaxMultipleValidate() {
        AssertUtil.assertNotNull(this.rewardMaxMultiple, ErrorCode.ERR_INTER, "rewardMaxMultiple is null");
        AssertUtil.assertTrue(this.rewardMaxMultiple > 0, ErrorCode.ERR_INTER, "rewardMaxMultiple is must great than 0");
    }
    private void freeWheelTimesValidate() {
        AssertUtil.assertNotNull(freeWheelTimes, ErrorCode.ERR_INTER, "freeWheelTimes is null");
        AssertUtil.assertTrue(freeWheelTimes > 0, ErrorCode.ERR_INTER, "freeWheelTimes must great than 0");
    }
    private void oddsConfigValidate() {
        Assert.notNull(oddsConfig, "oddsConfig is null");
        Assert.notNull(oddsConfig.commonOddsConfig, "oddsConfig.commonOddsConfig is null");
        // 校验-赔率配置
        oddsConfig.commonOddsConfig.keySet().forEach(symbolId -> {
            TreeMap<String, BigDecimal> map = oddsConfig.commonOddsConfig.get(symbolId);
            AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER, "Odds Config Err");
        });
    }
    private void probabilisticConfigValidate() {
        // probabilisticConfig校验
        Assert.notNull(probabilisticConfig, "probabilisticConfig is null");
        Assert.notNull(probabilisticConfig.wheelChartProbabilisticConfig, "probabilisticConfig#wheelChartProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.multipleProbabilisticConfig, "probabilisticConfig#multipleProbabilisticConfig is null");
        // probabilisticConfig.wheelChartProbabilisticConfig校验极其子属性校验
        Assert.notNull(probabilisticConfig.wheelChartProbabilisticConfig.freeProbabilisticConfig, "probabilisticConfig#wheelChartProbabilisticConfig#freeProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.wheelChartProbabilisticConfig.normalBetPayProbabilisticConfig, "probabilisticConfig#wheelChartProbabilisticConfig#normalBetPayProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.wheelChartProbabilisticConfig.buyBetPayProbabilisticConfig, "probabilisticConfig#wheelChartProbabilisticConfig#buyBetPayProbabilisticConfig is null");
        BigDecimal freeProbabilisticConfigSum = probabilisticConfig.wheelChartProbabilisticConfig.freeProbabilisticConfig.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal normalBetPayProbabilisticConfigSum = probabilisticConfig.wheelChartProbabilisticConfig.normalBetPayProbabilisticConfig.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal buyBetPayProbabilisticConfigSum = probabilisticConfig.wheelChartProbabilisticConfig.buyBetPayProbabilisticConfig.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        AssertUtil.assertTrue(freeProbabilisticConfigSum.compareTo(new BigDecimal("100")) == 0, ErrorCode.ERR_INTER, "probabilisticConfig#wheelChartProbabilisticConfig#freeProbabilisticConfig ERR");
        AssertUtil.assertTrue(normalBetPayProbabilisticConfigSum.compareTo(new BigDecimal("100")) == 0, ErrorCode.ERR_INTER, "probabilisticConfig#wheelChartProbabilisticConfig#normalBetPayProbabilisticConfig ERR");
        AssertUtil.assertTrue(buyBetPayProbabilisticConfigSum.compareTo(new BigDecimal("100")) == 0, ErrorCode.ERR_INTER, "probabilisticConfig#wheelChartProbabilisticConfig#buyBetPayProbabilisticConfig ERR");

        // probabilisticConfig.multipleProbabilisticConfig校验极其子属性校验
        Assert.notNull(probabilisticConfig.multipleProbabilisticConfig, "probabilisticConfig#multipleProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.multipleProbabilisticConfig.freeProbabilisticConfig, "probabilisticConfig#multipleProbabilisticConfig#freeProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.multipleProbabilisticConfig.payProbabilisticConfig, "probabilisticConfig#multipleProbabilisticConfig#payProbabilisticConfig is null");
        BigDecimal freeProbabilisticConfigSum2 = probabilisticConfig.multipleProbabilisticConfig.freeProbabilisticConfig.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal payProbabilisticConfigSum = probabilisticConfig.multipleProbabilisticConfig.payProbabilisticConfig.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        AssertUtil.assertTrue(freeProbabilisticConfigSum2.compareTo(new BigDecimal("100")) == 0, ErrorCode.ERR_INTER, "probabilisticConfig#multipleProbabilisticConfig#freeProbabilisticConfig ERR");
        AssertUtil.assertTrue(payProbabilisticConfigSum.compareTo(new BigDecimal("100")) == 0, ErrorCode.ERR_INTER, "probabilisticConfig#multipleProbabilisticConfig#payProbabilisticConfig ERR");

    }

    private void wheelChartValidate() {
        // wheelChart校验
        Assert.notNull(wheelChart, "wheelChart is null");
        Assert.notNull(wheelChart.normal, "wheelChart#normal is null");
        Assert.notNull(wheelChart.buy, "wheelChart#buy is null");
        // wheelChart.normal校验

        GameInfoDo kapreGameInfo = KapreGameHelper.KAPRE_GAME_INFO;
        Set<String> symbolIdSet = kapreGameInfo.getSymbolMap().keySet();
        wheelChart.normal.forEach((key, value) -> {
            value.forEach(list -> {
                list.forEach(symbolId -> {
                    AssertUtil.assertTrue(symbolIdSet.contains(symbolId), ErrorCode.ERR_INTER, "wheelChart#normal ERR");
                });
            });
        });
        // wheelChart.buy校验
        wheelChart.buy.forEach((key, value) -> {
            value.forEach(list -> {
                list.forEach(symbolId -> {
                    AssertUtil.assertTrue(symbolIdSet.contains(symbolId), ErrorCode.ERR_INTER, "wheelChart#buy ERR");
                });
            });
        });
    }
}
