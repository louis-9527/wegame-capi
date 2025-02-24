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
import java.util.*;

import static com.wegame.capi.common.Constants.CONST_10000;
import static com.wegame.capi.common.Constants.CONST_1000000;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MariaMakilingConfig {

    /**
     * 赔率配置
     */
    private OddsConfig oddsConfig;
    /**
     * 概率配置
     */
    private ProbabilisticConfig probabilisticConfig;

    /**
     * 中奖最大倍数
     */
    private Integer rewardMaxMultiple;

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
         * 正常玩滚轮生成概率配置
         * TreeMap<元素ID，<列坐标，概率列表>>
         */
        private TreeMap<String, List<BigDecimal>> commonPlayWheelProbabilisticConfig;

        /**
         * 免费玩滚轮生成概率配置
         * TreeMap<元素ID，<列坐标，概率列表>>
         */
        private TreeMap<String, List<BigDecimal>> freePlayWheelProbabilisticConfig;
        /**
         * 免费轮获得次数概率配置
         */
        private TreeMap<Integer, Integer> freeWheelTimesProbabilisticConfig;
    }

    public void validate() {
        oddsConfigValidate();
        probabilisticConfigValidate();
        rewardMaxMultipleValidate();
    }

    private void rewardMaxMultipleValidate() {
        AssertUtil.assertNotNull(this.rewardMaxMultiple, ErrorCode.ERR_INTER, "rewardMaxMultiple is null");
        AssertUtil.assertTrue(this.rewardMaxMultiple > 0, ErrorCode.ERR_INTER, "rewardMaxMultiple is must great than 0");
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
            TreeMap<String, Integer> map = new TreeMap<>();
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
            TreeMap<String, Integer> map = new TreeMap<>();
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
