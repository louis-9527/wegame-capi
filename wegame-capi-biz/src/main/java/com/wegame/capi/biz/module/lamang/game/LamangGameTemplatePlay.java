package com.wegame.capi.biz.module.lamang.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.dgit omain.LamangConfig;
import com.wegame.capi.biz.config.domain.common.CommonTemplateConfig;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.DiwataMayariGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.template.malakas.DiwataBetRecordTemplateEsDo;
import com.wegame.capi.biz.module.base.game.AbstractGameTemplatePlay;
import com.wegame.capi.biz.module.base.game.TemplateGame;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.wegame.capi.common.Constants.LAMANG_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;
import static com.wegame.capi.common.utils.ListUtil.getRandomElement;

@Slf4j
@Component("lamangGameTemplatePlay")
@ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
public class LamangGameTemplatePlay extends AbstractGameTemplatePlay<DiwataMayariGameRecordDo, DiwataBetRecordTemplateEsDo> {
    @Autowired
    LamangConfig lamangConfig;
    @Autowired
    LamangGameCommonTemplatePlay lamangGameCommonTemplatePlay;

    @Override
    public TemplateGame templateGame() {
        return lamangGameCommonTemplatePlay;
    }

    @Override
    public DiwataBetRecordTemplateEsDo getTemplate(BetDo betDo, GamePlayDo gamePlayDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        if (!Objects.equals(betDo.getBetPromotionType(), BetPromotionTypeEnum.NONE)) {
            CommonTemplateConfig.Rtp rtp = getDefaultRtp(betDo, lamangConfig.getCommonTemplateConfig());
            AssertUtil.assertNotNull(rtp, ErrorCode.ERR_INTER);
            return fetchTemplate(betDo, gamePlayDo, rtp);
        } else {
            // todo @jayni 根据个人干预 获取 rtp
            CommonTemplateConfig.Rtp rtp = getDefaultRtp(betDo, lamangConfig.getCommonTemplateConfig());
            AssertUtil.assertNotNull(rtp, ErrorCode.ERR_INTER);
            return fetchTemplate(betDo, gamePlayDo, rtp);
        }
    }

    private DiwataBetRecordTemplateEsDo fetchTemplate(BetDo betDo, GamePlayDo gamePlayDo, CommonTemplateConfig.Rtp rtp) {
        String templateIndexName = rtp.getIndexName();
        AssertUtil.assertStrNotEmpty(templateIndexName, ErrorCode.ERR_INTER);
        TemplateMultipleRange multipleRange = getTemplateMultipleRange(rtp.getMultipleRangeWeightMap().get(betDo.getBetType()));
        AssertUtil.assertNotNull(multipleRange, ErrorCode.ERR_INTER);
        gamePlayDo.setTemplateMultipleRange(multipleRange);
        List<Long> betRecordIdList = betRecordTemplateService.listIdByMultipleRange(templateIndexName, gamePlayDo.getBetType(), multipleRange.getStart(), multipleRange.getEnd());
        AssertUtil.assertNotEmpty(betRecordIdList, ErrorCode.ERR_INTER);
        Long betRecordId = getRandomElement(betRecordIdList);
        log.info("fetch template, templateIndexName: {}, betRecordId:{}", templateIndexName, betRecordId);
        DiwataBetRecordTemplateEsDo templateEsDo;
        if (lamangConfig.getCommonTemplateConfig().supportWarmUp(gamePlayDo.getBetType(), multipleRange)) {
            templateEsDo = betRecordTemplateService.getTemplateWithLocalCache(templateIndexName, betRecordId, DiwataBetRecordTemplateEsDo.class);
        } else {
            templateEsDo = betRecordTemplateService.findById(templateIndexName, betRecordId, DiwataBetRecordTemplateEsDo.class);
        }
        return templateEsDo;
    }
}