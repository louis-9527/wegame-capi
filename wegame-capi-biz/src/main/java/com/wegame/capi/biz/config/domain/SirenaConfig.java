package com.wegame.capi.biz.config.domain;

import cn.hutool.core.lang.Assert;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.module.sirena.SirenaGameHelper;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import static com.wegame.capi.common.Constants.CONST_100;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SirenaConfig implements Serializable {
    private static final long serialVersionUID = 5555098765480050L;
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
     * TreeMap<轮带名称(mode*), List<reel(colum)List<(元素id)>>>
     */
    private TreeMap<String, List<List<String>>> wheelChartConfig;
    /**
     * 免费轮次数配置
     */
    private FreeWheelTimesConfig freeWheelTimesConfig;
    /**
     * 中奖最大倍数
     */
    private Integer rewardMaxMultiple;
    /**
     * 中奖通知最小倍数
     */
    private Integer rewardNotifyMinMultiple;


    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class FreeWheelTimesConfig implements Serializable {
        private static final long serialVersionUID = 86580654676439675L;
        /**
         * 基础免费轮次数配置
         */
        private Integer baseFreeWheelTimes;
        /**
         * scatter+1 免费轮获得次数步长
         */
        private Integer freeWheelStepForScatter1;
        /**
         * 最大免费轮次数限制
         */
        private Integer maxFreeWheelTimes;

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
         * 小珍珠倍率权重
         * TreeMap<倍率, 出现概率:之和=100>
         */
        private TreeMap<BigDecimal, BigDecimal> smallPearlMultipleWeightConfig;
        /**
         * 大珍珠倍率权重
         * TreeMap<倍率, 出现概率:之和=100>
         */
        private TreeMap<BigDecimal, BigDecimal> bigPearlMultipleWeightConfig;
        /**
         * 免费轮小珍珠出现数量概率
         * TreeMap<珍珠数量, 出现概率:之和=100>
         */
        private TreeMap<BigDecimal, BigDecimal> freeWheelSmallPearlCountWeightConfig;
        /**
         * 轮带表权重(正常游戏-normal bet 付费轮)
         * TreeMap<轮带名称(mode*), 出现概率:之和=100>
         */
        private TreeMap<String, BigDecimal> normalBetWheelChatWeightConfig;

        /**
         * 轮带表权重(正常游戏-buy bet 付费轮.modeS)
         * TreeMap<轮带名称(mode*), 出现概率:之和=100>
         */
        private TreeMap<String, BigDecimal> buyBetWheelChatWeightConfig;
        /**
         * 轮带表权重(免费游戏-free bet 免费轮)
         * TreeMap<轮带名称(mode*), 出现概率:之和=100>
         */
        private TreeMap<String, BigDecimal> freeWheelWheelChatWeightConfig;
        /**
         * 轮带表权重(特殊游戏-上一次投注中珍珠之后的正常游戏-normal bet 付费轮)
         * TreeMap<轮带名称(mode*), 出现概率:之和=100>
         */
        private TreeMap<String, BigDecimal> specialWheelChatWeightConfig;
    }


    public void validate() {
        this.oddsConfigValidate();
        this.probabilisticConfigValidate();
        this.wheelChartValidate();
        this.freeWheelTimesValidate();
        this.rewardMaxMultipleValidate();
        this.rewardNotifyMinMultipleValidate();
    }

    private void rewardNotifyMinMultipleValidate() {

    }

    private void rewardMaxMultipleValidate() {
        AssertUtil.assertNotNull(this.rewardMaxMultiple, ErrorCode.ERR_INTER, "rewardMaxMultiple is null");
        AssertUtil.assertTrue(this.rewardMaxMultiple > 0, ErrorCode.ERR_INTER, "rewardMaxMultiple is must great than 0");
    }

    private void freeWheelTimesValidate() {
        AssertUtil.assertNotNull(this.freeWheelTimesConfig, ErrorCode.ERR_INTER, "freeWheelTimesConfig is null");
        AssertUtil.assertNotNull(this.freeWheelTimesConfig.baseFreeWheelTimes, ErrorCode.ERR_INTER, "freeWheelTimesConfig.baseFreeWheelTimes is null");
        AssertUtil.assertNotNull(this.freeWheelTimesConfig.freeWheelStepForScatter1, ErrorCode.ERR_INTER, "freeWheelTimesConfig.freeWheelStepForScatter1 is null");
        AssertUtil.assertNotNull(this.freeWheelTimesConfig.maxFreeWheelTimes, ErrorCode.ERR_INTER, "freeWheelTimesConfig.maxFreeWheelTimes is null");
        AssertUtil.assertTrue(this.freeWheelTimesConfig.baseFreeWheelTimes > 0, ErrorCode.ERR_INTER, "freeWheelTimesConfig.baseFreeWheelTimes must great than 0");
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
        Assert.notNull(probabilisticConfig, "probabilisticConfig is null");
        Assert.notNull(probabilisticConfig.smallPearlMultipleWeightConfig, "probabilisticConfig#smallPearlMultipleWeightConfig is null");
        Assert.notNull(probabilisticConfig.bigPearlMultipleWeightConfig, "probabilisticConfig#bigPearlMultipleWeightConfig is null");
        Assert.notNull(probabilisticConfig.freeWheelSmallPearlCountWeightConfig, "probabilisticConfig#freeWheelSmallPearlCountWeightConfig is null");
        Assert.notNull(probabilisticConfig.normalBetWheelChatWeightConfig, "probabilisticConfig#normalBetWheelChatWeightConfig is null");
        Assert.notNull(probabilisticConfig.buyBetWheelChatWeightConfig, "probabilisticConfig#buyBetWheelChatWeightConfig is null");
        Assert.notNull(probabilisticConfig.freeWheelWheelChatWeightConfig, "probabilisticConfig#freeWheelWheelChatWeightConfig is null");
        Assert.notNull(probabilisticConfig.specialWheelChatWeightConfig, "probabilisticConfig#specialWheelChatWeightConfig is null");

        this.probabilisticConfig100PercentValidate(probabilisticConfig.smallPearlMultipleWeightConfig);
        this.probabilisticConfig100PercentValidate(probabilisticConfig.bigPearlMultipleWeightConfig);
        this.probabilisticConfig100PercentValidate(probabilisticConfig.freeWheelSmallPearlCountWeightConfig);
        this.probabilisticConfig100PercentValidate(probabilisticConfig.normalBetWheelChatWeightConfig);
        this.probabilisticConfig100PercentValidate(probabilisticConfig.buyBetWheelChatWeightConfig);
        this.probabilisticConfig100PercentValidate(probabilisticConfig.freeWheelWheelChatWeightConfig);
        this.probabilisticConfig100PercentValidate(probabilisticConfig.specialWheelChatWeightConfig);
    }

    private <T> void probabilisticConfig100PercentValidate(TreeMap<T, BigDecimal> sourceMap) {
        BigDecimal totalProbabilistic = sourceMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        AssertUtil.assertTrue(new BigDecimal(CONST_100).compareTo(totalProbabilistic) == 0, ErrorCode.ERR_INTER, "probabilisticConfig ERR, not 100 %");
    }

    //
    private void wheelChartValidate() {
        // wheelChart校验
        Assert.notNull(this.wheelChartConfig, "wheelChartConfig is null");
        GameInfoDo sirenaGameInfo = SirenaGameHelper.SIRENA_GAME_INFO;
        Set<String> symbolIdSet = sirenaGameInfo.getSymbolMap().keySet();
        wheelChartConfig.forEach((key, value) -> {
            value.forEach(list -> {
                list.forEach(symbolId -> {
                    AssertUtil.assertTrue(symbolIdSet.contains(symbolId), ErrorCode.ERR_INTER, "wheelChart#normal ERR");
                });
            });
        });

    }
}
