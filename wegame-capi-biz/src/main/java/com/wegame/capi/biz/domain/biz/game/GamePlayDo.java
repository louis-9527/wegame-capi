package com.wegame.capi.biz.domain.biz.game;

import com.wegame.capi.biz.domain.biz.bizinterfere.BizInterfereContextDo;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * 游戏模型
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GamePlayDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 游戏Play类型
     */
    private GamePlayTypeEnum gamePlayType;
    /**
     * 游戏类型
     */
    private GameTypeEnum gameType;
    /**
     * 投注类型
     */
    private BetTypeEnum betType;
    /**
     * 投注子类型
     */
    private BetSubTypeEnum betSubType;
    /**
     * 投注金额
     */
    private Long betAmount;
    /**
     * 支付金额
     */
    private Long payAmount;
    /**
     * 投注记录id
     */
    private Long betRecordId;
    ///////////////
    /**
     * 干预事件配置-投注记录ID
     */
    private Long operationEventConfigBetRecordId;
    private GameOperationEventDo gameEventDo;
    /**
     * 投注营销类型：默认无
     */
    BetPromotionTypeEnum betPromotionType;
    /**
     * 对play 结果的验证数据源
     */
    private PlayValidateData playValidateData;

    /**
     * bizInteger context
     */
    private BizInterfereContextDo bizInterfereContext;

    public Long getAvailableBonus() {
        if (Objects.isNull(this.gameEventDo) || !this.gameEventDo.isEventResult() || Objects.isNull(this.gameEventDo.getOperationEvent())) {
            return null;
        }
        Long finishBonus = this.gameEventDo.getOperationEvent().getFinishBonus();
        Long maxBonus = this.gameEventDo.getOperationEvent().getMaxBonus();
        return maxBonus - finishBonus;
    }

    public void setOverallRewardAmount(long overallRewardAmount) {
        if (Objects.isNull(this.playValidateData)) {
            this.playValidateData = PlayValidateData.builder().build();
        }
        this.playValidateData.setOverallRewardAmount(overallRewardAmount);
    }

    public void setTemplateId(Long templateId) {
        if (Objects.isNull(this.playValidateData)) {
            this.playValidateData = PlayValidateData.builder().build();
        }
        this.playValidateData.setTemplateId(templateId);
    }

    public Long getOverallRewardAmount() {
        return Optional.ofNullable(this.playValidateData)
                .map(PlayValidateData::getOverallRewardAmount)
                .orElse(null);
    }

    public void setTemplateMultipleRange(TemplateMultipleRange templateMultipleRange) {
        if (Objects.isNull(this.playValidateData)) {
            this.playValidateData = PlayValidateData.builder().build();
        }
        this.playValidateData.setTemplateMultipleRange(templateMultipleRange);
    }

    public TemplateMultipleRange getTemplateMultipleRange() {
        return Optional.ofNullable(this.playValidateData)
                .map(PlayValidateData::getTemplateMultipleRange)
                .orElse(null);
    }

    /**
     * 对play 结果的验证数据源
     */
    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class PlayValidateData implements Serializable {
        private static final long serialVersionUID = -2266308277093710848L;
        /**
         * 支付金额
         */
        private Long overallRewardAmount;
        /**
         * aff templateId
         */
        private Long templateId;
        /**
         * 倍率范围.适用于templatePlay
         */
        private TemplateMultipleRange templateMultipleRange;
    }

}

