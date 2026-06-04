package com.wegame.capi.biz.config.domain;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.AbTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import static com.wegame.capi.biz.config.nacos.NacosConfigRefresherUtil.BAYANIHAN_JSON_ROOT_NAME;
import static com.wegame.capi.common.Constants.CONST_10000;
import static com.wegame.capi.common.Constants.CONST_1000000;

/**
 * bayanihan 配置
 *
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Component
@RefreshScope
@ConfigurationProperties(prefix = BAYANIHAN_JSON_ROOT_NAME)
public class BayanihanConfig extends GameBizConfig{

    private static final long serialVersionUID = -3176807225114663395L;

    /**
     * 赔率配置
     */
    private OddsConfig oddsConfig;
    /**
     * 概率配置
     */
    private ProbabilisticConfig probabilisticConfig;

    /**
     * 免费游戏相关配置
     */
    private FreeGameConfig freeGameConfig;
    /**
     * 中奖最大倍数
     */
    private Integer rewardMaxMultiple;

    /**
     * 模板配置
     */
    private TemplateConfig templateConfig;

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
         * 主游戏概率
         * TreeMap<元素ID，<列坐标，概率列表>>
         */
        private TreeMap<String, List<BigDecimal>> mainGameProbabilisticConfig;
        /**
         * 免费游戏概率
         * TreeMap<元素ID，<列坐标，概率列表>>
         */
        private TreeMap<String, List<BigDecimal>> freeGameProbabilisticConfig;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FreeGameConfig implements Serializable {
        /**
         * 免费游戏初始轮数
         * TreeMap<初始轮数，概率>
         */
        private TreeMap<String, BigDecimal> freeGameInitialRoundCountConfig;
        /**
         * 免费游戏初始倍率
         * TreeMap<初始倍率，概率>
         */
        private TreeMap<String, BigDecimal> freeGameInitialMultipleConfig;

        /**
         * 免费游戏赢得免费轮次数
         */
        private int freeGameRewardFgCount;

        /**
         * 升级倍率所需收集数量
         */
        private int multipleUpgradeCost;
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class TemplateConfig implements Serializable {

        private static final long serialVersionUID = -4036721298176088906L;
        /**
         * 预热Map
         */
        private Map<BetTypeEnum, BigDecimal> templateWarmUpEndOddsMap;

        private Map<AbTypeEnum, TemplateTypeEnum> abTemplateMapping;

        private Map<TemplateTypeEnum, TemplateRangeConfig> templateRangeConfig;

        public boolean supportWarmUp(BetTypeEnum betType, TemplateMultipleRange multipleRange) {
            BigDecimal endOdds = templateWarmUpEndOddsMap.get(betType);
            AssertUtil.assertNotNull(endOdds, ErrorCode.ERR_INTER);
            return multipleRange.getEnd().compareTo(endOdds) <= 0;
        }

        public TemplateTypeEnum getTemplateType(AbTypeEnum abType) {
            return abTemplateMapping.get(abType);
        }

        public TemplateRangeConfig getTemplateRangeConfig(TemplateTypeEnum templateType) {
            return templateRangeConfig.get(templateType);
        }

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

        public void validate() {
            Assert.notNull(mainGame, "templateRangeConfig.mainGame is null");
            Assert.notNull(freeGame, "templateRangeConfig.freeGame is null");
            this.getMainGame().keySet().forEach(templateMultipleRange -> {
                Assert.isTrue(templateMultipleRange.contains("_"), "templateRangeConfig.mainGame.key not contain '_'");
                String[] multipleRangeArr = templateMultipleRange.split("_");
                Assert.isTrue(multipleRangeArr.length == 2, "templateRangeConfig.mainGame.key length is not eqauls 2");
                Assert.isTrue(NumberUtil.isNumber(multipleRangeArr[0]), "templateRangeConfig.mainGame.key first number must be number");
                Assert.isTrue(NumberUtil.isNumber(multipleRangeArr[1]), "templateRangeConfig.mainGame.key first number must be number");
                Assert.isTrue(new BigDecimal(multipleRangeArr[0]).compareTo(new BigDecimal(multipleRangeArr[1])) <= 0, "templateRangeConfig.mainGame.key first number must be less than or equal to the second number");
            });
            this.getFreeGame().keySet().forEach(templateMultipleRange -> {
                Assert.isTrue(templateMultipleRange.contains("_"), "templateRangeConfig.freeGame.key not contain '_'");
                String[] multipleRangeArr = templateMultipleRange.split("_");
                Assert.isTrue(multipleRangeArr.length == 2, "templateRangeConfig.freeGame.key length is not eqauls 2");
                Assert.isTrue(NumberUtil.isNumber(multipleRangeArr[0]), "templateRangeConfig.freeGame.key first number must be number");
                Assert.isTrue(NumberUtil.isNumber(multipleRangeArr[1]), "templateRangeConfig.freeGame.key first number must be number");
                Assert.isTrue(new BigDecimal(multipleRangeArr[0]).compareTo(new BigDecimal(multipleRangeArr[1])) <= 0, "templateRangeConfig.freeGame.key first number must be less than or equal to the second number");
            });
        }
    }

    @Override
    public void bizValidate() {
        this.oddsConfigValidate();
        this.probabilisticConfigValidate();
        this.rewardMaxMultipleValidate();
        this.templateConfigValidate();
    }

    private void rewardMaxMultipleValidate() {
        AssertUtil.assertNotNull(this.rewardMaxMultiple, ErrorCode.ERR_INTER, "rewardMaxMultiple is null");
        AssertUtil.assertTrue(this.rewardMaxMultiple > 0, ErrorCode.ERR_INTER, "rewardMaxMultiple is must great than 0");
    }

    private void templateConfigValidate() {
        AssertUtil.assertNotNull(this.templateConfig, ErrorCode.ERR_INTER, "templateConfig is null");
        templateMappingValidate(this.templateConfig.getAbTemplateMapping());
        templateRangeMapValidate(this.getTemplateConfig().getTemplateRangeConfig());
    }

    private void templateMappingValidate(Map<AbTypeEnum, TemplateTypeEnum> templateMapping) {
        AssertUtil.assertNotNull(templateMapping, ErrorCode.ERR_INTER, "templateRangeConfig.templateMapping is null");
        for (AbTypeEnum abType : AbTypeEnum.values()) {
            // 每个AB类型只能有一个模板
            AssertUtil.assertNotNull(templateMapping.get(abType), ErrorCode.ERR_INTER, "templateMapping.get(abType) is null of " + abType.getId());
            // 模版必须是diwata 的gameType 类型
            AssertUtil.assertTrue(Objects.equals(templateMapping.get(abType).getGameType(), GameTypeEnum.BAYANIHAN), ErrorCode.ERR_INTER, "templateMapping.get(abType) is not BAYANIHAN of " + abType.getId());
        }
    }

    private void templateRangeMapValidate(Map<TemplateTypeEnum, TemplateRangeConfig> rangeConfigMap) {
        AssertUtil.assertNotNull(rangeConfigMap, ErrorCode.ERR_INTER, "templateRangeConfig.templateRangeMap is null");
        rangeConfigMap.forEach((templateType, rangConfig) -> {
            // 必须是diwata 的gameType 类型
            AssertUtil.assertTrue(Objects.equals(templateType.getGameType(), GameTypeEnum.BAYANIHAN), ErrorCode.ERR_INTER, "templateRangeConfig.templateRangeMap.get(templateType) is not BAYANIHAN of " + templateType.name());
            // betTypeMap必须配置完
            AssertUtil.assertNotNull(rangConfig, ErrorCode.ERR_INTER, "templateRangeConfig.templateRangeMap.get(templateType) is null of " + templateType.name());
            AssertUtil.assertNotNull(rangConfig.getFreeGame(), ErrorCode.ERR_INTER, "freegame config is null");
            AssertUtil.assertNotNull(rangConfig.getMainGame(), ErrorCode.ERR_INTER, "maingame config is null");

            rangConfig.validate();
        });
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
        //todo
        int colum = 5;

        // freeGame
        for (int columIndex = 0; columIndex < colum; columIndex++) {
            Integer columNo = columIndex;
            Map<String, Integer> map = new HashMap<>();
            probabilisticConfig.getFreeGameProbabilisticConfig().entrySet().forEach(item -> {
                int val = (item.getValue().get(columNo).multiply(new BigDecimal(CONST_10000))).intValue();
                if (val > 0) {
                    map.put(item.getKey(), val);
                }
            });
            Integer sumProbabilistic = map.values()
                    .stream()
                    .reduce(0, Integer::sum);
            AssertUtil.assertTrue(Objects.equals(sumProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.freeGameProbabilisticConfig ERR, columNo:" + columNo);
        }

        // mainGame
        for (int columIndex = 0; columIndex < colum; columIndex++) {
            Integer columNo = columIndex;
            Map<String, Integer> map = new HashMap<>();
            probabilisticConfig.getMainGameProbabilisticConfig().entrySet().forEach(item -> {
                int val = (item.getValue().get(columNo).multiply(new BigDecimal(CONST_10000))).intValue();
                if (val > 0) {
                    map.put(item.getKey(), val);
                }
            });
            Integer sumProbabilistic = map.values()
                    .stream()
                    .reduce(0, Integer::sum);
            AssertUtil.assertTrue(Objects.equals(sumProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.mainGameProbabilisticConfig ERR, columNo:" + columNo);
        }
    }

}
