package com.wegame.capi.biz.module.kapre.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.KapreConfig;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.KapreGameRecordDo;
import com.wegame.capi.biz.domain.biz.template.kapre.KapreBetRecordTemplateEsDo;
import com.wegame.capi.biz.module.base.game.AbstractGameTemplatePlay;
import com.wegame.capi.biz.module.base.game.TemplateGame;
import com.wegame.capi.biz.service.template.BetRecordTemplateEsService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import com.wegame.capi.common.utils.TemplateCacheWarmUpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.wegame.capi.common.Constants.*;
import static com.wegame.capi.common.utils.ListUtil.getRandomElement;

@Slf4j
@Component("kapreGameTemplatePlay")
@ConditionalOnProperty(name = KAPRE_PROP, havingValue = TRUE_VAL)
public class KapreGameTemplatePlay extends AbstractGameTemplatePlay<KapreGameRecordDo, KapreBetRecordTemplateEsDo> {

    @Autowired
    KapreConfig kapreConfig;
    @Autowired
    BetRecordTemplateEsService<KapreBetRecordTemplateEsDo> betRecordTemplateEsService;
    @Autowired
    KapreGameCommonTemplatePlay kapreGameCommonTemplatePlay;

    @Override
    public TemplateGame templateGame() {
        return kapreGameCommonTemplatePlay;
    }

    @Override
    public KapreBetRecordTemplateEsDo getTemplate(BetDo betDo, GamePlayDo gamePlayDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        TemplateMultipleRange templateMultipleRange = getTemplateMultipleRange(gamePlayDo.getBetType());
        AssertUtil.assertNotNull(templateMultipleRange, ErrorCode.ERR_INTER);
        gamePlayDo.setTemplateMultipleRange(templateMultipleRange);
        List<Long> betRecordIdList = betRecordTemplateEsService.listIdByMultipleRange(TemplateTypeEnum.KAPRE, gamePlayDo.getBetType(), templateMultipleRange.getStart(), templateMultipleRange.getEnd(), KapreBetRecordTemplateEsDo.class);
        AssertUtil.assertNotEmpty(betRecordIdList, ErrorCode.ERR_INTER);
        Long betRecordId = getRandomElement(betRecordIdList);
        log.info("template betRecordId:{}", betRecordId);
        if (TemplateCacheWarmUpUtil.isWarmUpMultipleRange(GameTypeEnum.KAPRE, gamePlayDo.getBetType(), templateMultipleRange)) {
            return betRecordTemplateEsService.getTemplateWithLocalCache(TemplateTypeEnum.KAPRE, betRecordId, KapreBetRecordTemplateEsDo.class);
        } else {
            return betRecordTemplateEsService.findById(TemplateTypeEnum.KAPRE, betRecordId, KapreBetRecordTemplateEsDo.class);
        }
    }

    private TemplateMultipleRange getTemplateMultipleRange(BetTypeEnum betTypeEnum) {
        String templateMultipleRange = randomService.randomKeyByWeight(Objects.equals(betTypeEnum, BetTypeEnum.NORMAL_BET)
                ? kapreConfig.getTemplateRangeConfig().getMainGame()
                : kapreConfig.getTemplateRangeConfig().getFreeGame());
        AssertUtil.assertStrNotEmpty(templateMultipleRange, ErrorCode.ERR_INTER);
        return TemplateMultipleRange.build(templateMultipleRange);
    }
}