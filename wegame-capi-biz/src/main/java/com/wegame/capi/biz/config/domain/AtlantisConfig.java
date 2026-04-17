package com.wegame.capi.biz.config.domain;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.nacos.NacosConfigRefresherUtil;
import com.wegame.capi.biz.module.atlantis.AtlantisGameHelper;
import com.wegame.capi.common.enums.BizEventEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import static com.wegame.capi.common.Constants.CONST_10000;
import static com.wegame.capi.common.Constants.CONST_1000000;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Component
@RefreshScope
@ConfigurationProperties(prefix = NacosConfigRefresherUtil.ATLANTIS_JSON_ROOT_NAME)
public class AtlantisConfig extends GameBizConfig implements Serializable {
    private static final long serialVersionUID = 5550628498801680050L;
    /**
     * 中奖最大倍数
     */
    private Integer rewardMaxMultiple;
    /**
     * 免费轮次数配置
     */
    private FreeWheelTimesConfig freeWheelTimesConfig;

    /**
     * 赔率配置
     */
    private OddsConfig oddsConfig;
    /**
     * 概率配置
     */
    private ProbabilisticConfig probabilisticConfig;
    /**
     * 模版范围配置
     */
    private TemplateRangeConfig templateRangeConfig;
    /**
     * 购买免费轮期望的scatter个数
     */
    private Integer buyBetExceptedScatterCount;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class FreeWheelTimesConfig implements Serializable {
        private static final long serialVersionUID = 86882342392389675L;
        /**
         * 基础免费次数:8
         * 4个scatter中8
         */
        private Integer baseFreeWheelTimes;
        /**
         * 步长:1
         * 在基础之上，每增加一个scatter 增加一次免费次数
         */
        private Integer step;

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
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class TemplateRangeConfig implements Serializable {
        private static final long serialVersionUID = -734841249803450751L;
        /**
         * 模板配置
         * LinkedHashMap<BetSubType, LinkedHashMap<模版范围, 权重>>
         */
        private LinkedHashMap<String, LinkedHashMap<String, Integer>> templateRangeMap;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProbabilisticConfig implements Serializable {
        private static final long serialVersionUID = -3127327864121298415L;
        private static final String SYMBOL_KEY_SEPARATOR = "_";

        /**
         * 滚轮生成概率配置
         * TreeMap<元素配置，<列坐标，概率列表>>
         * 在json 文件中 key的配置格式eg："1001_3"
         */
        private TreeMap<SymbolKeyConfig, List<BigDecimal>> wheelProbabilisticConfig;
        /**
         * 主游戏业务概率配置
         * TreeMap<格子,TreeMap<事件类型，概率>>
         */
        private TreeMap<Integer, TreeMap<BizEventEnum, BigDecimal>> mainGameBizEventProbabilisticConfig;
        /**
         * 免费游戏业务概率配置
         * TreeMap<格子,TreeMap<事件类型，概率>>
         */
        private TreeMap<Integer, TreeMap<BizEventEnum, BigDecimal>> freeGameBizEventProbabilisticConfig;


        /**
         * 单个事件特殊概率配置
         * TreeMap<事件类型,TreeMap<出现次数，概率>>
         */
        private TreeMap<BizEventEnum, TreeMap<Integer, BigDecimal>> singleBizSpecialEventProbabilisticConfig;

        public void setWheelProbabilisticConfig(LinkedHashMap<String, List<BigDecimal>> wheelProbabilisticConfigOrigin) {
            TreeMap<SymbolKeyConfig, List<BigDecimal>> wheelProbabilisticConfig = new TreeMap<>(Comparator.comparing(SymbolKeyConfig::getSymbolId).thenComparing(SymbolKeyConfig::getCount));
            if (wheelProbabilisticConfigOrigin != null) {
                wheelProbabilisticConfigOrigin.forEach((key, value) -> {
                    String[] symbolKeyArr = key.split(SYMBOL_KEY_SEPARATOR);
                    AssertUtil.assertNotEmpty(symbolKeyArr, ErrorCode.ERR_PARAM, "wheelProbabilisticConfig key is empty");
                    AssertUtil.assertTrue(symbolKeyArr.length == 2, ErrorCode.ERR_PARAM, "wheelProbabilisticConfig key length is not eqauls 2");
                    AssertUtil.assertTrue(NumberUtil.isInteger(symbolKeyArr[1]), ErrorCode.ERR_PARAM, "symbol count must be int  " + key);
                    wheelProbabilisticConfig.put(new SymbolKeyConfig(symbolKeyArr[0], Integer.valueOf(symbolKeyArr[1])), value);
                });
            }
            this.wheelProbabilisticConfig = wheelProbabilisticConfig;
        }

        @Data
        @SuperBuilder
        @EqualsAndHashCode(of = {"symbolId", "count"})
        public static class SymbolKeyConfig implements Serializable {
            private static final long serialVersionUID = -312756782432415L;

            public SymbolKeyConfig(String symbolId, Integer count) {
                AssertUtil.assertStrNotEmpty(symbolId, ErrorCode.ERR_INTER, "symbolId is empty");
                AssertUtil.assertTrue(Objects.nonNull(AtlantisGameHelper.ATLANTIS_GAME_INFO.getSymbolById(symbolId)), ErrorCode.ERR_PARAM, "symbol is not exist of " + symbolId);
                AssertUtil.assertNotNull(count, ErrorCode.ERR_INTER, "count is null");
                AssertUtil.assertTrue(count > 0, ErrorCode.ERR_INTER, "count is must great than 0");
                this.symbolId = symbolId;
                this.count = count;
            }

            /**
             * 元素id
             */
            private String symbolId;
            /**
             * 元素个数
             */
            private Integer count;
        }
    }

    @Override
    public void bizValidate() {
        rewardMaxMultipleValidate();
        freeWheelTimesConfigValidate();
        oddsConfigValidate();
        probabilisticConfigValidate();
    }

    private void rewardMaxMultipleValidate() {
        AssertUtil.assertNotNull(this.rewardMaxMultiple, ErrorCode.ERR_INTER, "rewardMaxMultiple is null");
        AssertUtil.assertTrue(this.rewardMaxMultiple > 0, ErrorCode.ERR_INTER, "rewardMaxMultiple is must great than 0");
    }

    private void freeWheelTimesConfigValidate() {
        AssertUtil.assertNotNull(this.freeWheelTimesConfig, ErrorCode.ERR_INTER, "freeWheelTimesConfig is null");
        AssertUtil.assertNotNull(this.freeWheelTimesConfig.getBaseFreeWheelTimes(), ErrorCode.ERR_INTER, "freeWheelTimesConfig.baseFreeWheelTimes is null");
        AssertUtil.assertTrue(this.freeWheelTimesConfig.getBaseFreeWheelTimes() > 0, ErrorCode.ERR_INTER, "freeWheelTimesConfig.baseFreeWheelTimes is must great than 0");
        AssertUtil.assertNotNull(this.freeWheelTimesConfig.getStep(), ErrorCode.ERR_INTER, "freeWheelTimesConfig.step is null");
        AssertUtil.assertTrue(this.freeWheelTimesConfig.getStep() > 0, ErrorCode.ERR_INTER, "freeWheelTimesConfig.step is must great than 0");
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
        // probabilisticConfig校验
        Assert.notNull(this.probabilisticConfig, "probabilisticConfig is null");
        this.wheelProbabilisticConfigValidate(this.probabilisticConfig.getWheelProbabilisticConfig());
        this.bizEventProbabilisticConfigValidate(this.probabilisticConfig.getMainGameBizEventProbabilisticConfig());
        this.bizEventProbabilisticConfigValidate(this.probabilisticConfig.getFreeGameBizEventProbabilisticConfig());
        this.singleBizSpecialEventProbabilisticConfig(this.probabilisticConfig.getSingleBizSpecialEventProbabilisticConfig());
    }

    private void singleBizSpecialEventProbabilisticConfig(TreeMap<BizEventEnum, TreeMap<Integer, BigDecimal>> singleBizSpecialEventProbabilisticConfig) {
        AssertUtil.assertNotNull(singleBizSpecialEventProbabilisticConfig, ErrorCode.ERR_INTER, "probabilisticConfig.singleBizSpecialEventProbabilisticConfig is null");
        singleBizSpecialEventProbabilisticConfig.forEach((bizEvent, value) -> {
            TreeMap<Integer, BigDecimal> decimalMap = singleBizSpecialEventProbabilisticConfig.get(bizEvent);
            Map<Integer, Integer> map = new HashMap<>();
            decimalMap.forEach((mapKey, decimalMapKValue) -> {
                int val = (decimalMapKValue.multiply(new BigDecimal(CONST_10000))).intValue();
                if (val > 0) {
                    map.put(mapKey, val);
                }
            });

            Integer sumProbabilistic = map.values()
                    .stream()
                    .reduce(0, Integer::sum);
            AssertUtil.assertTrue(Objects.equals(sumProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.singleBizSpecialEventProbabilisticConfig ERR, columNo:" + bizEvent.name());
        });
    }

    private void bizEventProbabilisticConfigValidate(TreeMap<Integer, TreeMap<BizEventEnum, BigDecimal>> bizEventProbabilisticConfig) {
        AssertUtil.assertNotNull(bizEventProbabilisticConfig, ErrorCode.ERR_INTER, "probabilisticConfig.bizEventProbabilisticConfig is null");
        bizEventProbabilisticConfig.forEach((key, value) -> {
            TreeMap<BizEventEnum, BigDecimal> indexMap = bizEventProbabilisticConfig.get(key);
            Map<BizEventEnum, Integer> map = new HashMap<>();
            indexMap.forEach((indexMapKey, indexMapKValue) -> {
                int val = (indexMapKValue.multiply(new BigDecimal(CONST_10000))).intValue();
                if (val > 0) {
                    map.put(indexMapKey, val);
                }
            });

            Integer sumProbabilistic = map.values()
                    .stream()
                    .reduce(0, Integer::sum);
            AssertUtil.assertTrue(Objects.equals(sumProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayWheelProbabilisticConfig ERR, columNo:" + key);

        });
    }

    private void wheelProbabilisticConfigValidate(TreeMap<ProbabilisticConfig.SymbolKeyConfig, List<BigDecimal>> wheelProbabilisticConfig) {
        AssertUtil.assertNotNull(wheelProbabilisticConfig, ErrorCode.ERR_INTER, "wheelProbabilisticConfig is null");

        int colum = 5;
        wheelProbabilisticConfig.forEach((key, value) -> {
            AssertUtil.assertNotEmpty(value, ErrorCode.ERR_INTER, "wheelProbabilisticConfig key: " + key + " ERR");
            AssertUtil.assertTrue(Objects.equals(value.size(), colum), ErrorCode.ERR_INTER, "wheelProbabilisticConfig: " + key + " ERR");
        });
        for (int columIndex = 0; columIndex < colum; columIndex++) {
            int columNo = columIndex;
            Map<ProbabilisticConfig.SymbolKeyConfig, Integer> map = new HashMap<>();
            wheelProbabilisticConfig.forEach((key, value) -> {
                int val = (value.get(columNo).multiply(new BigDecimal(CONST_10000))).intValue();
                if (val > 0) {
                    map.put(key, val);
                }
            });
            Integer sumProbabilistic = map.values()
                    .stream()
                    .reduce(0, Integer::sum);
            AssertUtil.assertTrue(Objects.equals(sumProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayWheelProbabilisticConfig ERR, columNo:" + columNo);
        }


    }
}
