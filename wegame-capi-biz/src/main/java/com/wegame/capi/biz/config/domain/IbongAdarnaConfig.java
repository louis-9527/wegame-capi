package com.wegame.capi.biz.config.domain;

import cn.hutool.core.lang.Assert;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.common.enums.MultipleTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import static com.wegame.capi.common.Constants.*;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class IbongAdarnaConfig extends GameBizConfig {

    /**
     * 赔率配置
     */
    private OddsConfig oddsConfig;
    /**
     * 概率配置
     */
    private ProbabilisticConfig probabilisticConfig;

    /**
     * 固定线路配置
     * Map<线路号，坐标：行号 & 列号>
     */
    private TreeMap<Integer, List<List<Integer>>> rewardFixedLinesConfig;
    /**
     * 中奖通知最小倍数
     */
    private Integer rewardNotifyMinMultiple;

    /**
     * 模版配置
     */
    private TemplateRangeConfig templateRangeConfig;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OddsConfig implements Serializable {
        /**
         * Map<元素ID，具体赔率
         */
        private TreeMap<String, TreeMap<String, BigDecimal>> commonOddsConfig;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProbabilisticConfig implements Serializable {
        /**
         * 元素出现概率配置
         * Map<元素ID，<列坐标，概率列表>>
         */
        private TreeMap<String, List<BigDecimal>> commonPlayWheelProbabilisticConfig;

        /**
         * 元素分裂概率配置
         * Map<元素ID，<分裂类型：不分裂，两倍分裂,三倍分裂，概率>>
         */
        private TreeMap<String, TreeMap<String, BigDecimal>> symbolBreakUpProbabilisticConfig;
        /**
         * 倍率出现概率配置
         * Map<倍率，<倍率类型：普通，ex，概率列表>>
         */
        private TreeMap<BigDecimal, TreeMap<String, BigDecimal>> multiPleProbabilisticConfig;

        /**
         * 每一列重复出现元素个数概率配置
         * index:代表列号
         * Map<重复类型，概率>
         */
        private List<TreeMap<String, BigDecimal>> perColumSymbolRepeatProbabilisticConfig;
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class TemplateRangeConfig implements Serializable {
        private static final long serialVersionUID = -73484243536450751L;
        /**
         * Map<Normal, Map<1-10, 100>>
         * <li>Normal:{@link com.wegame.capi.client.enums.BetSubTypeEnum}</li>
         * <li>1-10:倍率范围</li>
         * <li>100:权重</li>
         */
        private LinkedHashMap<String, LinkedHashMap<String, Integer>> templateRangeConfigMap;
    }

    @Override
    public void bizValidate() {
        oddsConfigValidate();
        probabilisticConfigValidate();
        rewardFixedLinesConfigValidate();
        rewardNotifyMinMultipleValidate();
        templateRangeConfigValidate();
    }

    private void templateRangeConfigValidate() {
        AssertUtil.assertNotNull(this.templateRangeConfig, ErrorCode.ERR_PARAM, "templateRangeConfig is null");
        AssertUtil.assertNotNull(this.templateRangeConfig.templateRangeConfigMap, ErrorCode.ERR_PARAM, "templateRangeConfig.templateRangeConfigMap is null");
    }

    private void oddsConfigValidate() {
        Assert.notNull(oddsConfig, "oddsConfig is null");
        Assert.notNull(oddsConfig.commonOddsConfig, "oddsConfig.commonOddsConfig is null");
        // 校验-赔率配置
        oddsConfig.commonOddsConfig.keySet().forEach(symbolId -> {
            Map<String, BigDecimal> map = oddsConfig.commonOddsConfig.get(symbolId);
            AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER, "Odds Config Err");
        });
    }

    private void probabilisticConfigValidate() {
        Assert.notNull(probabilisticConfig, "probabilisticConfig is null");
        Assert.notNull(probabilisticConfig.commonPlayWheelProbabilisticConfig, "commonPlayWheelProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.symbolBreakUpProbabilisticConfig, "symbolBreakUpProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.multiPleProbabilisticConfig, "multiPleProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.perColumSymbolRepeatProbabilisticConfig, "preColumSymbolRepeatProbabilisticConfig is null");
        int colum = 3;
        probabilisticConfig.commonPlayWheelProbabilisticConfig.forEach((key, value) -> {
            AssertUtil.assertNotEmpty(value, ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayWheelProbabilisticConfig key: " + key + " ERR");
            AssertUtil.assertTrue(Objects.equals(value.size(), colum), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayWheelProbabilisticConfig: " + key + " ERR");
        });
        for (int columIndex = 0; columIndex < colum; columIndex++) {
            Integer columNo = columIndex;
            Map<String, Integer> map = new HashMap<>();
            probabilisticConfig.commonPlayWheelProbabilisticConfig.entrySet().forEach(item -> {
                int val = (item.getValue().get(columNo).multiply(new BigDecimal(CONST_10000))).intValue();
                if (val > 0) {
                    map.put(item.getKey(), val);
                }
            });
            Integer sumProbabilistic = map.values()
                    .stream()
                    .reduce(0, Integer::sum);
            AssertUtil.assertTrue(Objects.equals(sumProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayWheelProbabilisticConfig ERR, columNo:" + columNo);
        }

        ////
        probabilisticConfig.symbolBreakUpProbabilisticConfig.forEach((key, value) -> {
            AssertUtil.assertNotNull(value, ErrorCode.ERR_INTER, "probabilisticConfig.symbolBreakUpProbabilisticConfig key: " + key + " ERR");
        });
        for (int columIndex = 0; columIndex < colum; columIndex++) {
            Integer columNo = columIndex;
            Map<String, Integer> map = new HashMap<>();
            probabilisticConfig.symbolBreakUpProbabilisticConfig.entrySet().forEach(item -> {
                // 所有value相加为100
                BigDecimal reduce = item.getValue().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                AssertUtil.assertTrue(new BigDecimal(CONST_100).compareTo(reduce) == 0, ErrorCode.ERR_INTER, "probabilisticConfig.symbolBreakUpProbabilisticConfig ERR, columNo:" + columNo);
            });
        }

        List<BigDecimal> normalMultiPleList = Lists.newArrayList();
        List<BigDecimal> exMultiPleList = Lists.newArrayList();
        probabilisticConfig.multiPleProbabilisticConfig.forEach((key, value) -> {
            BigDecimal normalProbabilistic = value.get(MultipleTypeEnum.NORMAL.getCode());
            AssertUtil.assertNotNull(normalProbabilistic, ErrorCode.ERR_INTER, "multiPleProbabilisticConfig is error,no normal probabilistic");
            BigDecimal exProbabilistic = value.get(MultipleTypeEnum.EX.getCode());
            AssertUtil.assertNotNull(normalProbabilistic, ErrorCode.ERR_INTER, "exProbabilistic is error,no ex probabilistic");
            normalMultiPleList.add(normalProbabilistic);
            exMultiPleList.add(exProbabilistic);
        });

        BigDecimal normalMultiPleSum = normalMultiPleList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        AssertUtil.assertTrue(new BigDecimal(CONST_100).compareTo(normalMultiPleSum) == 0, ErrorCode.ERR_INTER, "probabilisticConfig.multiPleProbabilisticConfig ERR, not 100 %");
        BigDecimal exMultiPleSum = exMultiPleList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        AssertUtil.assertTrue(new BigDecimal(CONST_100).compareTo(exMultiPleSum) == 0, ErrorCode.ERR_INTER, "probabilisticConfig.multiPleProbabilisticConfig ERR, not 100 %");

        probabilisticConfig.perColumSymbolRepeatProbabilisticConfig.forEach(map -> {
            BigDecimal totalProbabilistic = map.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            AssertUtil.assertTrue(new BigDecimal(CONST_100).compareTo(totalProbabilistic) == 0, ErrorCode.ERR_INTER, "probabilisticConfig.perColumSymbolRepeatProbabilisticConfig ERR, not 100 %");
        });
    }

    private void rewardFixedLinesConfigValidate() {
        AssertUtil.assertNotNull(rewardFixedLinesConfig, ErrorCode.ERR_INTER, "oddsConfig is null");
        for (Map.Entry<Integer, List<List<Integer>>> integerListEntry : rewardFixedLinesConfig.entrySet()) {
            AssertUtil.assertNotEmpty(integerListEntry.getValue(), ErrorCode.ERR_INTER, "rewardFixedLinesConfig.get(" + integerListEntry.getKey() + ") is null");
            AssertUtil.assertTrue(integerListEntry.getValue().size() == 3, ErrorCode.ERR_INTER, "rewardFixedLinesConfig.get(" + integerListEntry.getKey() + ") length is not equals 3");
            integerListEntry.getValue().forEach(indexList -> {
                AssertUtil.assertNotEmpty(indexList, ErrorCode.ERR_INTER, "rewardFixedLinesConfig.get(" + integerListEntry.getKey() + ") index is null");
                AssertUtil.assertTrue(indexList.size() == 2, ErrorCode.ERR_INTER, "rewardFixedLinesConfig.get(" + integerListEntry.getKey() + ") index length is not equals 2");
            });
        }
    }

    private void rewardNotifyMinMultipleValidate() {
        AssertUtil.assertNotNull(rewardNotifyMinMultiple, ErrorCode.ERR_INTER, "rewardNotifyMinMultiple is null");
        AssertUtil.assertTrue(rewardNotifyMinMultiple > 0, ErrorCode.ERR_INTER, "rewardNotifyMinMultiple must great than 0");
    }
}
