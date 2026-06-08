package com.wegame.capi.biz.module.base.playerintervention.compant;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.intervention.*;
import com.wegame.capi.biz.module.base.playerintervention.AbstractNodeComponent;
import com.wegame.capi.common.constants.InterventionRuleIdConstants;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * rtp过低干预
 */
@Slf4j
@LiteflowComponent(id = InterventionRuleIdConstants.RTP_LOW_INTERVENTION)
public class RtpLowIntervention extends AbstractNodeComponent<RtpLowIntervention.RtpLowInterventionConfigDo> {

    @Override
    protected boolean triggerCondition(PlayerInterventionParamDo paramDo, RtpLowInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        UserMetricInfoDo userMetricInfoDo = paramDo.getUserMetricInfoDo();
        AssertUtil.assertNotNull(userMetricInfoDo, ErrorCode.ERR_PARAM);
        double rtp = userMetricInfoDo.getRtp();
        return rtp <= config.getMaxRtp().doubleValue();
    }

    @Override
    protected boolean triggerProbability(PlayerInterventionParamDo paramDo, RtpLowInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        return super.fixedTriggerProbability(paramDo, config, contextDo);
    }

    @Override
    protected boolean repeatCondition(PlayerInterventionParamDo paramDo, RtpLowInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        return super.commonRepeatCondition(paramDo, config, contextDo);
    }

    @Override
    protected void intervention(PlayerInterventionParamDo paramDo, RtpLowInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        super.buildInterventionResult(paramDo, config, contextDo, null, null, null);
    }

    @Data
    public static class RtpLowInterventionConfigDo implements InterventionConfig {
        /**
         * 最大rtp
         * {@link DefaultInterventionConfigDo#getMaxRtp()}
         */
        private BigDecimal maxRtp;
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
