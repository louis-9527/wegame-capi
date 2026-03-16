package com.wegame.capi.biz.config.domain;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.common.enums.BizInterfereSubTypeEnum;
import com.wegame.capi.common.enums.BizInterfereTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * 业务干预配置
 */
@Slf4j
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BizInterfereConfig implements Serializable {
    private static final long serialVersionUID = 436784321L;
    /**
     * 业务控制总开关
     * 默认关闭
     */
    private boolean enabled = false;
    /**
     * 倍率控制配置
     */
    private MultipleControlConfig multipleControlConfig = new MultipleControlConfig();

    /**
     * 倍率控制 配置
     */
    @Slf4j
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MultipleControlConfig {
        /**
         * 业务控制开关
         * 默认打开
         */
        private boolean enabled = true;
        /**
         * 奖池配置
         */
        private Map<BizInterfereSubTypeEnum, MultipleControlConfigMappingInfo> multipleControlConfigMap;

        @Slf4j
        @Data
        @SuperBuilder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MultipleControlConfigMappingInfo {
            /**
             * 生效模版倍率范围
             */
            private String usableTemplateMultipleRange;
            /**
             * 投注金额进入奖池比例
             */
            private BigDecimal betIntoRewardRatio;
            /**
             * 奖池生效金额(保底)
             */
            private Long rewardUsableAmount;
        }

    }

    public Map<BizInterfereSubTypeEnum, MultipleControlConfig.MultipleControlConfigMappingInfo> getMultipleControlConfigMap() {
        if (!this.enabled) {
            throw new BusinessException(ErrorCode.ERR_INTER, "bizInterfereConfig enabled is closed");
        }
        if (!this.multipleControlConfig.enabled) {
            throw new BusinessException(ErrorCode.ERR_INTER, "bizInterfereConfig multipleControlConfig enabled is closed");
        }
        return this.multipleControlConfig.multipleControlConfigMap;
    }

    public MultipleControlConfig.MultipleControlConfigMappingInfo getMultipleControlConfigMappingInfo(BizInterfereSubTypeEnum rewardType) {
        AssertUtil.assertNotNull(rewardType, ErrorCode.ERR_INTER, "rewardType is null");
        AssertUtil.assertTrue(Objects.equals(rewardType.getBizInterfereType(), BizInterfereTypeEnum.MULTIPLE_CONTROL), ErrorCode.ERR_INTER, "rewardType must belong to BizInterfereTypeEnum.MULTIPLE_CONTROL");
        return this.getMultipleControlConfigMap().get(rewardType);
    }

    public boolean isMultipleControlEnabled() {
        return this.enabled && this.multipleControlConfig.enabled;
    }

    public void validate() {
        if (!enabled) {
            return;
        }

        // 高倍率控制配置检验
        this.validateMultipleControlValidate();
    }

    private void validateMultipleControlValidate() {
        if (!multipleControlConfig.enabled) {
            return;
        }
        AssertUtil.assertNotNull(this.multipleControlConfig.multipleControlConfigMap, ErrorCode.ERR_PARAM, "multipleControlConfigMap is null");
        Map<BizInterfereSubTypeEnum, MultipleControlConfig.MultipleControlConfigMappingInfo> multipleControlConfigMap = this.multipleControlConfig.multipleControlConfigMap;
        BigDecimal totalBetIntoRewardRatio = multipleControlConfigMap.values().stream().map(MultipleControlConfig.MultipleControlConfigMappingInfo::getBetIntoRewardRatio).reduce(BigDecimal.ZERO, BigDecimal::add);
        AssertUtil.assertTrue(totalBetIntoRewardRatio.compareTo(new BigDecimal("100")) <= 0, ErrorCode.ERR_PARAM, "totalBetIntoRewardRatio is greater than 100%");
        multipleControlConfigMap.forEach((subType, mappingInfo) -> {
            AssertUtil.assertStrNotEmpty(mappingInfo.usableTemplateMultipleRange, ErrorCode.ERR_PARAM, "usableTemplateMultipleRange is empty");
            AssertUtil.assertTrue(mappingInfo.getBetIntoRewardRatio().compareTo(new BigDecimal("0")) >= 0, ErrorCode.ERR_PARAM, "betIntoRewardRatio is less than 0");
            AssertUtil.assertTrue(mappingInfo.getRewardUsableAmount() >= 0, ErrorCode.ERR_PARAM, "rewardUsableAmount is must be great than 0");
        });
    }
}
