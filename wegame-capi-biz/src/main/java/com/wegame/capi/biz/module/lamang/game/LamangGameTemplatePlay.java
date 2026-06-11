package com.wegame.capi.biz.module.lamang.game;

import cn.hutool.core.util.StrUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.config.domain.common.CommonTemplateConfig;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.LamangGameRecordDo;
import com.wegame.capi.biz.domain.biz.template.lamang.LamangBetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionContextDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionParamDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionResultDo;
import com.wegame.capi.biz.module.base.game.AbstractGameTemplatePlay;
import com.wegame.capi.biz.module.base.game.TemplateGame;
import com.wegame.capi.biz.service.metric.UserMetricService;
import com.wegame.capi.biz.service.intervention.PlayerInterventionService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.constants.InterventionRuleIdConstants;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

import static com.wegame.capi.common.Constants.LAMANG_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Slf4j
@Component("lamangGameTemplatePlay")
@ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
public class LamangGameTemplatePlay extends AbstractGameTemplatePlay<LamangGameRecordDo, LamangBetRecordTemplateEsDo> {
    @Autowired
    LamangConfig lamangConfig;
    @Autowired
    LamangGameCommonTemplatePlay lamangGameCommonTemplatePlay;
    @Autowired
    PlayerInterventionService playerInterventionService;
    @Autowired
    UserMetricService userMetricService;

    @Override
    public TemplateGame templateGame() {
        return lamangGameCommonTemplatePlay;
    }

    @Override
    public LamangBetRecordTemplateEsDo getTemplate(BetDo betDo, GamePlayDo gamePlayDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        if (!Objects.equals(betDo.getBetPromotionType(), BetPromotionTypeEnum.NONE) || !lamangConfig.isInterventionSwitch()) {
            CommonTemplateConfig.Rtp rtp = getDefaultRtp(betDo, lamangConfig.getCommonTemplateConfig());
            AssertUtil.assertNotNull(rtp, ErrorCode.ERR_INTER);
            return fetchTemplate(betDo, gamePlayDo, lamangConfig.getCommonTemplateConfig(), rtp, LamangBetRecordTemplateEsDo.class);
        } else {
            LamangBetRecordTemplateEsDo templateEsDo = this.intervention(betDo, gamePlayDo);
            if (Objects.isNull(templateEsDo)) {
                // 兜底默认，没有干预
                CommonTemplateConfig.Rtp rtp = getDefaultRtp(betDo, lamangConfig.getCommonTemplateConfig());
                AssertUtil.assertNotNull(rtp, ErrorCode.ERR_INTER);
                return fetchTemplate(betDo, gamePlayDo, lamangConfig.getCommonTemplateConfig(), rtp, LamangBetRecordTemplateEsDo.class);
            }
            return templateEsDo;
        }
    }

    private LamangBetRecordTemplateEsDo intervention(BetDo betDo, GamePlayDo gamePlayDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        UserMetricInfoDo userMetricInfo = userMetricService.getUserMetricInfo(userInfo);
        PlayerInterventionParamDo playerInterventionParamDo = PlayerInterventionParamDo.buildDo(userMetricInfo, null, betDo);
        this.paramExtOperatorBuilder(playerInterventionParamDo, betDo, gamePlayDo);
        // 前置
        PlayerInterventionContextDo contextDo = new PlayerInterventionContextDo();
        PlayerInterventionResultDo preInterventionResult = playerInterventionService.preIntervention(betDo.getGameType(), playerInterventionParamDo, contextDo);

        LamangBetRecordTemplateEsDo noInterventionTemplateEsDo = null;
        if (Objects.isNull(preInterventionResult)) {
            // 前置没有结果-从默认获取
            CommonTemplateConfig.Rtp rtp = getDefaultRtp(betDo, lamangConfig.getCommonTemplateConfig());
            AssertUtil.assertNotNull(rtp, ErrorCode.ERR_INTER);
            noInterventionTemplateEsDo = fetchTemplate(betDo, gamePlayDo, lamangConfig.getCommonTemplateConfig(), rtp, LamangBetRecordTemplateEsDo.class);
            AssertUtil.assertNotNull(noInterventionTemplateEsDo, ErrorCode.ERR_INTER);
            playerInterventionParamDo.setBetRecordTemplateEsDo(noInterventionTemplateEsDo);
        } else {
            // 前置有结果
            LamangBetRecordTemplateEsDo preTemplateEsDo = this.fetchTemplateFromInterventionResult(preInterventionResult, betDo, gamePlayDo);
            preInterventionResult.setBetRecordTemplateEsDo(preTemplateEsDo);
            playerInterventionParamDo.setBetRecordTemplateEsDo(preTemplateEsDo);
        }
        // 走后置
        PlayerInterventionResultDo postInterventionResult = playerInterventionService.postIntervention(betDo.getGameType(), playerInterventionParamDo, contextDo);
        LamangBetRecordTemplateEsDo postTemplateEsDo = this.fetchTemplateFromInterventionResult(postInterventionResult, betDo, gamePlayDo);
        if (Objects.nonNull(postTemplateEsDo)) {
            betDo.setInterventionRuleId(postInterventionResult.getCmpId());
            return postTemplateEsDo;
        } else {
            return noInterventionTemplateEsDo;
        }
    }

