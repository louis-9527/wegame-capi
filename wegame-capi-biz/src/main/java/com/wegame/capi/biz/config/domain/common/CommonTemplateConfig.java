package com.wegame.capi.biz.config.domain.common;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.AbTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.BetAmountRangeDo;
import com.wegame.capi.common.domain.InventoryRangeDo;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.InventoryTypeEnum;
import com.wegame.capi.common.enums.TemplateTypeEnum;
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
public class CommonTemplateConfig implements Serializable {
    /**
     * 启用/禁用
     */
    private boolean enable = false;

    /**
     * 模版预热Map：投注类型 —> 最大倍率（预热范围：[0,最大倍率]）, -1则不预热
     */
    private Map<BetTypeEnum, BigDecimal> templateWarmUpEndOddsMap;

    /**
     * 投注记录Es模版名称：类型 -> es模版名称
     */
    private Map<String, String> templateBetRecordEsNameMap;

    public boolean supportWarmUp(BetTypeEnum betType, TemplateMultipleRange multipleRange) {
        BigDecimal endOdds = templateWarmUpEndOddsMap.get(betType);
        AssertUtil.assertNotNull(endOdds, ErrorCode.ERR_INTER);
        return multipleRange.getEnd().compareTo(endOdds) <= 0;
    }

    public void validate() {
        //rewardConfigValidate();
        //inventoryConfigValidate();
    }

    private void templateRangeConfigValidate() {
        //AssertUtil.assertNotNull(this.templateRangeConfig, ErrorCode.ERR_INTER, "templateRangeConfig is null");
        //this.templateWarmUpEndOddsMapValidate(this.templateRangeConfig.getTemplateWarmUpEndOddsMap());
        //this.templateMappingValidate(this.templateRangeConfig.getTemplateMapping());
        //this.templateRangeMapValidate(this.templateRangeConfig.getTemplateRangeMap());
    }

    private void templateWarmUpEndOddsMapValidate(Map<BetTypeEnum, BigDecimal> templateWarmUpEndOddsMap) {
        AssertUtil.assertNotNull(templateWarmUpEndOddsMap, ErrorCode.ERR_INTER, "templateRangeConfig.templateRangeMap is null");
        for (BetTypeEnum betType : BetTypeEnum.values()) {
            AssertUtil.assertNotNull(templateWarmUpEndOddsMap.get(betType), ErrorCode.ERR_INTER, "templateWarmUpEndOddsMap.get(betType) is null of " + betType.getCode());
        }
    }

    private void templateMappingValidate(Map<AbTypeEnum, TemplateTypeEnum> templateMapping) {
        AssertUtil.assertNotNull(templateMapping, ErrorCode.ERR_INTER, "templateRangeConfig.templateMapping is null");
        for (AbTypeEnum abType : AbTypeEnum.values()) {
            // 每个AB类型只能有一个模板
            AssertUtil.assertNotNull(templateMapping.get(abType), ErrorCode.ERR_INTER, "templateRangeConfig.templateMapping.get(abType) is null of " + abType.getId());
            // 模版必须是diwata 的gameType 类型
            AssertUtil.assertTrue(Objects.equals(templateMapping.get(abType).getGameType(), GameTypeEnum.DIWATA_MAYARI), ErrorCode.ERR_INTER, "templateRangeConfig.templateMapping.get(abType) is not DIWATA_MAYARI of " + abType.getId());
        }
    }

    private void templateRangeMapValidate(Map<TemplateTypeEnum, Map<BetTypeEnum, Map<String, Integer>>> templateRangeMap) {
        AssertUtil.assertNotNull(templateRangeMap, ErrorCode.ERR_INTER, "templateRangeConfig.templateRangeMap is null");
        templateRangeMap.forEach((templateType, betTypeMap) -> {
            // 必须是diwata 的gameType 类型
            AssertUtil.assertTrue(Objects.equals(templateType.getGameType(), GameTypeEnum.DIWATA_MAYARI), ErrorCode.ERR_INTER, "templateRangeConfig.templateRangeMap.get(templateType) is not DIWATA_MAYARI of " + templateType.name());
            // betTypeMap必须配置完
            AssertUtil.assertNotNull(betTypeMap, ErrorCode.ERR_INTER, "templateRangeConfig.templateRangeMap.get(templateType) is null of " + templateType.name());
            AssertUtil.assertNotNull(betTypeMap.get(BetTypeEnum.NORMAL_BET), ErrorCode.ERR_INTER, "templateRangeConfig.templateRangeMap.get(templateType).get(BetTypeEnum.NORMAL_BET) is null of " + templateType.name() + " BetTypeEnum.NORMAL_BET");
            AssertUtil.assertNotNull(betTypeMap.get(BetTypeEnum.BUY_BET), ErrorCode.ERR_INTER, "templateRangeConfig.templateRangeMap.get(templateType).get(BetTypeEnum.BUY_BET) is null of " + templateType.name() + " BetTypeEnum.BUY_BET");

            betTypeMap.forEach((betType, rangeMap) -> {
                rangeMap.forEach((range, value) -> {
                    TemplateMultipleRange multipleRange = TemplateMultipleRange.build(range);
                    AssertUtil.assertNotNull(multipleRange, ErrorCode.ERR_INTER, "templateRangeConfig.templateRangeMap.get(templateType).get(betType).get(range) is null of " + templateType.name() + " " + betType.getCode() + " " + range);
                    AssertUtil.assertTrue(multipleRange.getStart().compareTo(BigDecimal.ZERO) >= 0, ErrorCode.ERR_INTER, "templateRangeConfig.templateRangeMap.get(templateType).get(betType).get(range) is must great than 0 of " + templateType.name() + " " + betType.getCode());
                    AssertUtil.assertTrue(multipleRange.getEnd().compareTo(multipleRange.getStart()) >= 0, ErrorCode.ERR_INTER, "templateRangeConfig.templateRangeMap.get(templateType).get(betType).get(range) is must great than start of " + templateType.name() + " " + betType.getCode() + " " + range);
                    AssertUtil.assertTrue(value >= 0, ErrorCode.ERR_INTER, "templateRangeConfig.templateRangeMap.get(templateType).get(betType).get(range) is must great than 0 of " + templateType.name() + " " + betType.getCode() + " " + range);
                });
            });
        });
    }

