package com.wegame.capi.biz.module.base.playerintervention.compant;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.intervention.*;
import com.wegame.capi.biz.module.base.playerintervention.AbstractNodeComponent;
import com.wegame.capi.common.constants.InterventionRuleIdConstants;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**
 * 新人救命
 */
@Slf4j
@LiteflowComponent(id = InterventionRuleIdConstants.NEW_USER_HELP_INTERVENTION)
public class NewUserHelpIntervention extends AbstractNodeComponent<NewUserHelpIntervention.NewUserHelpInterventionConfigDo> {

    /**
     * 今日注册
     * 累计游戏局数 <= 100
     * 今日游戏局数 >= 20
     * 当前余额 <=  投注额*5
     * 总派彩/总投注 <= 80%
     */
    @Override
    protected boolean triggerCondition(PlayerInterventionParamDo paramDo, NewUserHelpInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        UserMetricInfoDo userMetricInfoDo = paramDo.getUserMetricInfoDo();
        AssertUtil.assertNotNull(userMetricInfoDo, ErrorCode.ERR_PARAM);
        boolean todayRegister = userMetricInfoDo.isTodayRegister();
        int totalBetTimes = userMetricInfoDo.getTotalBetTimes();
        int todayBetTimes = userMetricInfoDo.getTodayBetTimes();
        Long currentCredit = userMetricInfoDo.getCurrentBalance();
        if (Objects.isNull(currentCredit)){
            return false;
        }
        double rtp = userMetricInfoDo.getRtp();
        BigDecimal betAmount = new BigDecimal(paramDo.getBetAmount());

        return todayRegister &&
                totalBetTimes <= config.getMaxTotalGameCount() &&
                todayBetTimes >= config.getMinTodayGameCount() &&
                currentCredit <= betAmount.multiply(config.getBetAmountCoefficient()).longValue() &&
                rtp <= config.getMaxRtp().doubleValue()
                ;
    }

    @Override
    protected boolean triggerProbability(PlayerInterventionParamDo paramDo, NewUserHelpInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        return super.fixedTriggerProbability(paramDo, config, contextDo);
    }

    @Override
    protected boolean repeatCondition(PlayerInterventionParamDo paramDo, NewUserHelpInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        return super.commonRepeatCondition(paramDo, config, contextDo);
    }

    /**
     * 挑选 （本次进入游戏余额 / 本次投注额）X 0.8倍率的模板。
     * 倍率可以有±10%的误差。
     * 不成功走正常96%模板
     */
    @Override
    protected void intervention(PlayerInterventionParamDo paramDo, NewUserHelpInterventionConfigDo config, PlayerInterventionContextDo contextDo) {
        UserMetricInfoDo userMetricInfoDo = paramDo.getUserMetricInfoDo();
        AssertUtil.assertNotNull(userMetricInfoDo, ErrorCode.ERR_PARAM);
        long enterBalance = Optional.ofNullable(userMetricInfoDo.getEnterBalance()).orElse(0L);
        long payAmount = paramDo.getPayAmount();
        AssertUtil.assertTrue(payAmount>0, ErrorCode.ERR_PARAM);
        BigDecimal betAmountCoefficient = config.getBetAmountCoefficient();
        BigDecimal baseMultiple = BigDecimal.valueOf(enterBalance / (double) payAmount).multiply(betAmountCoefficient);
        BigDecimal start = baseMultiple.multiply(BigDecimal.ONE.subtract(config.getMultipleBias()));
        BigDecimal end = baseMultiple.multiply(BigDecimal.ONE.add(config.getMultipleBias()));
        super.buildInterventionResult(paramDo, config, contextDo,TemplateMultipleRange.builder().start(start).end(end).build(),null,null);
    }

    @Data
    public static class NewUserHelpInterventionConfigDo implements InterventionConfig {
        /**
         * 最大累计游戏局数
         * {@link DefaultInterventionConfigDo#getMaxTotalGameCount()}
         */
        private Integer maxTotalGameCount;
        /**
         * 最小今日局数
         * {@link DefaultInterventionConfigDo#getMinTodayGameCount()}
         */
        private Integer minTodayGameCount;
        /**
         * 投注额系数
         * {@link DefaultInterventionConfigDo#getBetAmountCoefficient()}
         */
        private BigDecimal betAmountCoefficient;
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
         * 干预模版倍率系数
         * {@link DefaultInterventionConfigDo#getInterventionMultipleCoefficient()}
         */
        private BigDecimal interventionMultipleCoefficient;
        /**
         * 兜底干预模版name
         * eg:96rtp
         * {@link DefaultInterventionConfigDo#getBackstopRtpName()}
         */
        private String backstopRtpName;
        /**
         * 干预模版倍率偏差
         * {@link DefaultInterventionConfigDo#getMultipleBias()}
         */
        private BigDecimal multipleBias;

    }
}
