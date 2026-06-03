package com.wegame.capi.biz.module.lamang.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.config.domain.common.CommonTemplateConfig;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.LamangGameRecordDo;
import com.wegame.capi.biz.domain.biz.template.lamang.LamangBetRecordTemplateEsDo;
import com.wegame.capi.biz.module.base.game.AbstractGameTemplatePlay;
import com.wegame.capi.biz.module.base.game.TemplateGame;
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
            // todo @jayni 根据个人干预 获取 rtp
            CommonTemplateConfig.Rtp rtp = getDefaultRtp(betDo, lamangConfig.getCommonTemplateConfig());
            AssertUtil.assertNotNull(rtp, ErrorCode.ERR_INTER);
            return fetchTemplate(betDo, gamePlayDo, lamangConfig.getCommonTemplateConfig(), rtp, LamangBetRecordTemplateEsDo.class);
        }
    }
}