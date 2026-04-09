package com.wegame.capi.biz.config.domain;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.module.sirena.SirenaGameHelper;
import com.wegame.capi.common.enums.SymbolEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.config.nacos.NacosConfigRefresherUtil.SIRENA_JSON_ROOT_NAME;
import static com.wegame.capi.common.Constants.CONST_100;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Component
@RefreshScope
@ConfigurationProperties(prefix = SIRENA_JSON_ROOT_NAME)
public class SirenaConfig extends GameBizConfig implements Serializable {
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
     * TreeMap<带名称(mode*),轮 List<reel(colum)List<(元素id)>>>
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
    /**
     * 大珍珠最小倍率
     */
    private BigDecimal minBigPearlMultiple;


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
        private TreeMap<Integer, BigDecimal> freeWheelSmallPearlCountWeightConfig;
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


    @Override
    public void bizValidate() {
        this.oddsConfigValidate();
        this.probabilisticConfigValidate();
        this.wheelChartValidate();
        this.freeWheelTimesValidate();
        this.rewardMaxMultipleValidate();
        this.rewardNotifyMinMultipleValidate();
    }

    private void rewardNotifyMinMultipleValidate() {
        AssertUtil.assertNotNull(this.rewardNotifyMinMultiple, ErrorCode.ERR_INTER, "rewardNotifyMinMultiple is null");
        AssertUtil.assertTrue(this.rewardNotifyMinMultiple > 0, ErrorCode.ERR_INTER, "rewardNotifyMinMultiple is must great than 0");
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
        AssertUtil.assertTrue(this.freeWheelTimesConfig.freeWheelStepForScatter1 > 0, ErrorCode.ERR_INTER, "freeWheelTimesConfig.freeWheelStepForScatter1 must great than 0");
        AssertUtil.assertTrue(this.freeWheelTimesConfig.maxFreeWheelTimes > 0, ErrorCode.ERR_INTER, "freeWheelTimesConfig.maxFreeWheelTimes must great than 0");
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
        // 统一校验wheelChart校验
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

        // 轮带表权重(正常游戏-normal bet 付费轮)
        this.normalBetWheelChartConfigValidate();
        // 轮带表权重(正常游戏-buy bet 付费轮.modeS)
        this.buyBetWheelChartConfigValidate();
        // 轮带表权重(免费游戏-free bet 免费轮)
        this.freeWheelWheelChartConfigValidate();
        // 轮带表权重(特殊游戏-上一次投注中珍珠之后的正常游戏-normal bet 付费轮)
        this.specialWheelChartConfigValidate();
    }

    private void specialWheelChartConfigValidate() {
        this.wheelChartCommonValidate(this.probabilisticConfig.specialWheelChatWeightConfig, "modeWheelChartList");
    }

    private void freeWheelWheelChartConfigValidate() {
        this.wheelChartCommonValidate(this.probabilisticConfig.freeWheelWheelChatWeightConfig, "freeWheelWheelChart");
    }

    private void buyBetWheelChartConfigValidate() {
        // 通用校验
        this.wheelChartCommonValidate(this.probabilisticConfig.buyBetWheelChatWeightConfig, "buyBetWheelChart");

        // 特殊校验
        List<String> availableModeNameList = this.buildAvailableModeNameList(this.probabilisticConfig.buyBetWheelChatWeightConfig);
        if (CollectionUtil.isEmpty(availableModeNameList)) {
            return;
        }
        availableModeNameList.forEach(modeName -> {
            List<List<String>> modeWheelChartList = wheelChartConfig.get(modeName);
            AssertUtil.assertNotEmpty(modeWheelChartList, ErrorCode.ERR_INTER, "buyBetWheelChart modeWheelChartList is empty of " + modeName);
            for (int reel = 0; reel < modeWheelChartList.size(); reel++) {
                List<String> reelWheelChart = modeWheelChartList.get(reel);
                // 校验2:2-4 reel 每隔4元素有且仅有1个scatter
                if (reel >= 1 && reel <= 3) {
                    // 先需要被5整除
                    int reelWheelChartSize = reelWheelChart.size();
                    AssertUtil.assertFalse(reelWheelChart.isEmpty() || reelWheelChartSize % 5 != 0, ErrorCode.ERR_INTER
                            , "reelWheelChartSize must more than 0 and Must be divided by 5 of mode " + modeName + " of reel " + (reel + 1));
                    for (int i = 0; i < reelWheelChartSize; i++) {
                        // 滑动窗口
                        int scatterCount = 0;
                        for (int start = i; start <= i + 4; start++) {
                            int currentIndex = start % reelWheelChartSize;
                            String currentSymbolId = reelWheelChart.get(currentIndex);
                            if (Objects.equals(currentSymbolId, SymbolEnum.Scatter.getId())) scatterCount++;
                        }
                        AssertUtil.assertTrue(scatterCount == 1, ErrorCode.ERR_INTER
                                , "reelWheelChart must has 1 scatter in per 5 symbol  in mode " + modeName + " of reel " + (reel + 1));
                    }
                }
            }
        });
    }

    private void normalBetWheelChartConfigValidate() {
        // 根据正常投注轮代表权重不为0的所有mode
        this.wheelChartCommonValidate(this.probabilisticConfig.normalBetWheelChatWeightConfig, "normalBetWheelChart");
    }

    private void wheelChartCommonValidate(TreeMap<String, BigDecimal> currentWheelChatWeightConfig, String configName) {
        List<String> availableModeNameList = this.buildAvailableModeNameList(currentWheelChatWeightConfig);
        if (CollectionUtil.isEmpty(availableModeNameList)) {
            return;
        }
        availableModeNameList.forEach(modeName -> {
            List<List<String>> modeWheelChartList = wheelChartConfig.get(modeName);
            AssertUtil.assertNotEmpty(modeWheelChartList, ErrorCode.ERR_INTER, configName + " modeWheelChartList is empty of " + modeName);
            modeWheelChartList.get(0).forEach(symbolId -> {
                // 第一个reel不能出现wild
                AssertUtil.assertTrue(!Objects.equals(symbolId, SymbolEnum.Wild.getId()),
                        ErrorCode.ERR_INTER, configName + " can not exist wild in reel 1 in mode " + modeName);
            });
        });
    }

    private List<String> buildAvailableModeNameList(TreeMap<String, BigDecimal> currentWheelChatWeightConfig) {
        return currentWheelChatWeightConfig.keySet().stream().filter(mode -> {
            BigDecimal weight = currentWheelChatWeightConfig.get(mode);
            return Objects.nonNull(weight) && weight.compareTo(BigDecimal.ZERO) != 0;
        }).collect(Collectors.toList());
    }


}
