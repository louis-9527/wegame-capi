package com.wegame.capi.biz.config.domain;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.wegame.capi.common.Constants.CONST_10000;
import static com.wegame.capi.common.Constants.CONST_1000000;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MalakasConfig extends GameBizConfig implements Serializable {

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
    private TreeMap<Integer, Integer> scatterFreeWheelTimesConfig;
    /**
     * 中奖最大倍数
     */
    private Integer rewardMaxMultiple;
    /**
     * 模板配置
     */
    private MalakasConfig.TemplateRangeConfig templateRangeConfig;

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
         * 正常轮概率
         */
        private PlayProbabilisticConfig commonPlayProbabilisticConfig;
        /**
         * 免费轮概率
         */
        private PlayProbabilisticConfig freePlayProbabilisticConfig;
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
         * 元素大小概率配置 S M L XL
         */
        private TreeMap<String, List<BigDecimal>> symbolSizeProbabilisticConfig;
        /**
         * 每个reel出现金边概率配置: eg：5%
         */
        private List<BigDecimal> goldBoarderProbabilisticConfig;
    }

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

    @Override
    public void bizValidate() {
        oddsConfigValidate();
        probabilisticConfigValidate();
        scatterFreeWheelTimesConfigValidate();
        rewardMaxMultipleValidate();
        templateRangeConfigValidate();
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
        Assert.notNull(probabilisticConfig.commonPlayProbabilisticConfig, "probabilisticConfig.commonPlayProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.freePlayProbabilisticConfig, "probabilisticConfig.freePlayPlayProbabilisticConfig is null");

        playProbabilisticConfigValid(probabilisticConfig.commonPlayProbabilisticConfig);
        playProbabilisticConfigValid(probabilisticConfig.freePlayProbabilisticConfig);
    }
    private void scatterFreeWheelTimesConfigValidate() {
        Assert.notNull(scatterFreeWheelTimesConfig, "scatterFreeWheelTimesConfig is null");
    }

    private void playProbabilisticConfigValid(PlayProbabilisticConfig playProbabilisticConfig) {
        wheelProbabilisticConfigValid(REEL_NUMBERS, playProbabilisticConfig.getWheelProbabilisticConfig());
        symbolSizeProbabilisticConfigValid(REEL_NUMBERS, playProbabilisticConfig.getSymbolSizeProbabilisticConfig());
        goldBoarderProbabilisticConfigValid(playProbabilisticConfig.getGoldBoarderProbabilisticConfig());
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
    private void symbolSizeProbabilisticConfigValid(int column, TreeMap<String, List<BigDecimal>> symbolSizeProbabilisticConfig) {
        symbolSizeProbabilisticConfig.forEach((key, value) -> {
            AssertUtil.assertNotEmpty(value, ErrorCode.ERR_INTER, "probabilisticConfig.commonPlaySymbolSizeProbabilisticConfigValid key: " + key + " ERR");
            AssertUtil.assertTrue(Objects.equals(value.size(), column), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlaySymbolSizeProbabilisticConfigValid: " + key + " ERR");
        });
        for (int columnIndex = 0; columnIndex < column; columnIndex++) {
            int columnNo = columnIndex;
            TreeMap<String, Integer> map = new TreeMap<>();
            symbolSizeProbabilisticConfig.entrySet().forEach(item -> {
                int val = (item.getValue().get(columnNo).multiply(new BigDecimal(CONST_10000))).intValue();
                if (val > 0) {
                    map.put(item.getKey(), val);
                }
            });
            Integer sumProbabilistic = map.values().stream().reduce(0, Integer::sum);
            AssertUtil.assertTrue(Objects.equals(sumProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlaySymbolSizeProbabilisticConfigValid ERR, columnNo:" + columnNo);
        }
    }
    private void goldBoarderProbabilisticConfigValid(List<BigDecimal> list) {
        AssertUtil.assertTrue(Objects.equals(list.size(), 7), ErrorCode.ERR_INTER, "probabilisticConfig.freeWheelGoldBoarderProbabilisticConfig size error");
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