    //private void rewardConfigValidate() {
    //    Assert.notNull(rewardConfig, "rewardConfig is null");
    //    Assert.isTrue(MapUtils.isNotEmpty(rewardConfig.getRewardBlankProbability()), "rewardConfig Err");
    //    Assert.isTrue(MapUtils.isNotEmpty(rewardConfig.getRewardConfigMap()), "rewardConfig Err");
    //    Assert.isTrue(MapUtils.isNotEmpty(rewardConfig.getRewardFloorMultiple()), "rewardConfig Err");
    //    // 校验-轮空率配置
    //    rewardConfig.getRewardBlankProbability().entrySet().forEach(item -> {
    //        Assert.isTrue(Objects.equals(item.getKey(), BetTypeEnum.NORMAL_BET), "rewardConfig Err");
    //        Assert.notNull(item.getValue(), "rewardConfig error");
    //        Assert.isTrue(item.getValue().compareTo(BigDecimal.ZERO) > 0, "rewardConfig Err");
    //        Assert.isTrue(item.getValue().compareTo(BigDecimal.ONE) < 0, "rewardConfig Err");
    //        int actualScale = item.getValue().stripTrailingZeros().scale();
    //        // 轮空率最大支持四位小数
    //        Assert.isTrue(actualScale >= 0 && actualScale <= 4, "rewardConfig Err");
    //    });
    //    // 校验-奖励配置map校验
    //    Assert.isTrue(MapUtils.isNotEmpty(rewardConfig.getRewardConfigMap()), "rewardConfig Err");
    //    rewardConfig.getRewardConfigMap().entrySet().forEach(item -> {
    //        Assert.notNull(item.getKey(), "rewardConfig Err");
    //        Assert.isTrue(MapUtils.isNotEmpty(item.getValue()), "rewardConfig Err");
    //        TreeMap<String, TreeMap<String, Integer>> treeMap = item.getValue();
    //        treeMap.entrySet().forEach(treeMapItem -> {
    //            Assert.isTrue(StringUtils.isNotEmpty(treeMapItem.getKey()), "rewardConfig Err");
    //            Assert.isTrue(MapUtils.isNotEmpty(treeMapItem.getValue()), "rewardConfig Err");
    //            treeMapItem.getValue().entrySet().forEach(subItem -> {
    //                validateRange(subItem.getKey());
    //                Assert.isTrue(subItem.getValue() >= 0, "rewardConfig inventory range weight must >= 0");
    //            });
    //        });
    //    });
    //    // 校验-中奖兜底赔率配置
    //    Arrays.stream(BetTypeEnum.values()).forEach(item -> {
    //        String oddsRangeStr = rewardConfig.getRewardFloorMultiple().get(item);
    //        Assert.notEmpty(oddsRangeStr, "rewardConfig rewardFloorOdds ERR");
    //        validateRange(oddsRangeStr);
    //    });
    //}
    //
    //private void validateRange(String rangeStr) {
    //    Assert.isTrue(rangeStr.contains(COMMON_CONFIG_SPLIT), "range not contain '_'");
    //    String[] rangeArr = rangeStr.split(COMMON_CONFIG_SPLIT);
    //    Assert.isTrue(rangeArr.length == 2, "range length is not equals 2");
    //    Assert.isTrue(NumberUtil.isNumber(rangeArr[0]), "range first number must be number");
    //    Assert.isTrue(NumberUtil.isNumber(rangeArr[1]), "range second number must be number");
    //    Assert.isTrue(new BigDecimal(rangeArr[0]).compareTo(new BigDecimal(rangeArr[1])) <= 0, "range must left <= right");
    //}
    //
    //private void inventoryConfigValidate() {
    //    Assert.notNull(inventoryConfig, "inventoryConfig is null");
    //    Assert.isTrue(MapUtils.isNotEmpty(inventoryConfig.getInventoryConfigMap()), "inventoryConfig.inventoryConfigMap is null");
    //    Arrays.stream(InventoryTypeEnum.values()).forEach(item -> {
    //        InventoryConfigItem inventoryConfigItem = inventoryConfig.getInventoryConfigMap().get(item);
    //        Assert.notNull(inventoryConfigItem, "inventoryConfig.inventoryConfigMap.inventoryConfigItem is null");
    //        inventoryConfigItem.validate();
    //    });
    //}
}
