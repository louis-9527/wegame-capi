package com.wegame.capi.biz.config.domain;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.nacos.NacosConfigRefresherUtil;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.module.kapre.KapreGameHelper;
import com.wegame.capi.common.utils.NacosKeyUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Component
@RefreshScope
@ConfigurationProperties(prefix = NacosConfigRefresherUtil.KAPRE_JSON_ROOT_NAME)
public class KapreConfig extends GameBizConfig implements Serializable {
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
    private KapreConfig.WheelChart wheelChart;
    /**
     * 模板配置
     */
    private TemplateRangeConfig templateRangeConfig;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class TemplateRangeConfig implements Serializable {
        private static final long serialVersionUID = -734841249803450751L;
        /**
         * 主游戏模板配置
         */
        private Map<String, Integer> mainGame;
        /**
         * 免费游戏模板配置
         */
        private Map<String, Integer> freeGame;
    }

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

        public void setFreeProbabilisticConfig(TreeMap<String, BigDecimal> freeProbabilisticConfig) {
            TreeMap<BigDecimal, BigDecimal> trueFreeProbabilisticConfig = new TreeMap<>();
            AssertUtil.assertNotNull(freeProbabilisticConfig, ErrorCode.ERR_INTER, "freeProbabilisticConfig is null");
            freeProbabilisticConfig.forEach((key, value) -> {
                trueFreeProbabilisticConfig.put(NacosKeyUtil.transformToDecimal( key), value);
            });

            this.freeProbabilisticConfig = trueFreeProbabilisticConfig;
        }

        public void setPayProbabilisticConfig(TreeMap<String, BigDecimal> payProbabilisticConfig) {
            TreeMap<BigDecimal, BigDecimal> truePayProbabilisticConfig = new TreeMap<>();
            AssertUtil.assertNotNull(payProbabilisticConfig, ErrorCode.ERR_INTER, "payProbabilisticConfig is null");
            payProbabilisticConfig.forEach((key, value) -> {
                truePayProbabilisticConfig.put(NacosKeyUtil.transformToDecimal( key), value);
            });
            this.payProbabilisticConfig = truePayProbabilisticConfig;
        }
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

    @Override
    public void bizValidate() {
        rewardMaxMultipleValidate();
        freeWheelTimesValidate();
        oddsConfigValidate();
        probabilisticConfigValidate();
        wheelChartValidate();
        templateRangeConfigValidate();
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

    private void templateRangeConfigValidate() {
        Assert.notNull(templateRangeConfig, "templateRangeConfig is null");
        Assert.notNull(templateRangeConfig.getMainGame(), "templateRangeConfig.mainGame is null");
        Assert.notNull(templateRangeConfig.getFreeGame(), "templateRangeConfig.freeGame is null");
        templateRangeConfig.getMainGame().keySet().forEach(templateMultipleRange -> {
            Assert.isTrue(templateMultipleRange.contains("_"), "templateRangeConfig.mainGame.key not contain '_'");
            String[] multipleRangeArr = templateMultipleRange.split("_");
            Assert.isTrue(multipleRangeArr.length == 2, "templateRangeConfig.mainGame.key length is not eqauls 2");
            Assert.isTrue(NumberUtil.isNumber(multipleRangeArr[0]), "templateRangeConfig.mainGame.key first number must be number");
            Assert.isTrue(NumberUtil.isNumber(multipleRangeArr[1]), "templateRangeConfig.mainGame.key first number must be number");
            Assert.isTrue(new BigDecimal(multipleRangeArr[0]).compareTo(new BigDecimal(multipleRangeArr[1])) <= 0, "templateRangeConfig.mainGame.key first number must be less than or equal to the second number");
        });
        templateRangeConfig.getFreeGame().keySet().forEach(templateMultipleRange -> {
            Assert.isTrue(templateMultipleRange.contains("_"), "templateRangeConfig.freeGame.key not contain '_'");
            String[] multipleRangeArr = templateMultipleRange.split("_");
            Assert.isTrue(multipleRangeArr.length == 2, "templateRangeConfig.freeGame.key length is not eqauls 2");
            Assert.isTrue(NumberUtil.isNumber(multipleRangeArr[0]), "templateRangeConfig.freeGame.key first number must be number");
            Assert.isTrue(NumberUtil.isNumber(multipleRangeArr[1]), "templateRangeConfig.freeGame.key first number must be number");
            Assert.isTrue(new BigDecimal(multipleRangeArr[0]).compareTo(new BigDecimal(multipleRangeArr[1])) <= 0, "templateRangeConfig.freeGame.key first number must be less than or equal to the second number");
        });
    }
}
