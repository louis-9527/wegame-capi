package com.wegame.capi.biz.config.domain.common;

import cn.hutool.core.lang.Assert;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
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
     * rtp倍率范围权重Map<br/>
     * Map<rtp名称(自定义)，Map<投注类型，Map<左开倍率_右闭倍率，权重>>>
     */
    private TreeMap<String, Rtp> rtpMap;
    /**
     * rtp控制Map<br/>
     * Map<名称(自定义)，Map<名称(各游戏自定义)，rtp名称>>
     */
    private TreeMap<String, TreeMap<String, String>> rtpControlMap;

    public boolean supportWarmUp(BetTypeEnum betType, TemplateMultipleRange multipleRange) {
        BigDecimal endOdds = templateWarmUpEndOddsMap.get(betType);
        return Objects.nonNull(endOdds) && multipleRange.getEnd().compareTo(endOdds) <= 0;
    }

    public Rtp getRtp(String key, String subKey) {
        TreeMap<String, String> map = rtpControlMap.get(key);
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
        String rtpName = map.get(subKey);
        AssertUtil.assertStrNotEmpty(rtpName, ErrorCode.ERR_INTER);
        return rtpMap.get(rtpName);
    }

    public void validate() {
        validateTemplateWarmUpEndOddsMap();
        validateRtpMap();
        validateRewardControlMap();
    }

    private void validateTemplateWarmUpEndOddsMap() {
        AssertUtil.assertNotNull(templateWarmUpEndOddsMap, ErrorCode.ERR_INTER, "templateWarmUpEndOddsMap is null");
        for (BetTypeEnum betType : BetTypeEnum.values()) {
            AssertUtil.assertNotNull(templateWarmUpEndOddsMap.get(betType), ErrorCode.ERR_INTER, "templateWarmUpEndOddsMap.get(betType) is null of " + betType.getCode());
        }
    }

    private void validateRtpMap() {
        Assert.isTrue(MapUtils.isNotEmpty(rtpMap), "rtpMap is empty");
        rtpMap.entrySet().forEach(item -> {
            Rtp rtp = item.getValue();
            String rtpName = item.getKey();
            Assert.notNull(rtp,  rtpName + " : rtp is null");
            Assert.notEmpty(rtp.getIndexName(), rtpName + "indexName is empty");
            TreeMap<BetTypeEnum, TreeMap<String, Integer>> multipleRangeWeightMap = rtp.getMultipleRangeWeightMap();
            for (BetTypeEnum betType : BetTypeEnum.values()) {
                Map<String, Integer> rangeMap = multipleRangeWeightMap.get(betType);
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
        Assert.notNull(rtpControlMap, "rewardControlMap is null");
        Assert.isTrue(MapUtils.isNotEmpty(rtpControlMap), "rewardControlMap is empty");
        Assert.notNull(rtpControlMap.get(DEFAULT), "rewardControlMap defaultVal is null");
        Assert.isTrue(MapUtils.isNotEmpty(rtpControlMap.get(DEFAULT)), "rewardControlMap defaultVal is empty");
        rtpControlMap.entrySet().forEach(item -> {
            Map<String, String> subMap = item.getValue();
            Assert.isTrue(MapUtils.isNotEmpty(subMap), "rewardControlMap subMap is empty");
            subMap.keySet().forEach(rtpName -> {
                Rtp rtp = rtpMap.get(rtpName);
                Assert.notNull(rtp, rtpName + " : rtp is null");
            });
        });
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rtp implements Serializable {
        /**
         * es 索引名称
         */
        private String indexName;
        /**
         * rtp倍率范围权重Map<br/>
         * Map<投注类型，Map<左开倍率_右闭倍率，权重>>
         */
        private TreeMap<BetTypeEnum, TreeMap<String, Integer>> multipleRangeWeightMap;
    }
}