    private void paramExtOperatorBuilder(PlayerInterventionParamDo param, BetDo betDo, GamePlayDo gamePlayDo) {
        Function function = (Function<PlayerInterventionResultDo, LamangBetRecordTemplateEsDo>) resultDo -> fetchTemplateFromInterventionResult(resultDo, betDo, gamePlayDo);
        param.addExtOperator(InterventionRuleIdConstants.FG_LIMIT_INTERVENTION, function);
    }


    private LamangBetRecordTemplateEsDo fetchTemplateFromInterventionResult(PlayerInterventionResultDo interventionResult, BetDo betDo, GamePlayDo gamePlayDo) {
        if (Objects.isNull(interventionResult)) {
            return null;
        }
        if (Objects.nonNull(interventionResult.getBetRecordTemplateEsDo())) {
            return (LamangBetRecordTemplateEsDo) interventionResult.getBetRecordTemplateEsDo();
        }
        LamangBetRecordTemplateEsDo templateEsDo = null;
        if (StrUtil.isNotEmpty(interventionResult.getRtpName())) {
            CommonTemplateConfig.Rtp rtpByRtpName = lamangConfig.getCommonTemplateConfig().getRtpByRtpName(interventionResult.getRtpName());
            AssertUtil.assertNotNull(rtpByRtpName, ErrorCode.ERR_INTER);
            if (Objects.nonNull(interventionResult.getTemplateMultipleRange())) {
                templateEsDo = super.fetchTemplateInRandom(gamePlayDo, rtpByRtpName.getIndexName(), interventionResult.getTemplateMultipleRange(), LamangBetRecordTemplateEsDo.class);
                if (Objects.nonNull(templateEsDo)) {
                    return templateEsDo;
                }
            } else {
                return super.fetchTemplate(betDo, gamePlayDo, lamangConfig.getCommonTemplateConfig(), rtpByRtpName, LamangBetRecordTemplateEsDo.class);
            }
        } else {
            // 使用默认rtp
            if (Objects.nonNull(interventionResult.getTemplateMultipleRange())) {
                CommonTemplateConfig.Rtp defaultRtp = getDefaultRtp(betDo, lamangConfig.getCommonTemplateConfig());
                templateEsDo = super.fetchTemplateInRandom(gamePlayDo, defaultRtp.getIndexName(), interventionResult.getTemplateMultipleRange(), LamangBetRecordTemplateEsDo.class);
                if (Objects.nonNull(templateEsDo)) {
                    return templateEsDo;
                }
            }
        }
        if (StrUtil.isNotEmpty(interventionResult.getBackstopRtpName())) {
            CommonTemplateConfig.Rtp rtpByRtpName = lamangConfig.getCommonTemplateConfig().getRtpByRtpName(interventionResult.getBackstopRtpName());
            AssertUtil.assertNotNull(rtpByRtpName, ErrorCode.ERR_INTER);
            return super.fetchTemplate(betDo, gamePlayDo, lamangConfig.getCommonTemplateConfig(), rtpByRtpName, LamangBetRecordTemplateEsDo.class);
        }
        return templateEsDo;
    }

}