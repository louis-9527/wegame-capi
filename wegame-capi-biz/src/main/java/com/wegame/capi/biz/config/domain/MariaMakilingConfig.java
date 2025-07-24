package com.wegame.capi.biz.config.domain;

import cn.hutool.core.lang.Assert;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.module.mariamakiling.MariaMakilingGameHelper;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.wegame.capi.common.Constants.*;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MariaMakilingConfig implements Serializable {

    private static final long serialVersionUID = -7867808743045979621L;
    /**
     * 中奖最大倍数
     */
    private Integer rewardMaxMultiple;
    /**
     * 赔率配置
     */
    private OddsConfig oddsConfig;
    /**
     * 赔率配置
     */
    private WeightConfig weightConfig;
    /**
     * 概率配置
     */
    private ProbabilisticConfig probabilisticConfig;
    /**
     * 模板配置
     */
    private Map<String, TemplateMultipleRange> templateRangeConfigMap;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class OddsConfig implements Serializable {
        private static final long serialVersionUID = 3937946953651929522L;
        /**
         * TreeMap<元素ID，<赔率类型，具体赔率>>
         */
        private TreeMap<String, TreeMap<String, BigDecimal>> commonOddsConfig;
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class WeightConfig implements Serializable {
        private static final long serialVersionUID = 8473517761146027610L;
        /**
         * 正常轮权重配置
         * TreeMap<配置名称，权重>
         */
        private TreeMap<String, Integer> commonWheelWeightConfig;
        /**
         * 免费轮权重配置
         * TreeMap<配置名称，权重>
         */
        private TreeMap<String, Integer> freeWheelWeightConfig;
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class ProbabilisticConfig implements Serializable {
        private static final long serialVersionUID = -5132363037160707448L;
        /**
         * 免费轮获得次数概率配置
         * TreeMap<次数，权重>
         */
        private TreeMap<Integer, Integer> freeWheelTimesProbabilisticConfig;
        /**
         * 滚轮概率配置
         * TreeMap<配置名称，<元素ID，<列坐标，概率列表>>>
         */
        private TreeMap<String, TreeMap<String, List<BigDecimal>>> wheelProbabilisticConfig;
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class TemplateMultipleRange implements Serializable {
        private static final long serialVersionUID = -734841249803450751L;
        /**
         * 倍数区间开始
         */
        private String multipleRangeStart;
        /**
         * 倍数区间结束
         */
        private String multipleRangeEnd;
    }

    public void validate() {
        rewardMaxMultipleValidate();
        oddsConfigValidate();
        weightConfigValidate();
        probabilisticConfigValidate();
        templateRangeConfigMapValidate();
    }

    private void rewardMaxMultipleValidate() {
        Assert.notNull(this.rewardMaxMultiple, "rewardMaxMultiple is null");
        Assert.isTrue(this.rewardMaxMultiple > 0, "rewardMaxMultiple is must great than 0");
    }

    private void oddsConfigValidate() {
        Assert.notNull(oddsConfig, "oddsConfig is null");
        Assert.notNull(oddsConfig.commonOddsConfig, "oddsConfig.commonOddsConfig is null");
        GameInfoDo gameInfoDo = MariaMakilingGameHelper.MARIA_MAKILING_GAME_INFO;
        // 校验-赔率配置
        oddsConfig.commonOddsConfig.keySet().forEach(symbolId -> {
            Assert.notEmpty(oddsConfig.commonOddsConfig.get(symbolId), "Odds Config Err");
            Assert.notNull(gameInfoDo.getSymbolById(symbolId), "symbolId is not exist: " + symbolId);
        });
    }

    private void weightConfigValidate() {
        Assert.notNull(weightConfig, "weightConfig is null");
        Assert.notEmpty(weightConfig.commonWheelWeightConfig, "weightConfig.commonWheelWeightConfig is null");
        Assert.notEmpty(weightConfig.freeWheelWeightConfig, "weightConfig.freeWheelWeightConfig is null");
        // 检验权重和为100
        int commonWheelWeightSum = weightConfig.commonWheelWeightConfig.values()
                .stream()
                .reduce(0, Integer::sum);
        Assert.isTrue(commonWheelWeightSum == CONST_100000, "weightConfig.commonWheelWeightConfig Err");
        int freeWheelWeightSum = weightConfig.freeWheelWeightConfig.values()
                .stream()
                .reduce(0, Integer::sum);
        Assert.isTrue(freeWheelWeightSum == CONST_100000, "weightConfig.freeWheelWeightConfig Err");
        GameInfoDo gameInfoDo = MariaMakilingGameHelper.MARIA_MAKILING_GAME_INFO;
        // 校验对应配置存在
        weightConfig.commonWheelWeightConfig.keySet().forEach(configName -> {
            TreeMap<String, List<BigDecimal>> configMap = probabilisticConfig.wheelProbabilisticConfig.get(configName);
            Assert.notNull(configMap, "common wheel probabilisticConfig is not exist: " + configName);
            configMap.keySet().forEach(symbolId -> {
                Assert.notNull(gameInfoDo.getSymbolById(symbolId), "symbolId is not exist: " + configName);
            });
        });
        weightConfig.freeWheelWeightConfig.keySet().forEach(configName -> {
            TreeMap<String, List<BigDecimal>> configMap = probabilisticConfig.wheelProbabilisticConfig.get(configName);
            Assert.notNull(configMap, "free wheel probabilisticConfig is not exist: " + configName);
            configMap.keySet().forEach(symbolId -> {
                Assert.notNull(gameInfoDo.getSymbolById(symbolId), "symbolId is not exist: " + configName);
            });
        });
    }

    private void probabilisticConfigValidate() {
        Assert.notNull(probabilisticConfig, "probabilisticConfig is null");
        Assert.notNull(probabilisticConfig.freeWheelTimesProbabilisticConfig, "probabilisticConfig.freeWheelTimesProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.wheelProbabilisticConfig, "probabilisticConfig.wheelProbabilisticConfig is null");

        // 校验-免费轮次数概率配置
        int sum = probabilisticConfig.freeWheelTimesProbabilisticConfig.values()
                .stream()
                .reduce(0, Integer::sum);
        Assert.isTrue(Objects.equals(sum, 100), "probabilisticConfig.freeWheelTimesProbabilisticConfig ERR");
        probabilisticConfig.wheelProbabilisticConfig.forEach((configName, config) -> {
            validSingleConfig(configName, config);
        });
    }

    private void templateRangeConfigMapValidate() {
        //todo mai dong 增加校验
    }

    public void validSingleConfig(String configName, TreeMap<String, List<BigDecimal>> config) {
        int column = 5;
        config.forEach((key, value) -> {
            Assert.notEmpty(value, "config : " + key + " ERR, value is empty: " + configName);
            Assert.isTrue(Objects.equals(value.size(), column), "config : " + key + " ERR: " + configName);
        });
        for (int columnIndex = 0; columnIndex < column; columnIndex++) {
            Integer columnNo = columnIndex;
            TreeMap<String, Integer> map = new TreeMap<>();
            config.entrySet().forEach(item -> {
                int val = (item.getValue().get(columnNo).multiply(new BigDecimal(CONST_10000))).intValue();
                if (val > 0) {
                    map.put(item.getKey(), val);
                }
            });
            Integer sumProbabilistic = map.values()
                    .stream()
                    .reduce(0, Integer::sum);
            Assert.isTrue(Objects.equals(sumProbabilistic, CONST_1000000), "config ERR, columNo: " + columnNo + ", configName: " + configName);
        }
    }

    public TreeMap<String, Integer> getWeightConfig(boolean isFreeWheel) {
        return isFreeWheel ? weightConfig.getFreeWheelWeightConfig() : weightConfig.getCommonWheelWeightConfig();
    }
}
