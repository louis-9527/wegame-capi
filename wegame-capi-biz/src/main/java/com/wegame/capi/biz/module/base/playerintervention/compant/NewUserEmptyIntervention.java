package com.wegame.capi.biz.module.base.playerintervention.compant;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.intervention.*;
import com.wegame.capi.biz.module.base.playerintervention.AbstractNodeComponent;
import com.wegame.capi.common.constants.InterventionRuleIdConstants;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 新人空盘干预
 */
@Slf4j
@LiteflowComponent(id = InterventionRuleIdConstants.NEW_USER_EMPTY_INTERVENTION)
public class NewUserEmptyIntervention extends AbstractNodeComponent<NewUserEmptyIntervention.NewUserEmptyInterventionConfigDo> {
    /**
     * 游戏局数 <= 100
     * 连续空盘计数 >= 5
     * 本局结果为空盘
     */
    @Override
    protected boolean triggerCondition(PlayerInterventionParamDo paramDo, NewUserEmptyInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        UserMetricInfoDo userMetricInfoDo = paramDo.getUserMetricInfoDo();
        AssertUtil.assertNotNull(userMetricInfoDo, ErrorCode.ERR_PARAM, "userMetricInfoDo is null");
        int totalBetTimes = userMetricInfoDo.getTotalBetTimes();
        int continueEmptyTimes = userMetricInfoDo.getContinueEmptyTimes();
        BetRecordTemplateEsDo betRecordTemplateEsDo = paramDo.getBetRecordTemplateEsDo();
        AssertUtil.assertNotNull(betRecordTemplateEsDo, ErrorCode.ERR_PARAM, "betRecordTemplateEsDo is null");
        Double multiple = betRecordTemplateEsDo.getMultiple();

        return totalBetTimes <= config.getMaxTotalGameCount() &&
                continueEmptyTimes >= config.getMinContinuousEmptyCount() &&
                multiple <= 0
                ;
    }

    @Override
    protected boolean triggerProbability(PlayerInterventionParamDo paramDo, NewUserEmptyInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        return super.fixedTriggerProbability(paramDo, config, contextDo);
    }

    @Override
    protected boolean repeatCondition(PlayerInterventionParamDo paramDo, NewUserEmptyInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        return super.commonRepeatCondition(paramDo, config, contextDo);
    }

    @Override
    protected void intervention(PlayerInterventionParamDo paramDo, NewUserEmptyInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        super.buildInterventionResult(paramDo, config, contextDo, TemplateMultipleRange.build(config.getInterventionTemplateRange()), null, null);
    }

    /**
     * @see com.wegame.capi.biz.domain.intervention.DefaultInterventionConfigDo
     */
    @Data
    public static class NewUserEmptyInterventionConfigDo implements InterventionConfig {

        /**
         * 最大累计游戏局数
         * {@link DefaultInterventionConfigDo#getMaxTotalGameCount()}
         */
        private Integer maxTotalGameCount;
        /**
         * 最小连续空盘次数
         * {@link DefaultInterventionConfigDo#getMinContinuousEmptyCount()}
         */
        private Integer minContinuousEmptyCount;

        /**
         * 触发概率
         * {@link DefaultInterventionConfigDo#getTriggerProbability()}
         */
        private BigDecimal triggerProbability;

        /**
         * 重复类型
         *
         * @see com.wegame.capi.common.enums.RepeatTypeEnum
         * {@link DefaultInterventionConfigDo#getRepeatType()}
         */
        private String repeatType;
        /**
         * 干预模版倍率范围
         * eg:0-1
         * {@link DefaultInterventionConfigDo#getInterventionTemplateRange()}
         */
        private String interventionTemplateRange;
    }
}
