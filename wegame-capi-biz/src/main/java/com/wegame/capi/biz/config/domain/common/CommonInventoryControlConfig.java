package com.wegame.capi.biz.config.domain.common;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.domain.BetAmountRangeDo;
import com.wegame.capi.common.domain.InventoryRangeDo;
import com.wegame.capi.common.enums.InventoryTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.wegame.capi.common.Constants.COMMON_CONFIG_SPLIT;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommonInventoryControlConfig implements Serializable {
    /**
     * 启用/禁用
     */
    private boolean enable = false;
    /**
     * 奖励配置
     */
    private RewardConfig rewardConfig;
    /**
     * 库存配置
     */
    private InventoryConfig inventoryConfig;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RewardConfig implements Serializable {
        /**
         * 中奖轮空率
         * TreeMap<投注类型，中奖轮空概率>
         */
        private TreeMap<BetTypeEnum, BigDecimal> rewardBlankProbability;
        /**
         * 奖励配置Map
         * TreeMap<投注类型，TreeMap<名称，<左开赔率_右闭赔率，权重>>>
         */
        private TreeMap<BetTypeEnum, TreeMap<String, TreeMap<String, Integer>>> rewardConfigMap;
        /**
         * 中奖兜底模版倍率
         * TreeMap<投注类型，中奖兜底赔率区间（左赔率_右赔率）>
         */
        private TreeMap<BetTypeEnum, String> rewardFloorMultiple;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryConfig implements Serializable {
        /**
         * 库存池子Map
         * TreeMap<投注类型，TreeMap<pid，TreeMap<channelId，poolName>>
         */
        private TreeMap<BetTypeEnum, TreeMap<String, TreeMap<String, String>>> inventoryPoolMap;
        /**
         * 库存配置Map
         * TreeMap<名称，<左开赔率_右闭赔率，权重>>
         */
        private TreeMap<InventoryTypeEnum, InventoryConfigItem> inventoryConfigMap;

        public String getInventoryPoolName(BetTypeEnum betType, String pid, String channelId) {
            TreeMap<String, TreeMap<String, String>> amap = inventoryPoolMap.get(betType);
            AssertUtil.assertTrue(MapUtils.isNotEmpty(amap), ErrorCode.ERR_INTER);
            TreeMap<String, String> map = amap.get(pid);
            AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
            return map.get(channelId);
        }

        public Map.Entry<InventoryTypeEnum, CommonInventoryControlConfig.InventoryConfigItem> match(Long betAmount) {
            return inventoryConfigMap.entrySet().stream().filter(item -> {
                BetAmountRangeDo betAmountRange = BetAmountRangeDo.build(item.getValue().getBetAmountRange());
                return betAmountRange.inRange(betAmount);
            }).findFirst().orElse(null);
        }
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryConfigItem implements Serializable {
        /**
         * 投注范围(左闭右闭)
         */
        private String betAmountRange;
        /**
         * 库存入池率
         */
        private BigDecimal inventoryInputRate;
        /**
         * 库存控制配置
         * TreeMap<投注类型，TreeMap<库存区间（左开右闭），库存控制配置>
         */
        private TreeMap<BetTypeEnum, TreeMap<String, InventoryControlConfigItem>> controlConfig;

        public void validate() {
            Assert.notNull(betAmountRange, "betAmountRange is null");
            BetAmountRangeDo betAmountRangeDo = BetAmountRangeDo.build(betAmountRange);
            Assert.notNull(betAmountRangeDo, "betAmountRange is null");
            Assert.notNull(inventoryInputRate, "inventoryInputRate is null");
            Assert.isTrue(inventoryInputRate.compareTo(BigDecimal.ZERO) > 0, "inventoryInputRate must > 0");
            Assert.isTrue(inventoryInputRate.compareTo(BigDecimal.ONE) <= 0, "inventoryInputRate must <= 1");
            int actualScale = inventoryInputRate.stripTrailingZeros().scale();
            // 库存入库率最大支持四位小数
            Assert.isTrue(actualScale >= 0 && actualScale <= 3, "inventoryInputRate Scale mulst <= 4");
            Assert.isTrue(inventoryInputRate.compareTo(new BigDecimal(0.9)) >= 0
                    && inventoryInputRate.compareTo(new BigDecimal(1)) <= 0, "inventoryInputRate must in range [0.9, 1]");
            Assert.isTrue(MapUtils.isNotEmpty(controlConfig), "controlConfig is empty");
            TreeMap<String, InventoryControlConfigItem> normalBetMap = controlConfig.get(BetTypeEnum.NORMAL_BET);
            Assert.isTrue(MapUtils.isNotEmpty(normalBetMap), "controlConfig is empty");
            normalBetMap.entrySet().forEach(item -> {
                String inventoryRangeStr = item.getKey();
                Assert.notEmpty(inventoryRangeStr, "inventoryRangeStr is null");
                InventoryRangeDo inventoryRange = InventoryRangeDo.build(item.getKey());
                Assert.notNull(inventoryRange, "inventoryRangeStr is error");
                InventoryControlConfigItem inventoryControlConfigItem = item.getValue();
                Assert.notNull(inventoryControlConfigItem, "inventoryControlConfigItem is null");
                inventoryControlConfigItem.validate();
            });
            TreeMap<String, InventoryControlConfigItem> buyBetMap = controlConfig.get(BetTypeEnum.BUY_BET);
            Assert.isTrue(MapUtils.isNotEmpty(buyBetMap), "controlConfig is empty");
            buyBetMap.entrySet().forEach(item -> {
                String inventoryRangeStr = item.getKey();
                Assert.notEmpty(inventoryRangeStr, "inventoryRangeStr is null");
                InventoryRangeDo inventoryRange = InventoryRangeDo.build(item.getKey());
                Assert.notNull(inventoryRange, "inventoryRangeStr is error");
                InventoryControlConfigItem inventoryControlConfigItem = item.getValue();
                Assert.notNull(inventoryControlConfigItem, "inventoryControlConfigItem is null");
                inventoryControlConfigItem.validate();
            });
        }

        public InventoryControlConfigItem matchControlConfig(BetTypeEnum betType, long currentInventoryVal) {
            return controlConfig.get(betType).entrySet().stream().filter(item -> {
                InventoryRangeDo inventoryRange = InventoryRangeDo.build(item.getKey());
                return inventoryRange.inRange(currentInventoryVal);
            }).findFirst().map(Map.Entry::getValue).orElse(null);
        }
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryControlConfigItem implements Serializable {
        /**
         * 赢的概率
         */
        private BigDecimal winProbability;
        /**
         * 赢：中奖配置id
         */
        private String winRewardConfigId;
        /**
         * 输：中奖配置id
         */
        private String loseRewardConfigId;
        ///**
        // * 正常投注概率配置
        // */
        //private ProbabilisticConfig normalBetProbabilityConfig;
        ///**
        // * 购买投注概率配置
        // */
        //private ProbabilisticConfig buyBetProbabilityConfig;

        public String getRewardConfigId(boolean winRet) {
            return winRet ? winRewardConfigId : loseRewardConfigId;
        }

        public void validate() {
            Assert.notNull(winProbability, "winProbability is null");
            Assert.isTrue(winProbability.compareTo(BigDecimal.ZERO) >= 0, "winProbability must >= 0");
            Assert.isTrue(winProbability.compareTo(BigDecimal.ONE) <= 0, "winProbability must <= 1");
            int actualScale = winProbability.stripTrailingZeros().scale();
            // 赢的概率最大支持四位小数
            Assert.isTrue(actualScale >= 0 && actualScale <= 4, "winProbability actualScale must <= 4");
            Assert.notEmpty(winRewardConfigId, "winRewardConfigId is null");
            Assert.notEmpty(loseRewardConfigId, "loseRewardConfigId is null");
        }
    }

    //@Data
    //@SuperBuilder
    //@NoArgsConstructor
    //@AllArgsConstructor
    //public static class ProbabilisticConfig implements Serializable {
    //    /**
    //     * 赢的概率
    //     */
    //    private BigDecimal winProbability;
    //    /**
    //     * 赢：中奖配置id
    //     */
    //    private String winRewardConfigId;
    //    /**
    //     * 输：中奖配置id
    //     */
    //    private String loseRewardConfigId;
    //
    //    public void validate() {
    //        Assert.notEmpty(winRewardConfigId, "winRewardConfigId is null");
    //        Assert.notEmpty(loseRewardConfigId, "winRewardConfigId is null");
    //    }
    //}

    public void validate() {
        rewardConfigValidate();
        inventoryConfigValidate();
    }

    private void rewardConfigValidate() {
        Assert.notNull(rewardConfig, "rewardConfig is null");
        Assert.isTrue(MapUtils.isNotEmpty(rewardConfig.getRewardBlankProbability()), "rewardConfig Err");
        Assert.isTrue(MapUtils.isNotEmpty(rewardConfig.getRewardConfigMap()), "rewardConfig Err");
        Assert.isTrue(MapUtils.isNotEmpty(rewardConfig.getRewardFloorMultiple()), "rewardConfig Err");
        // 校验-轮空率配置
        rewardConfig.getRewardBlankProbability().entrySet().forEach(item -> {
            Assert.isTrue(Objects.equals(item.getKey(), BetTypeEnum.NORMAL_BET), "rewardConfig Err");
            Assert.notNull(item.getValue(), "rewardConfig error");
            Assert.isTrue(item.getValue().compareTo(BigDecimal.ZERO) > 0, "rewardConfig Err");
            Assert.isTrue(item.getValue().compareTo(BigDecimal.ONE) < 0, "rewardConfig Err");
            int actualScale = item.getValue().stripTrailingZeros().scale();
            // 轮空率最大支持四位小数
            Assert.isTrue(actualScale >= 0 && actualScale <= 4, "rewardConfig Err");
        });
        // 校验-奖励配置map校验
        Assert.isTrue(MapUtils.isNotEmpty(rewardConfig.getRewardConfigMap()), "rewardConfig Err");
        rewardConfig.getRewardConfigMap().entrySet().forEach(item -> {
            Assert.notNull(item.getKey(), "rewardConfig Err");
            Assert.isTrue(MapUtils.isNotEmpty(item.getValue()), "rewardConfig Err");
            TreeMap<String, TreeMap<String, Integer>> treeMap = item.getValue();
            treeMap.entrySet().forEach(treeMapItem -> {
                Assert.isTrue(StringUtils.isNotEmpty(treeMapItem.getKey()), "rewardConfig Err");
                Assert.isTrue(MapUtils.isNotEmpty(treeMapItem.getValue()), "rewardConfig Err");
                treeMapItem.getValue().entrySet().forEach(subItem -> {
                    validateRange(subItem.getKey());
                    Assert.isTrue(subItem.getValue() >= 0, "rewardConfig inventory range weight must >= 0");
                });
            });
        });
        // 校验-中奖兜底赔率配置
        Arrays.stream(BetTypeEnum.values()).forEach(item -> {
            String oddsRangeStr = rewardConfig.getRewardFloorMultiple().get(item);
            Assert.notEmpty(oddsRangeStr, "rewardConfig rewardFloorOdds ERR");
            validateRange(oddsRangeStr);
        });
    }

    private void validateRange(String rangeStr) {
        Assert.isTrue(rangeStr.contains(COMMON_CONFIG_SPLIT), "range not contain '_'");
        String[] rangeArr = rangeStr.split(COMMON_CONFIG_SPLIT);
        Assert.isTrue(rangeArr.length == 2, "range length is not equals 2");
        Assert.isTrue(NumberUtil.isNumber(rangeArr[0]), "range first number must be number");
        Assert.isTrue(NumberUtil.isNumber(rangeArr[1]), "range second number must be number");
        Assert.isTrue(new BigDecimal(rangeArr[0]).compareTo(new BigDecimal(rangeArr[1])) <= 0, "range must left <= right");
    }

    private void inventoryConfigValidate() {
        Assert.notNull(inventoryConfig, "inventoryConfig is null");
        Assert.isTrue(MapUtils.isNotEmpty(inventoryConfig.getInventoryConfigMap()), "inventoryConfig.inventoryConfigMap is null");
        Arrays.stream(InventoryTypeEnum.values()).forEach(item -> {
            InventoryConfigItem inventoryConfigItem = inventoryConfig.getInventoryConfigMap().get(item);
            Assert.notNull(inventoryConfigItem, "inventoryConfig.inventoryConfigMap.inventoryConfigItem is null");
            inventoryConfigItem.validate();
        });
    }
}
