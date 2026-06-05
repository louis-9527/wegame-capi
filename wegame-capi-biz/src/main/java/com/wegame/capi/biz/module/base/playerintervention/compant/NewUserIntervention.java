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
 * 新人干预
 */
@Slf4j
@LiteflowComponent(id = InterventionRuleIdConstants.NEW_USER_INTERVENTION)
public class NewUserIntervention extends AbstractNodeComponent<NewUserIntervention.NewUserInterventionConfigDo> {

    @Override
    protected boolean triggerCondition(PlayerInterventionParamDo paramDo, NewUserInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        UserMetricInfoDo userMetricInfoDo = paramDo.getUserMetricInfoDo();
        AssertUtil.assertNotNull(userMetricInfoDo, ErrorCode.ERR_PARAM);
        return userMetricInfoDo.isTodayRegister() && userMetricInfoDo.getTotalBetTimes() <= config.getMaxTotalGameCount();
    }

    @Override
    protected boolean triggerProbability(PlayerInterventionParamDo paramDo, NewUserInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        return super.fixedTriggerProbability(paramDo, config, contextDo);
    }

    @Override
    protected boolean repeatCondition(PlayerInterventionParamDo paramDo, NewUserInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        return super.commonRepeatCondition(paramDo, config, contextDo);
    }

    @Override
    protected void intervention(PlayerInterventionParamDo paramDo, NewUserInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        super.buildInterventionResult(paramDo, config, contextDo, null,null,null);
    }

    @Data
    public static class NewUserInterventionConfigDo implements InterventionConfig {
        /**
         * 是否当日注册
         * {@link DefaultInterventionConfigDo#isToDayRegister()}
         */
        private boolean isToDayRegister;
        /**
         * 最大累计游戏局数
         * {@link DefaultInterventionConfigDo#getMaxTotalGameCount()}
         */
        private Integer maxTotalGameCount;
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
         * 干预模版id
         * eg:98%类型
         * {@link DefaultInterventionConfigDo#getRtpName()}
         */
        private String rtpName;

    }
}
