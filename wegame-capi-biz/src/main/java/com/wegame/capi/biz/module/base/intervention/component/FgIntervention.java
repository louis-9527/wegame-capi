package com.wegame.capi.biz.module.base.intervention.component;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.intervention.*;
import com.wegame.capi.biz.module.base.intervention.AbstractNodeComponent;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.constants.InterventionRuleIdConstants;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;

/**
 * fg干预
 */
@Slf4j
@LiteflowComponent(id = InterventionRuleIdConstants.FG_INTERVENTION)
public class FgIntervention extends AbstractNodeComponent<FgIntervention.FgInterventionConfigDo> {

    @Override
    protected boolean triggerCondition(PlayerInterventionParamDo paramDo, FgInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        // 玩家使用普通投注时 && 当日不中FG次数 >= 100
        UserMetricInfoDo userMetricInfoDo = paramDo.getUserMetricInfoDo();
        AssertUtil.assertNotNull(userMetricInfoDo, ErrorCode.ERR_PARAM);
        BetTypeEnum betType = paramDo.getBetType();
        AssertUtil.assertNotNull(betType, ErrorCode.ERR_PARAM);
        int todayContinueNoFgTimes =userMetricInfoDo.getContinueNoFgTimes();
        List<BetTypeEnum> supportBetTypes = Optional.ofNullable(config.getSupportBetTypeList()).orElse(Collections.emptyList());
        return supportBetTypes.contains(betType) &&
                todayContinueNoFgTimes >= config.getMinTotalNoHitFgCount();
    }

    @Override
    protected boolean triggerProbability(PlayerInterventionParamDo paramDo, FgInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        UserMetricInfoDo userMetricInfoDo = paramDo.getUserMetricInfoDo();
        AssertUtil.assertNotNull(userMetricInfoDo, ErrorCode.ERR_PARAM, "userMetricInfoDo is null");
        Integer todayContinueNoFgTimes = Optional.ofNullable(userMetricInfoDo.getContinueNoFgTimes()).orElse(0);
        // 当日不中FG次数 X 0.5%
        return super.canTrigger(config.getTriggerProbabilityCoefficient().multiply(new BigDecimal(todayContinueNoFgTimes)));
    }

    @Override
    protected boolean repeatCondition(PlayerInterventionParamDo paramDo, FgInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        return super.commonRepeatCondition(paramDo, config, contextDo);
    }

    @Override
    protected void intervention(PlayerInterventionParamDo paramDo, FgInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        super.buildInterventionResult(paramDo, config, contextDo, null, null, null);
    }

    /**
     * @see DefaultInterventionConfigDo
     */
    @Data
    public static class FgInterventionConfigDo implements InterventionConfig {
        /**
         * 支持的投注类型
         * {@link DefaultInterventionConfigDo#getSupportBetTypeList()}
         */
        private List<BetTypeEnum> supportBetTypeList;
        /**
         * 最小不中fg次数
         * {@link DefaultInterventionConfigDo#getMinTotalNoHitFgCount()}
         */
        private Integer minTotalNoHitFgCount;
        /**
         * 触发概率系数
         * {@link DefaultInterventionConfigDo#getTriggerProbabilityCoefficient()}
         */
        private BigDecimal triggerProbabilityCoefficient;
        /**
         * 重复类型
         * @see com.wegame.capi.common.enums.RepeatTypeEnum
         * {@link DefaultInterventionConfigDo#getRepeatType()}
         */
        private String repeatType;
        /**
         * 干预模版name
         * eg:98rtp
         */
        private String rtpName;

    }
}
