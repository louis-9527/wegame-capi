package com.wegame.capi.biz.module.base.intervention.component;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.intervention.*;
import com.wegame.capi.biz.module.base.intervention.AbstractNodeComponent;
import com.wegame.capi.common.constants.InterventionRuleIdConstants;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 次留干预
 */
@Slf4j
@LiteflowComponent(id = InterventionRuleIdConstants.RETENTION_USER_INTERVENTION)
public class RetentionUserIntervention extends AbstractNodeComponent<RetentionUserIntervention.RetentionUserInterventionConfigDo> {

    /**
     * 昨日有投注记录。
     * 今日累计游戏局数 <= 200
     */
    @Override
    protected boolean triggerCondition(PlayerInterventionParamDo paramDo, RetentionUserInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        UserMetricInfoDo userMetricInfoDo = paramDo.getUserMetricInfoDo();
        AssertUtil.assertNotNull(userMetricInfoDo, ErrorCode.ERR_PARAM);
        return Objects.nonNull(userMetricInfoDo.getYesterdayHasBet()) && userMetricInfoDo.getYesterdayHasBet() &&
                userMetricInfoDo.getTodayBetTimes() <= config.getMaxTodayGameCount();
    }

    @Override
    protected boolean triggerProbability(PlayerInterventionParamDo paramDo, RetentionUserInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        return super.fixedTriggerProbability(paramDo, config, contextDo);
    }

    @Override
    protected boolean repeatCondition(PlayerInterventionParamDo paramDo, RetentionUserInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        return super.commonRepeatCondition(paramDo, config, contextDo);
    }

    @Override
    protected void intervention(PlayerInterventionParamDo paramDo, RetentionUserInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        super.buildInterventionResult(paramDo, config, contextDo, null, null, null);
    }

    @Data
    public static class RetentionUserInterventionConfigDo implements InterventionConfig {
        /**
         * 最大今日
         * {@link DefaultInterventionConfigDo#getMaxTodayGameCount()}
         */
        private Integer maxTodayGameCount;
        /**
         * 触发概率
         * {@link DefaultInterventionConfigDo#getTriggerProbability()}
         */
        private BigDecimal triggerProbability;
        /**
         * 重复类型
         * @see com.wegame.capi.common.enums.RepeatTypeEnum
         * {@link DefaultInterventionConfigDo#getRepeatType()}
         */
        private String repeatType;
        /**
         * 干预模版name
         * eg:98rtp
         * {@link DefaultInterventionConfigDo#getRtpName()}
         */
        private String rtpName;

    }


}
