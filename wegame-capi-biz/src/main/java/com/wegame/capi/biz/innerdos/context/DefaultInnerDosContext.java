package com.wegame.capi.biz.innerdos.context;

import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.metrics.GameMetricsDo;
import com.wegame.capi.biz.domain.metrics.UserPlayMetricsDo;
import com.wegame.capi.biz.innerdos.config.InnerDosCondition;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.innerdos.GameInnerDosTypeEnum;
import com.wegame.capi.common.utils.RtpUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 内部干预上下文(默认)
 */

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DefaultInnerDosContext implements InnerDosContext {

    /**
     * gamePlayDo
     */
    private GamePlayDo gamePlayDo;

    /**
     * 模版权重配置
     */
    private DefaultTemplateRangeConfig defaultTemplateRangeConfig;
    /**
     * 内部干预条件
     * 1.常态条件配置
     * 2.常态条件配置
     * 3.rtp干预配置(不需要)
     */
    private InnerDosCondition innerDosCondition;
    /**
     * 用户指标
     */
    private UserPlayMetricsDo userPlayMetricsDo;
    /**
     * 大盘指标数据
     */
    private GameMetricsDo gameMetricsDo;

    @Override
    public GamePlayDo getGamePlayDo() {
        return this.gamePlayDo;
    }

    @Override
    public UserPlayMetricsDo getUserPlayMetricsDo() {
        return this.userPlayMetricsDo;
    }

    @Override
    public GameMetricsDo getGameMetricsDo() {
        return this.gameMetricsDo;
    }

    @Override
    public Map<String, BigDecimal> getTemplateRangeMap(Object... args) {
        GameInnerDosTypeEnum typeName = (GameInnerDosTypeEnum) args[0];
        BetSubTypeEnum templateName = (BetSubTypeEnum) args[1];
        return defaultTemplateRangeConfig.getTemplateRangeMap(typeName, templateName);
    }

    public boolean isNewPlayer(Object... args) {
        // 累计游戏局数是否小于配置的最大局数
        Long totalGameCount = this.userPlayMetricsDo.getTotalGameCount();
        InnerDosCondition.NormalCondition newPlayerCondition = innerDosCondition.getNewPlayerCondition();
        return totalGameCount <= newPlayerCondition.getRoundsCountLimit();
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class DefaultTemplateRangeConfig {
        /**
         * 类型映射模版
         * 支持两层映射类型配置
         * Map<String, Map<String, Map<String, Integer>>>
         * 如：
         * Map<96.5, Map<Normal, Map<1-10, 100>>>
         * Map<96.5, Map<ex, Map<1-10, 100>>>
         */
        private LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, BigDecimal>>> templateRangeMap;

        /**
         * 大盘内部干预类型和模版名称映射
         * 如：
         * Map<TOO_LOW_DOS, "96.5">
         */
        private Map<GameInnerDosTypeEnum, String> dosTypeToTemplateNameMapping;

        public Map<String, BigDecimal> getTemplateRangeMap(GameInnerDosTypeEnum dosTypeEnum, BetSubTypeEnum templateName) {
            String typeName = dosTypeToTemplateNameMapping.get(dosTypeEnum);
            return this.templateRangeMap.get(typeName).get(templateName.getCode());
        }

        public Map<String, LinkedHashMap<String, BigDecimal>> getTemplateRangeMap(GameInnerDosTypeEnum dosTypeEnum) {
            String typeName = dosTypeToTemplateNameMapping.get(dosTypeEnum);
            return this.templateRangeMap.get(typeName);
        }
    }

    /**
     * 是否触发新人空盘限制
     *
     * @return true/false
     */
    public boolean triggerNewPlayerEmptyDisk() {
        return this.userPlayMetricsDo.getContinuousLostCount()
                >= this.innerDosCondition.getNewPlayerCondition().getEmptyDiskLimit();
    }

    /**
     * 是否触发常态空盘限制
     *
     * @return true/false
     */
    public boolean triggerNormalPlayerEmptyDisk() {
        return this.userPlayMetricsDo.getContinuousLostCount()
                >= this.innerDosCondition.getNormalCondition().getEmptyDiskLimit();
    }

    /**
     * 根据dos类型获取 模版名称
     *
     * @param dosTypeEnum dos类型枚举
     * @return 模版名称
     */
    public String getTemplateName(GameInnerDosTypeEnum dosTypeEnum) {
        return defaultTemplateRangeConfig.getDosTypeToTemplateNameMapping().get(dosTypeEnum);
    }

    /**
     * 获取用户当前rtp
     *
     * @return rtp
     */
    public BigDecimal getUserCurrentRtp() {
        Long totalBetAmount = this.userPlayMetricsDo.getTotalBetAmount();
        Long totalPayoutAmount = this.userPlayMetricsDo.getTotalPayoutAmount();
        return RtpUtil.calculateRtp(totalBetAmount, totalPayoutAmount);
    }

    /**
     * 获取新玩家DOS干预系数
     *
     * @return DOS干预系数
     */
    public BigDecimal getNewPlayerDosCoefficient() {
        return this.innerDosCondition.getNewPlayerCondition().getDosCoefficient();
    }

    /**
     * 获取常态DOS干预系数
     *
     * @return DOS干预系数
     */
    public BigDecimal getNormalDosCoefficient() {
        return this.innerDosCondition.getNewPlayerCondition().getDosCoefficient();
    }

}
