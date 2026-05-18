package com.wegame.capi.biz.config.domain.common;

import cn.hutool.core.lang.Assert;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.TemplateIndexTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

import static com.wegame.capi.common.Constants.DEFAULT;

/**
 * 通用模版配置模型
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommonTemplateConfig implements Serializable {
    /**
     * 模版预热Map</br>
     * 投注类型 —> 最大倍率（预热范围：[0,最大倍率]）, -1则不预热
     */
    private Map<BetTypeEnum, BigDecimal> templateWarmUpEndOddsMap;
    /**
     * 模版索引Map</br>
     * 模版索引类型 -> 索引名称
     */
    private Map<TemplateIndexTypeEnum, String> templateIndexMap;
    /**
     * rtp倍率范围权重Map<br/>
     * Map<rtp名称(自定义)，Map<投注类型，Map<左开倍率_右闭倍率，权重>>>
     */
    private TreeMap<String, TreeMap<BetTypeEnum, TreeMap<String, Integer>>> rtpMultipleRangeWeightMap;
    /**
     * 中奖控制Map<br/>
     * Map<名称(自定义)，Map<名称(各游戏自定义)，rtp名称>>
     */
    private TreeMap<String, TreeMap<String, String>> rewardControlMap;

    public boolean supportWarmUp(BetTypeEnum betType, TemplateMultipleRange multipleRange) {
        BigDecimal endOdds = templateWarmUpEndOddsMap.get(betType);
        AssertUtil.assertNotNull(endOdds, ErrorCode.ERR_INTER);
        return multipleRange.getEnd().compareTo(endOdds) <= 0;
    }

    public String getIndexName(TemplateIndexTypeEnum templateIndexType) {
        return templateIndexMap.get(templateIndexType);
    }

    public TreeMap<String, Integer> getMultipleWeightMap(String key, String subKey, BetTypeEnum betType) {
        TreeMap<String, String> map = rewardControlMap.get(key);
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
        String rtpName = map.get(subKey);
        AssertUtil.assertStrNotEmpty(rtpName, ErrorCode.ERR_INTER);
        TreeMap<BetTypeEnum, TreeMap<String, Integer>> rtpMap = rtpMultipleRangeWeightMap.get(rtpName);
        AssertUtil.assertTrue(MapUtils.isNotEmpty(rtpMap), ErrorCode.ERR_INTER);
        TreeMap<String, Integer> ret = rtpMap.get(betType);
        AssertUtil.assertTrue(MapUtils.isNotEmpty(ret), ErrorCode.ERR_INTER);
        return ret;
    }

    public void validate() {
        validateTemplateWarmUpEndOddsMap();
        validateTemplateIndexMap();
        validateRtpMultipleRangeWeightMap();
        validateRewardControlMap();
    }

    private void validateTemplateWarmUpEndOddsMap() {
        AssertUtil.assertNotNull(templateWarmUpEndOddsMap, ErrorCode.ERR_INTER, "templateWarmUpEndOddsMap is null");
        for (BetTypeEnum betType : BetTypeEnum.values()) {
            AssertUtil.assertNotNull(templateWarmUpEndOddsMap.get(betType), ErrorCode.ERR_INTER, "templateWarmUpEndOddsMap.get(betType) is null of " + betType.getCode());
        }
    }

    private void validateTemplateIndexMap() {
        AssertUtil.assertNotNull(templateIndexMap, ErrorCode.ERR_INTER, "templateIndexMap is null");
        for (TemplateIndexTypeEnum type : TemplateIndexTypeEnum.values()) {
            AssertUtil.assertNotNull(templateIndexMap.get(type), ErrorCode.ERR_INTER, "templateIndexMap.get(type) is null of " + type.getCode());
        }
    }

    private void validateRtpMultipleRangeWeightMap() {
        Assert.notNull(rtpMultipleRangeWeightMap, "rtpMultipleRangeWeightMap is null");
        Assert.isTrue(MapUtils.isNotEmpty(rtpMultipleRangeWeightMap), "rtpMultipleRangeWeightMap is empty");
        rtpMultipleRangeWeightMap.entrySet().forEach(item -> {
            TreeMap<BetTypeEnum, TreeMap<String, Integer>> subMap = item.getValue();
            Assert.isTrue(MapUtils.isNotEmpty(subMap), "rtpMultipleRangeWeightMap subMap is empty");
            for (BetTypeEnum betType : BetTypeEnum.values()) {
                Map<String, Integer> rangeMap = subMap.get(betType);
                Assert.isTrue(MapUtils.isNotEmpty(rangeMap), "rangeMap is empty");
                rangeMap.forEach((range, weight) -> {
                    TemplateMultipleRange multipleRange = TemplateMultipleRange.build(range);
                    AssertUtil.assertNotNull(multipleRange, ErrorCode.ERR_INTER, "rangeMap range is error: " + range);
                    AssertUtil.assertTrue(multipleRange.getStart().compareTo(BigDecimal.ZERO) >= 0, ErrorCode.ERR_INTER, "rangeMap range is error: " + range);
                    AssertUtil.assertTrue(multipleRange.getEnd().compareTo(multipleRange.getStart()) >= 0, ErrorCode.ERR_INTER, "rangeMap range is error: " + range);
                    AssertUtil.assertTrue(weight >= 0, ErrorCode.ERR_INTER, "rangeMap weight is error: " + weight);
                });
            }
        });
    }

    private void validateRewardControlMap() {
        Assert.notNull(rewardControlMap, "rewardControlMap is null");
        Assert.isTrue(MapUtils.isNotEmpty(rewardControlMap), "rewardControlMap is empty");
        Assert.notNull(rewardControlMap.get(DEFAULT), "rewardControlMap defaultVal is null");
        Assert.isTrue(MapUtils.isNotEmpty(rewardControlMap.get(DEFAULT)), "rewardControlMap defaultVal is empty");
        rewardControlMap.entrySet().forEach(item -> {
            Map<String, String> subMap = item.getValue();
            Assert.isTrue(MapUtils.isNotEmpty(subMap), "rewardControlMap subMap is empty");
            subMap.keySet().forEach(rtpName -> {
                TreeMap<BetTypeEnum, TreeMap<String, Integer>> rtpMap = rtpMultipleRangeWeightMap.get(rtpName);
                Assert.isTrue(MapUtils.isNotEmpty(rtpMap), rtpName + " : rtpMap is empty");
            });
        });
    }
}
