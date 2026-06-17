package com.wegame.capi.biz.config.domain;

import cn.hutool.core.lang.Assert;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import static com.wegame.capi.biz.config.nacos.NacosConfigRefresherUtil.BAKUNAWA_JSON_ROOT_NAME;
import static com.wegame.capi.common.Constants.CONST_10000;
import static com.wegame.capi.common.Constants.CONST_1000000;

/**
 * Bakunawa游戏配置
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Component
@RefreshScope
@ConfigurationProperties(prefix = BAKUNAWA_JSON_ROOT_NAME)
public class BakunawaConfig extends GameBizConfig {
    private static final long serialVersionUID = -4811887935435114L;
    /**
     * 元素概率配置
     */
    private ProbabilisticConfig probabilisticConfig;
    /**
     * 中奖通知最小倍数
     */
    private Integer rewardNotifyMinMultiple;
    /**
     * 中奖最大倍数
     */
    private Integer rewardMaxMultiple;
    /**
     * 模板配置
     */
    private BakunawaConfig.TemplateRangeConfig templateRangeConfig;

    @Override
    public GameTypeEnum gameType() {
        return GameTypeEnum.BAKUNAWA;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProbabilisticConfig implements Serializable {
        private static final long serialVersionUID = 3091229516351949275L;
        /**
         * 元素出现概率配置
         * Map<元素ID，<列坐标，概率列表>>
         */
        private TreeMap<String, List<BigDecimal>> commonPlayWheelProbabilisticConfig;
        /**
         * 倍率盘出现概率配置
         * Map<倍率，概率>
         */
        private TreeMap<String, BigDecimal> multiplePieProbabilisticConfig;
        /**
         * 吞月倍率盘出现概率配置
         * Map<倍率，概率>
         */
        private TreeMap<String, BigDecimal> superMultiplePieProbabilisticConfig;
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
        probabilisticConfigValidate();
        rewardNotifyMinMultipleValidate();
    }

    private void probabilisticConfigValidate() {
        Assert.notNull(probabilisticConfig, "probabilisticConfig is null");

        // 1、元素出现概率配置校验
        Assert.notNull(probabilisticConfig.commonPlayWheelProbabilisticConfig, "commonPlayWheelProbabilisticConfig is null");
        int colum = 3;
        probabilisticConfig.commonPlayWheelProbabilisticConfig.forEach((key, value) -> {
            AssertUtil.assertNotEmpty(value, ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayWheelProbabilisticConfig key: " + key + " ERR");
            AssertUtil.assertTrue(Objects.equals(value.size(), colum), ErrorCode.ERR_INTER, "probabilisticConfig.commonPlayWheelProbabilisticConfig: " + key + " ERR");
        });
        for (int columIndex = 0; columIndex < colum; columIndex++) {
            int columNo = columIndex;
            Map<String, Integer> map = new HashMap<>();
            probabilisticConfig.commonPlayWheelProbabilisticConfig.forEach((key, value) -> {
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

        // 2、倍率盘出现概率配置校验
        Assert.notNull(probabilisticConfig.multiplePieProbabilisticConfig, "multiplierWheelProbabilisticConfig is null");
        Integer multiplierProbabilistic = probabilisticConfig.multiplePieProbabilisticConfig.values()
                .stream()
                .map(value -> value.multiply(new BigDecimal(10000)).intValue())
                .reduce(0, Integer::sum);
        AssertUtil.assertTrue(Objects.equals(multiplierProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.multiplierWheelProbabilisticConfig 概率总和不等于 100");


        // 3、吞月倍率盘出现概率配置校验
        Assert.notNull(probabilisticConfig.superMultiplePieProbabilisticConfig, "superMultiplierWheelProbabilisticConfig is null");
        Integer superMultiplierProbabilistic = probabilisticConfig.superMultiplePieProbabilisticConfig.values()
                .stream()
                .map(value -> value.multiply(new BigDecimal(10000)).intValue())
                .reduce(0, Integer::sum);
        AssertUtil.assertTrue(Objects.equals(superMultiplierProbabilistic, CONST_1000000), ErrorCode.ERR_INTER, "probabilisticConfig.superMultiplierWheelProbabilisticConfig 概率总和不等于 1000000");
    }

    private void rewardNotifyMinMultipleValidate() {
        AssertUtil.assertNotNull(rewardNotifyMinMultiple, ErrorCode.ERR_INTER, "rewardNotifyMinMultiple is null");
        AssertUtil.assertTrue(rewardNotifyMinMultiple > 0, ErrorCode.ERR_INTER, "rewardNotifyMinMultiple must great than 0");
        AssertUtil.assertNotNull(rewardMaxMultiple, ErrorCode.ERR_INTER, "rewardMaxMultiple is null");
        AssertUtil.assertTrue(rewardMaxMultiple > 0, ErrorCode.ERR_INTER, "rewardMaxMultiple must great than 0");
    }
}
