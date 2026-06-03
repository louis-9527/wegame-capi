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
import com.wegame.capi.biz.service.UserMetricService;
import com.wegame.capi.biz.service.intervention.PlayerInterventionService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Objects;

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
        if (!Objects.equals(betDo.getBetPromotionType(), BetPromotionTypeEnum.NONE)) {
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
        PlayerInterventionParamDo.buildDo(userMetricInfo, null, betDo.getBetType(), betDo.getBetAmount());
        // 前置
        PlayerInterventionContextDo contextDo = new PlayerInterventionContextDo();
        PlayerInterventionResultDo preInterventionResult = playerInterventionService.preIntervention(betDo.getGameType(), PlayerInterventionParamDo.buildDo(userMetricInfo, null, betDo.getBetType(), betDo.getBetAmount()), contextDo);
        LamangBetRecordTemplateEsDo preTemplateEsDo = this.fetchTemplate(preInterventionResult, betDo, gamePlayDo);
        // 后置
        PlayerInterventionResultDo postInterventionResult = playerInterventionService.postIntervention(betDo.getGameType(), PlayerInterventionParamDo.buildDo(userMetricInfo, preTemplateEsDo, betDo.getBetType(), betDo.getBetAmount()), contextDo);
        LamangBetRecordTemplateEsDo templateEsDo = this.fetchTemplate(postInterventionResult, betDo, gamePlayDo);
        if (Objects.nonNull(templateEsDo)) {
            betDo.setInterventionRuleId(preInterventionResult.getComponentId());
        }
        return templateEsDo;
    }

    private LamangBetRecordTemplateEsDo fetchTemplate(PlayerInterventionResultDo interventionResult, BetDo betDo, GamePlayDo gamePlayDo) {
        if (Objects.isNull(interventionResult)) {
            return null;
        }
        if (Objects.nonNull(interventionResult.getBetRecordTemplateEsDo())) {
            return (LamangBetRecordTemplateEsDo) interventionResult.getBetRecordTemplateEsDo();
        }
        LamangBetRecordTemplateEsDo templateEsDo = null;
        if (StrUtil.isNotEmpty(interventionResult.getRtpName())) {
            CommonTemplateConfig.Rtp rtpByRtpName = lamangConfig.getCommonTemplateConfig().getRtpByRtpName(interventionResult.getRtpName());
            AssertUtil.assertNotNull(rtpByRtpName, ErrorCode.ERR_INTER, "RtpName is null: " + interventionResult.getRtpName());
            if (Objects.nonNull(interventionResult.getTemplateMultipleRange())) {
                templateEsDo = super.fetchTemplateInRandom(gamePlayDo, rtpByRtpName.getIndexName(), interventionResult.getTemplateMultipleRange(), LamangBetRecordTemplateEsDo.class);
            } else {
                return fetchTemplate(betDo, gamePlayDo, lamangConfig.getCommonTemplateConfig(), rtpByRtpName, LamangBetRecordTemplateEsDo.class);
            }
        }

        if (Objects.isNull(templateEsDo) && StrUtil.isNotEmpty(interventionResult.getBackstopRtpName())) {
            CommonTemplateConfig.Rtp rtpByRtpName = lamangConfig.getCommonTemplateConfig().getRtpByRtpName(interventionResult.getRtpName());
            AssertUtil.assertNotNull(rtpByRtpName, ErrorCode.ERR_INTER, "RtpName is null: " + interventionResult.getRtpName());
            return fetchTemplate(betDo, gamePlayDo, lamangConfig.getCommonTemplateConfig(), rtpByRtpName, LamangBetRecordTemplateEsDo.class);
        }
        return templateEsDo;
    }

}