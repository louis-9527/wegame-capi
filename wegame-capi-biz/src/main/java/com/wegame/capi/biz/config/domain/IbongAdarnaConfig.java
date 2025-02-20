package com.wegame.capi.biz.config.domain;

import cn.hutool.core.lang.Assert;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.wegame.capi.common.Constants.CONST_10000;
import static com.wegame.capi.common.Constants.CONST_1000000;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IbongAdarnaConfig {

    /**
     * 赔率配置
     */
    private OddsConfig oddsConfig;
    /**
     * 概率配置
     */
    private ProbabilisticConfig probabilisticConfig;


    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OddsConfig implements Serializable {
        /**
         * Map<元素ID，具体赔率
         */
        private Map<String, BigDecimal> commonOddsConfig;
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
        private Map<String, List<BigDecimal>> commonPlayWheelProbabilisticConfig;

        /**
         * 元素分裂概率配置
         * Map<元素ID，<分裂类型：不分裂，两倍分裂,三倍分裂，概率列表>>
         */
        private Map<String, Map<String, BigDecimal>> symbolBreakUpProbabilisticConfig;
        /**
         * 倍率出现概率配置
         * Map<倍率，<倍率类型：普通，ex，概率列表>>
         */
        private Map<Integer, Map<String, BigDecimal>> multiPleProbabilisticConfig;
    }

    /**
     * 固定线路配置
     * 一共五条线
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RewardFixedLinesConfig {
        // 坐标





    }


    public void validate() {
        oddsConfigValidate();
        probabilisticConfigValidate();
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
        Assert.notNull(probabilisticConfig.commonPlayWheelProbabilisticConfig, "probabilisticConfig is null");
        Assert.notNull(probabilisticConfig.freePlayWheelProbabilisticConfig, "probabilisticConfig is null");
        Assert.notNull(probabilisticConfig.freeWheelTimesProbabilisticConfig, "probabilisticConfig is null");
        // 校验-免费轮次数概率配置
        int sum = probabilisticConfig.freeWheelTimesProbabilisticConfig.values()
                .stream()
                .reduce(0, Integer::sum);
        AssertUtil.assertTrue(Objects.equals(sum, 100), ErrorCode.ERR_INTER, "probabilisticConfig.freeWheelTimesProbabilisticConfig ERR");
        int colum = 5;
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

        probabilisticConfig.freePlayWheelProbabilisticConfig.forEach((key, value) -> {
            AssertUtil.assertNotEmpty(value, ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayWheelProbabilisticConfig key: " + key + " ERR");
            AssertUtil.assertTrue(Objects.equals(value.size(), colum), ErrorCode.ERR_INTER, "probabilisticConfig.freePlayWheelProbabilisticConfig: " + key + " ERR");
        });
        for (int columIndex = 0; columIndex < colum; columIndex++) {
            Integer columNo = columIndex;
            Map<String, Integer> map = new HashMap<>();
            probabilisticConfig.freePlayWheelProbabilisticConfig.entrySet().forEach(item -> {
                int val = (item.getValue().get(columNo).multiply(new BigDecimal(CONST_10000))).intValue();
                if (val > 0) {
                    map.put(item.getKey(), val);
                }
            });
            Integer sumProbabilistic = map.values()
                    .stream()
                    .reduce(0, Integer::sum);
            AssertUtil.assertTrue(Objects.equals(sumProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.freePlayWheelProbabilisticConfig ERR, columNo:" + columNo);
        }
    }
}
