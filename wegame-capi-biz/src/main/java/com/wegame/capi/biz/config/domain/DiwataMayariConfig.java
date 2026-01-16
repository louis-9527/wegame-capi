package com.wegame.capi.biz.config.domain;

import cn.hutool.core.lang.Assert;
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

/**
 * diwata mayari 配置
 * 1.赔率配置
 * 2.概率配置
 * 2.1 正常轮(正常投注，购买投注) 首轮概率
 * 2.2 正常轮回合概率
 * 2.3 免费轮首轮概率
 * 2.4 免费轮回合概率
 * 3.倍率盘规则配置
 * 4.免费轮次数获取规则配置.基础10轮(3个scatter)，步长2（每增加一个scatter）
 * 5.中奖最大倍数配置（10250）
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DiwataMayariConfig extends GameBizConfig{

    private final static int REEL_NUMBERS = 6;

    /**
     * 赔率配置
     */
    private OddsConfig oddsConfig;
    /**
     * 概率配置
     */
    private ProbabilisticConfig probabilisticConfig;
    /**
     * 倍率盘规则配置
     */
    private MultiplePieRuleConfig multiplePieRuleConfig;
    /**
     * 免费轮次数规则配置
     */
    private FreeWheelTimesRuleConfig freeWheelTimesRuleConfig;
    /**
     * 中奖最大倍数
     */
    private Integer rewardMaxMultiple;

    /**
     * 模板配置
     */
    private TemplateRangeConfig templateRangeConfig;

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
         * 【正常轮首轮】滚轮生成概率配置
         * TreeMap<元素ID，<列坐标，概率列表>>
         */
        private TreeMap<String, List<BigDecimal>> commonPlayFirstSpinProbabilisticConfig;
        /**
         * 【正常轮回合】滚轮生成概率配置
         * TreeMap<元素ID，<列坐标，概率列表>>
         */
        private TreeMap<String, List<BigDecimal>> commonPlayRoundProbabilisticConfig;
        /**
         * 【免费轮首轮】滚轮生成概率配置
         * TreeMap<元素ID，<列坐标，概率列表>>
         */
        private TreeMap<String, List<BigDecimal>> freePlayFirstSpinProbabilisticConfig;
        /**
         * 【免费轮回合】滚轮生成概率配置
         * TreeMap<元素ID，<列坐标，概率列表>>
         */
        private TreeMap<String, List<BigDecimal>> freePlayRoundProbabilisticConfig;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MultiplePieRuleConfig implements Serializable {
        /**
         * 正常轮最小倍率-1.0
         */
        private BigDecimal commonPlayMinMultiple;

        /**
         * 免费轮最小倍率-8.0
         */
        private BigDecimal freePlayMinMultiple;
        /**
         * 最大倍率-1024.0
         */
        private BigDecimal maxMultiple;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FreeWheelTimesRuleConfig implements Serializable {
        /**
         * 基础次数-3个scatter个数-10
         */
        private Integer baseFreeWheelTimes;
        /**
         * 步长-在基础次数基础上，每增加一个scatter,次数增加的次数-2
         */
        private Integer stepFreeWheelTimes;
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

    public void bizValidate() {
        this.oddsConfigValidate();
        this.probabilisticConfigValidate();
        this.multiplePieRuleConfigValidate();
        this.freeWheelTimesRuleConfigValidate();
        this.rewardMaxMultipleValidate();
    }

    private void freeWheelTimesRuleConfigValidate() {
        AssertUtil.assertNotNull(this.freeWheelTimesRuleConfig, ErrorCode.ERR_INTER, "freeWheelTimesRuleConfig is null");
        AssertUtil.assertNotNull(this.freeWheelTimesRuleConfig.baseFreeWheelTimes,
                ErrorCode.ERR_INTER, "freeWheelTimesRuleConfig.baseFreeWheelTimes is null");
        AssertUtil.assertTrue(this.freeWheelTimesRuleConfig.baseFreeWheelTimes > 0,
                ErrorCode.ERR_INTER, "freeWheelTimesRuleConfig.baseFreeWheelTimes is must be than 0");
        AssertUtil.assertNotNull(this.freeWheelTimesRuleConfig.stepFreeWheelTimes,
                ErrorCode.ERR_INTER, "freeWheelTimesRuleConfig.stepFreeWheelTimes is null");
        AssertUtil.assertTrue(this.freeWheelTimesRuleConfig.stepFreeWheelTimes >= 0,
                ErrorCode.ERR_INTER, "freeWheelTimesRuleConfig.stepFreeWheelTimes is must be or equals than 0");
    }

    private void multiplePieRuleConfigValidate() {
        AssertUtil.assertNotNull(this.multiplePieRuleConfig, ErrorCode.ERR_INTER, "multiplePieRuleConfig is null");
        AssertUtil.assertNotNull(this.multiplePieRuleConfig.commonPlayMinMultiple, ErrorCode.ERR_INTER, "multiplePieRuleConfig.commonPlayMinMultiple is null");
        AssertUtil.assertTrue(this.multiplePieRuleConfig.commonPlayMinMultiple.compareTo(BigDecimal.ZERO) > 0, ErrorCode.ERR_INTER,
                "multiplePieRuleConfig.commonPlayMinMultiple is must be than 0");
        AssertUtil.assertNotNull(this.multiplePieRuleConfig.freePlayMinMultiple, ErrorCode.ERR_INTER, "multiplePieRuleConfig.freePlayMinMultiple is null");
        AssertUtil.assertTrue(this.multiplePieRuleConfig.freePlayMinMultiple.compareTo(BigDecimal.ZERO) > 0, ErrorCode.ERR_INTER,
                "multiplePieRuleConfig.freePlayMinMultiple is must be than 0");
        AssertUtil.assertNotNull(this.multiplePieRuleConfig.maxMultiple, ErrorCode.ERR_INTER, "multiplePieRuleConfig.maxMultiple is null");
        AssertUtil.assertTrue(this.multiplePieRuleConfig.maxMultiple.compareTo(BigDecimal.ZERO) > 0, ErrorCode.ERR_INTER,
                "multiplePieRuleConfig.maxMultiple is must be than 0");

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
        Assert.notNull(probabilisticConfig.commonPlayFirstSpinProbabilisticConfig, "probabilisticConfig.commonPlayFirstSpinProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.commonPlayRoundProbabilisticConfig, "probabilisticConfig.commonPlayRoundProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.freePlayFirstSpinProbabilisticConfig, "probabilisticConfig.freePlayFirstSpinProbabilisticConfig is null");
        Assert.notNull(probabilisticConfig.freePlayRoundProbabilisticConfig, "probabilisticConfig.freePlayRoundProbabilisticConfig is null");
        wheelProbabilisticConfigValid(probabilisticConfig.commonPlayFirstSpinProbabilisticConfig);
        wheelProbabilisticConfigValid(probabilisticConfig.commonPlayRoundProbabilisticConfig);
        wheelProbabilisticConfigValid(probabilisticConfig.freePlayFirstSpinProbabilisticConfig);
        wheelProbabilisticConfigValid(probabilisticConfig.freePlayRoundProbabilisticConfig);
    }

    private void wheelProbabilisticConfigValid(TreeMap<String, List<BigDecimal>> wheelProbabilisticConfig) {
        wheelProbabilisticConfig.forEach((key, value) -> {
            AssertUtil.assertNotEmpty(value, ErrorCode.ERR_INTER, "probabilisticConfig.wheelProbabilisticConfig key: " + key + " ERR");
            AssertUtil.assertTrue(Objects.equals(value.size(), REEL_NUMBERS), ErrorCode.ERR_INTER, "probabilisticConfig.wheelProbabilisticConfig: " + key + " ERR");
        });
        for (int columnIndex = 0; columnIndex < REEL_NUMBERS; columnIndex++) {
            int columnNo = columnIndex;
            TreeMap<String, Integer> map = new TreeMap<>();
            wheelProbabilisticConfig.entrySet().forEach(item -> {
                int val = (item.getValue().get(columnNo).multiply(new BigDecimal(CONST_10000))).intValue();
                if (val > 0) {
                    map.put(item.getKey(), val);
                }
            });
            Integer sumProbabilistic = map.values().stream().reduce(0, Integer::sum);
            AssertUtil.assertTrue(Objects.equals(sumProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.wheelProbabilisticConfig ERR, columnNo:" + columnNo);
        }
    }

}
