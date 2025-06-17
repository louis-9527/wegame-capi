package com.wegame.capi.biz.config.domain;

import cn.hutool.core.lang.Assert;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import static com.wegame.capi.common.Constants.CONST_10000;
import static com.wegame.capi.common.Constants.CONST_1000000;

/**
 * lamang 配置类
 * // todo jackpot jackpotTriggerBetAmountConfig 通过重写set方法的方式达到排序的目的
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LamangConfig {

    private final static int REEL_NUMBERS = 5;

    /**
     * 赔率配置
     */
    private OddsConfig oddsConfig;
    /**
     * 概率配置
     */
    private ProbabilisticConfig probabilisticConfig;
    /**
     * 倍率盘配置
     */
    private MultiPleConfig multiPleConfig;
    /**
     * 中奖最大倍数
     */
    private Integer rewardMaxMultiple;
    /**
     * 免费次数
     */
    private Integer freeWheelTimes;
    /**
     * jackpot 配置
     */
    private JackPotConfig jackPotConfig;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OddsConfig implements Serializable {
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
        /**
         * 正常轮元素出现概率
         */
        private PlayProbabilisticConfig commonPlaySymbolProbabilisticConfig;
        /**
         * 免费轮概率
         */
        private PlayProbabilisticConfig freePlaySymbolProbabilisticConfig;
        /**
         * 超大wild出现后元素刷新概率
         */
        private PlayProbabilisticConfig supperWildRefreshProbabilisticConfig;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayProbabilisticConfig implements Serializable {
        /**
         * 滚轮生成概率配置
         * TreeMap<元素ID，<列坐标，概率列表>>
         */
        private TreeMap<String, List<BigDecimal>> wheelProbabilisticConfig;
        /**
         * 每个滚轮生成金边概率
         * <列坐标，当前列生成金边概率>
         */
        private List<BigDecimal> wheelGoldProbabilisticConfig;
        /**
         * wild类型出现概率配置
         * TreeMap<元素id，概率>
         */
        private TreeMap<String, BigDecimal> wildTypeProbabilisticConfig;
    }


    /**
     * 倍率盘配置
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MultiPleConfig implements Serializable {
        /**
         * 普通轮倍率
         * [1.0,2.0,3.0,5.0]
         */
        private List<BigDecimal> commonPlayMultiConfigList;
        /**
         * 免费轮倍率配置
         * [2.0,4.0,6.0,10.0]
         */
        private List<BigDecimal> freePlayMultiConfigList;
    }


    //    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JackPotConfig implements Serializable {
        /**
         * 正常轮提成配置
         */
        @Getter
        @Setter
        private CommissionConfig commonCommissionConfig;
        /**
         * 购买轮提成配置
         */
        @Getter
        @Setter
        private CommissionConfig buyCommissionConfig;
        /**
         * 奖池中奖触发投注金额配置
         * TreeMap<满足条件最小投注金额(从大到小配置), TreeMap<奖池名称, 中奖概率>>
         * 为什么使用linkedHashMap？需要按照配置顺序进行赋值
         */
        @Getter
        private LinkedHashMap<Long, TreeMap<String, BigDecimal>> jackpotTriggerBetAmountConfig;
        /**
         * jackpot保底金额配置
         * TreeMap<奖池名称, 保底金额>
         */
        @Getter
        @Setter
        private TreeMap<String, Long> guaranteedAmount;

        public void setJackpotTriggerBetAmountConfig(LinkedHashMap<Long, TreeMap<String, BigDecimal>> jackpotTriggerBetAmountConfig) {
            // 对数据进行排序
            if (jackpotTriggerBetAmountConfig != null) {
                LinkedHashMap<Long, TreeMap<String, BigDecimal>> sortedMap = new LinkedHashMap<>();
                jackpotTriggerBetAmountConfig.entrySet()
                        .stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByKey()))
                        .forEachOrdered(e -> sortedMap.put(e.getKey(), e.getValue()));
                this.jackpotTriggerBetAmountConfig = sortedMap;
            }
        }
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommissionConfig implements Serializable {
        /**
         * 按照金额提成
         * TreeMap<投注金额, TreeMap<奖池名称,提成金额>>
         */
        private TreeMap<Long, TreeMap<String, Long>> amountCommissionConfig;
        /**
         * 按比例提成
         * TreeMap<奖池名称, 提成比例>
         */
        private TreeMap<String, BigDecimal> proportionCommissionConfig;
    }

    public void validate() {
        oddsConfigValidate();
        probabilisticConfigValidate();
        multiPleConfigValidate();
        rewardMaxMultipleValidate();
        jackPotConfigValidate();
        freeWheelTimesValidate();
    }

    private void jackPotConfigValidate() {
        AssertUtil.assertNotNull(jackPotConfig, ErrorCode.ERR_PARAM, "jackPotConfig is null");
        AssertUtil.assertNotNull(jackPotConfig.commonCommissionConfig, ErrorCode.ERR_PARAM, "commonCommissionConfig is null");
        AssertUtil.assertNotNull(jackPotConfig.buyCommissionConfig, ErrorCode.ERR_PARAM, "buyCommissionConfig is null");
        AssertUtil.assertNotNull(jackPotConfig.jackpotTriggerBetAmountConfig, ErrorCode.ERR_PARAM, "jackpotTriggerBetAmountConfig is null");
        AssertUtil.assertNotNull(jackPotConfig.guaranteedAmount, ErrorCode.ERR_PARAM, "guaranteedAmount is null");

        this.commissionConfigValidate(jackPotConfig.commonCommissionConfig);
        this.commissionConfigValidate(jackPotConfig.buyCommissionConfig);
        this.jackpotTriggerBetAmountConfigValidate();
        this.guaranteedAmountValidate();

    }

    private void guaranteedAmountValidate() {
        TreeMap<String, Long> config = jackPotConfig.guaranteedAmount;
        config.keySet().forEach(poolName -> {
            AssertUtil.assertNotNull(poolName, ErrorCode.ERR_PARAM, "proportionCommissionConfig validate error,poolName " + poolName + " is not exist");
        });
    }

    private void jackpotTriggerBetAmountConfigValidate() {
        LinkedHashMap<Long, TreeMap<String, BigDecimal>> config = jackPotConfig.jackpotTriggerBetAmountConfig;
        config.values().forEach(item -> {
            item.keySet().forEach(poolName -> {
                AssertUtil.assertNotNull(poolName, ErrorCode.ERR_PARAM, "jackpotTriggerBetAmountConfig validate error,poolName " + poolName + " is not exist");
            });
        });
    }

    private void commissionConfigValidate(CommissionConfig commonCommissionConfig) {
        AssertUtil.assertNotNull(commonCommissionConfig.amountCommissionConfig, ErrorCode.ERR_PARAM, "amountCommissionConfig is null");
        AssertUtil.assertNotNull(commonCommissionConfig.proportionCommissionConfig, ErrorCode.ERR_PARAM, "proportionCommissionConfig is null");
        // 按金额提成
        TreeMap<Long, TreeMap<String, Long>> amountCommissionConfig = commonCommissionConfig.amountCommissionConfig;
        // 确定所有奖池类型有数据
        amountCommissionConfig.values().forEach(item -> {
            item.keySet().forEach(poolName -> {
                AssertUtil.assertNotNull(poolName, ErrorCode.ERR_PARAM, "amountCommissionConfig validate error,poolName " + poolName + " is not exist");
            });
        });

        // 按比例提成
        TreeMap<String, BigDecimal> proportionCommissionConfig = commonCommissionConfig.proportionCommissionConfig;
        proportionCommissionConfig.keySet().forEach(poolName -> {
            AssertUtil.assertNotNull(poolName, ErrorCode.ERR_PARAM, "proportionCommissionConfig validate error, poolName " + poolName + " is not exist");
        });
    }

    private void multiPleConfigValidate() {
        AssertUtil.assertNotNull(multiPleConfig, ErrorCode.ERR_PARAM, "multiPleConfig is null");
        AssertUtil.assertNotEmpty(multiPleConfig.commonPlayMultiConfigList, ErrorCode.ERR_PARAM, "commonPlayMultiConfigList is empty");
        AssertUtil.assertNotEmpty(multiPleConfig.freePlayMultiConfigList, ErrorCode.ERR_PARAM, "freePlayMultiConfigList is empty");
    }

    private void rewardMaxMultipleValidate() {
        AssertUtil.assertNotNull(this.rewardMaxMultiple, ErrorCode.ERR_INTER, "rewardMaxMultiple is null");
        AssertUtil.assertTrue(this.rewardMaxMultiple > 0, ErrorCode.ERR_INTER, "rewardMaxMultiple is must great than 0");
    }

    private void freeWheelTimesValidate() {
        AssertUtil.assertNotNull(this.freeWheelTimes, ErrorCode.ERR_INTER, "freeWheelTimes is null");
        AssertUtil.assertTrue(this.freeWheelTimes > 0, ErrorCode.ERR_INTER, "freeWheelTimes is must great than 0");
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
        Assert.notNull(probabilisticConfig.commonPlaySymbolProbabilisticConfig, "probabilisticConfig.commonPlaySymbolProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.freePlaySymbolProbabilisticConfig, "probabilisticConfig.freePlaySymbolProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.supperWildRefreshProbabilisticConfig, "probabilisticConfig.supperWildRefreshProbabilisticConfig is null");
        this.playProbabilisticConfigValid(probabilisticConfig.commonPlaySymbolProbabilisticConfig);
        this.playProbabilisticConfigValid(probabilisticConfig.freePlaySymbolProbabilisticConfig);
        this.playProbabilisticConfigValid(probabilisticConfig.supperWildRefreshProbabilisticConfig);
    }

    private void wildTypeProbabilisticConfig(TreeMap<String, BigDecimal> wildTypeProbabilisticConfig) {
//        int sum = 0;
//        for (BigDecimal probability : wildTypeProbabilisticConfig.values()) {
//            int val = (probability.multiply(new BigDecimal(CONST_10000))).intValue();
//            sum += val;
//        }
//        AssertUtil.assertTrue(Objects.equals(sum, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.wildTypeProbabilisticConfig ERR");
    }

    private void playProbabilisticConfigValid(PlayProbabilisticConfig playProbabilisticConfig) {
        // wheelGoldProbabilisticConfig
        // 概率校验
        wheelProbabilisticConfigValid(REEL_NUMBERS, playProbabilisticConfig.getWheelProbabilisticConfig());
        wheelGoldProbabilisticConfig(playProbabilisticConfig.wheelGoldProbabilisticConfig);
        // wild 类型出现概率校验
        this.wildTypeProbabilisticConfig(playProbabilisticConfig.wildTypeProbabilisticConfig);
    }

    private void wheelProbabilisticConfigValid(int column, TreeMap<String, List<BigDecimal>> wheelProbabilisticConfig) {
        wheelProbabilisticConfig.forEach((key, value) -> {
            AssertUtil.assertNotEmpty(value, ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayWheelProbabilisticConfig key: " + key + " ERR");
            AssertUtil.assertTrue(Objects.equals(value.size(), column), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayWheelProbabilisticConfig: " + key + " ERR");
        });
        for (int columnIndex = 0; columnIndex < column; columnIndex++) {
            int columnNo = columnIndex;
            TreeMap<String, Integer> map = new TreeMap<>();
            wheelProbabilisticConfig.entrySet().forEach(item -> {
                int val = (item.getValue().get(columnNo).multiply(new BigDecimal(CONST_10000))).intValue();
                if (val > 0) {
                    map.put(item.getKey(), val);
                }
            });
            Integer sumProbabilistic = map.values().stream().reduce(0, Integer::sum);
            AssertUtil.assertTrue(Objects.equals(sumProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayWheelProbabilisticConfig ERR, columnNo:" + columnNo);
        }
    }

    private void wheelGoldProbabilisticConfig(List<BigDecimal> list) {
        AssertUtil.assertTrue(Objects.equals(list.size(), REEL_NUMBERS), ErrorCode.ERR_INTER, "probabilisticConfig.freeWheelGoldBoarderProbabilisticConfig size error");
    }
}
