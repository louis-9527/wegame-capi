package com.wegame.capi.biz.module.base.playerintervention.compant;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.intervention.*;
import com.wegame.capi.biz.module.base.playerintervention.AbstractNodeComponent;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.constants.InterventionRuleIdConstants;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;

/**
 * fg限制干预
 */
@Slf4j
@LiteflowComponent(id = InterventionRuleIdConstants.FG_LIMIT_INTERVENTION)
public class FgLimitIntervention extends AbstractNodeComponent<FgLimitIntervention.FgLimitInterventionConfigDo> {
    /**
     * 玩家使用普通投注时
     * 累计游戏局数 <= 200
     * 击中FG
     * 模板赔率 >=  30
     */
    @Override
    protected boolean triggerCondition(PlayerInterventionParamDo paramDo, FgLimitInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        UserMetricInfoDo userMetricInfoDo = paramDo.getUserMetricInfoDo();
        AssertUtil.assertNotNull(userMetricInfoDo, ErrorCode.ERR_PARAM, "userMetricInfoDo is null");
        AssertUtil.assertNotNull(paramDo.getBetType(), ErrorCode.ERR_PARAM, "betType is null");
        List<BetTypeEnum> supportBetTypes = Optional.ofNullable(config.getSupportBetTypeList()).orElse(Collections.emptyList());
        int todayBetTimes = userMetricInfoDo.getTodayBetTimes();
        BetRecordTemplateEsDo betRecordTemplateEsDo = paramDo.getBetRecordTemplateEsDo();
        AssertUtil.assertNotNull(betRecordTemplateEsDo, ErrorCode.ERR_PARAM, "fgLimitIntervention,betRecordTemplateEsDo is null");
        boolean hasFreeGame = Optional.ofNullable(betRecordTemplateEsDo.getHasFreeGame()).orElse(false);
        Double templateMultiple = Optional.ofNullable(betRecordTemplateEsDo.getMultiple()).orElse(0d);

        return supportBetTypes.contains(paramDo.getBetType()) &&
                todayBetTimes <= config.getMaxTotalGameCount() &&
                hasFreeGame &&
                new BigDecimal(templateMultiple).compareTo(config.getMinTemplateMultiple()) >= 0   ;
    }

    @Override
    protected boolean triggerProbability(PlayerInterventionParamDo paramDo, FgLimitInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        return super.fixedTriggerProbability(paramDo, config, contextDo);
    }

    @Override
    protected boolean repeatCondition(PlayerInterventionParamDo paramDo, FgLimitInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        return super.commonRepeatCondition(paramDo, config, contextDo);
    }

    @Override
    protected void intervention(PlayerInterventionParamDo paramDo, FgLimitInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        // todo 重试逻辑补充
        super.buildInterventionResult(paramDo,config, contextDo,null,null,null);
    }

    @Data
    public static class FgLimitInterventionConfigDo implements InterventionConfig {
        /**
         * 支持的投注类型
         * {@link DefaultInterventionConfigDo#getSupportBetTypeList()}
         */
        private List<BetTypeEnum> supportBetTypeList;
        /**
         * 最大累计游戏局数
         * {@link DefaultInterventionConfigDo#getMaxTotalGameCount()}
         */
        private Integer maxTotalGameCount;
        /**
         * 是否击中fg
         * {@link DefaultInterventionConfigDo#isHitFg()}
         */
        private boolean isHitFg;
        /**
         * 最小模版倍率
         * {@link DefaultInterventionConfigDo#getMinTemplateMultiple()}
         */
        private BigDecimal minTemplateMultiple;
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
        /**
         * 干预重试次数
         * {@link DefaultInterventionConfigDo#getInterventionRetryCount()}
         */
        private int interventionRetryCount;

    }
}
