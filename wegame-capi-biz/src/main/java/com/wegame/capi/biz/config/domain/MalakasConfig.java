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
public class MalakasConfig {

    private final static int REEL_NUMBERS = 7;

    /**
     * 赔率配置
     */
    private OddsConfig oddsConfig;
    /**
     * 概率配置
     */
    private ProbabilisticConfig probabilisticConfig;
    /**
     * Scatter个数对应的免费轮次数
     */
    private Map<Integer, Integer> scatterFreeWheelTimesConfig;


    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OddsConfig implements Serializable {
        /**
         * Map<元素ID，<赔率类型，具体赔率>>
         */
        private Map<String, Map<String, BigDecimal>> commonOddsConfig;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProbabilisticConfig implements Serializable {
        /**
         * 正常玩滚轮生成概率配置
         * Map<元素ID，<列坐标，概率列表>>
         */
        private Map<String, List<BigDecimal>> commonPlayWheelProbabilisticConfig;
        /**
         * 免费玩滚轮生成概率配置
         * Map<元素ID，<列坐标，概率列表>>
         */
        private Map<String, List<BigDecimal>> freePlayWheelProbabilisticConfig;
        /**
         * 正常玩儿元素大小概率配置 S M L XL
         */
        private Map<String, List<BigDecimal>> commonPlaySymbolSizeProbabilisticConfig;
        /**
         * 免费轮元素大小概率配置 S M L XL
         */
        private Map<String, List<BigDecimal>> freeWheelSymbolSizeProbabilisticConfig;
        /**
         * 正常玩儿每个reel出现金边概率配置: eg：5%
         */
        private List<BigDecimal> commonPlayGoldBoarderProbabilisticConfig;
        /**
         * 免费轮每个reel出现金边概率配置: eg：5%
         */
        private List<BigDecimal> freeWheelGoldBoarderProbabilisticConfig;
        /**
         * 免费轮获得Scatter个数概率配置
         */
        private Map<Integer, Integer> freeWheelScatterProbabilisticConfig;
    }

    public void validate() {
        oddsConfigValidate();
        probabilisticConfigValidate();
        scatterFreeWheelTimesConfigValidate();
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
        Assert.notNull(probabilisticConfig.freePlayWheelProbabilisticConfig, "probabilisticConfig.freePlayWheelProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.freeWheelScatterProbabilisticConfig, "probabilisticConfig.freeWheelScatterProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.commonPlaySymbolSizeProbabilisticConfig, "probabilisticConfig.commonPlaySymbolSizeProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.freeWheelSymbolSizeProbabilisticConfig, "probabilisticConfig.freeWheelSymbolSizeProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.commonPlayGoldBoarderProbabilisticConfig, "probabilisticConfig.commonPlayGoldBoarderProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.freeWheelGoldBoarderProbabilisticConfig, "probabilisticConfig.freeWheelGoldBoarderProbabilisticConfig is null");

        commonPlayWheelProbabilisticConfigValid(REEL_NUMBERS);
        freeWheelScatterProbabilisticConfigValid();
        freePlayWheelProbabilisticConfigValid(REEL_NUMBERS);
        commonPlaySymbolSizeProbabilisticConfigValid(REEL_NUMBERS);
        freeWheelSymbolSizeProbabilisticConfigValid(REEL_NUMBERS);
        AssertUtil.assertTrue(Objects.equals(probabilisticConfig.getCommonPlayGoldBoarderProbabilisticConfig().size(), 7), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayGoldBoarderProbabilisticConfig size error");
        AssertUtil.assertTrue(Objects.equals(probabilisticConfig.getFreeWheelGoldBoarderProbabilisticConfig().size(), 7), ErrorCode.ERR_INTER, "probabilisticConfig.freeWheelGoldBoarderProbabilisticConfig size error");
    }
    private void scatterFreeWheelTimesConfigValidate() {
        // todo 校验scatter配置
        Assert.notNull(scatterFreeWheelTimesConfig, "scatterFreeWheelTimesConfig is null");
    }
    private void commonPlayWheelProbabilisticConfigValid(int column) {
        probabilisticConfig.commonPlayWheelProbabilisticConfig.forEach((key, value) -> {
            AssertUtil.assertNotEmpty(value, ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayWheelProbabilisticConfig key: " + key + " ERR");
            AssertUtil.assertTrue(Objects.equals(value.size(), column), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayWheelProbabilisticConfig: " + key + " ERR");
        });
        for (int columnIndex = 0; columnIndex < column; columnIndex++) {
            int columnNo = columnIndex;
            Map<String, Integer> map = new HashMap<>();
            probabilisticConfig.commonPlayWheelProbabilisticConfig.entrySet().forEach(item -> {
                int val = (item.getValue().get(columnNo).multiply(new BigDecimal(CONST_10000))).intValue();
                if (val > 0) {
                    map.put(item.getKey(), val);
                }
            });
            Integer sumProbabilistic = map.values().stream().reduce(0, Integer::sum);
            AssertUtil.assertTrue(Objects.equals(sumProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayWheelProbabilisticConfig ERR, columnNo:" + columnNo);
        }
    }
    private void freePlayWheelProbabilisticConfigValid(int column) {
        probabilisticConfig.freePlayWheelProbabilisticConfig.forEach((key, value) -> {
            AssertUtil.assertNotEmpty(value, ErrorCode.ERR_INTER, "probabilisticConfig.freePlayWheelProbabilisticConfig key: " + key + " ERR");
            AssertUtil.assertTrue(Objects.equals(value.size(), column), ErrorCode.ERR_INTER, "probabilisticConfig.freePlayWheelProbabilisticConfig: " + key + " ERR");
        });
        for (int columnIndex = 0; columnIndex < column; columnIndex++) {
            int columnNo = columnIndex;
            Map<String, Integer> map = new HashMap<>();
            probabilisticConfig.freePlayWheelProbabilisticConfig.entrySet().forEach(item -> {
                int val = (item.getValue().get(columnNo).multiply(new BigDecimal(CONST_10000))).intValue();
                if (val > 0) {
                    map.put(item.getKey(), val);
                }
            });
            Integer sumProbabilistic = map.values().stream().reduce(0, Integer::sum);
            AssertUtil.assertTrue(Objects.equals(sumProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.freePlayWheelProbabilisticConfig ERR, columnNo:" + columnNo);
        }
    }
    private void freeWheelScatterProbabilisticConfigValid() {
        // 校验-免费轮次数概率配置
        int sum = probabilisticConfig.freeWheelScatterProbabilisticConfig.values().stream().reduce(0, Integer::sum);
        AssertUtil.assertTrue(Objects.equals(sum, 100), ErrorCode.ERR_INTER, "probabilisticConfig.freeWheelTimesProbabilisticConfig ERR");
    }

    private void commonPlaySymbolSizeProbabilisticConfigValid(int column) {
        probabilisticConfig.commonPlaySymbolSizeProbabilisticConfig.forEach((key, value) -> {
            AssertUtil.assertNotEmpty(value, ErrorCode.ERR_INTER, "probabilisticConfig.commonPlaySymbolSizeProbabilisticConfigValid key: " + key + " ERR");
            AssertUtil.assertTrue(Objects.equals(value.size(), column), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlaySymbolSizeProbabilisticConfigValid: " + key + " ERR");
        });
        for (int columnIndex = 0; columnIndex < column; columnIndex++) {
            int columnNo = columnIndex;
            Map<String, Integer> map = new HashMap<>();
            probabilisticConfig.commonPlaySymbolSizeProbabilisticConfig.entrySet().forEach(item -> {
                int val = (item.getValue().get(columnNo).multiply(new BigDecimal(CONST_10000))).intValue();
                if (val > 0) {
                    map.put(item.getKey(), val);
                }
            });
            Integer sumProbabilistic = map.values().stream().reduce(0, Integer::sum);
            AssertUtil.assertTrue(Objects.equals(sumProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlaySymbolSizeProbabilisticConfigValid ERR, columnNo:" + columnNo);
        }
    }
    private void freeWheelSymbolSizeProbabilisticConfigValid(int column) {
        probabilisticConfig.freeWheelSymbolSizeProbabilisticConfig.forEach((key, value) -> {
            AssertUtil.assertNotEmpty(value, ErrorCode.ERR_INTER, "probabilisticConfig.freeWheelSymbolSizeProbabilisticConfig key: " + key + " ERR");
            AssertUtil.assertTrue(Objects.equals(value.size(), column), ErrorCode.ERR_INTER, "probabilisticConfig.freeWheelSymbolSizeProbabilisticConfig: " + key + " ERR");
        });
        for (int columnIndex = 0; columnIndex < column; columnIndex++) {
            int columnNo = columnIndex;
            Map<String, Integer> map = new HashMap<>();
            probabilisticConfig.freeWheelSymbolSizeProbabilisticConfig.entrySet().forEach(item -> {
                int val = (item.getValue().get(columnNo).multiply(new BigDecimal(CONST_10000))).intValue();
                if (val > 0) {
                    map.put(item.getKey(), val);
                }
            });
            Integer sumProbabilistic = map.values().stream().reduce(0, Integer::sum);
            AssertUtil.assertTrue(Objects.equals(sumProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.freeWheelSymbolSizeProbabilisticConfig ERR, columnNo:" + columnNo);
        }
    }
}
