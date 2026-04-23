package com.wegame.capi.biz.module.malakas.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.domain.biz.template.malakas.MalakasBetRecordTemplateEsDo;
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

import static com.wegame.capi.common.Constants.MALAKAS_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;
import static com.wegame.capi.common.utils.ListUtil.getRandomElement;

@Slf4j
@Component("malakasGameTemplatePlay")
@ConditionalOnProperty(name = MALAKAS_PROP, havingValue = TRUE_VAL)
public class MalakasGameTemplatePlay extends AbstractGameTemplatePlay<MalakasGameRecordDo, MalakasBetRecordTemplateEsDo> {

    @Autowired
    MalakasConfig malakasConfig;
    @Autowired
    BetRecordTemplateEsService<MalakasBetRecordTemplateEsDo> betRecordTemplateEsService;
    @Autowired
    MalakasGameCommonTemplatePlay malakasGameCommonTemplatePlay;

    @Override
    public TemplateGame templateGame() {
        return malakasGameCommonTemplatePlay;
    }

    @Override
    public MalakasBetRecordTemplateEsDo getTemplate(BetDo betDo, GamePlayDo gamePlayDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        TemplateMultipleRange templateMultipleRange = getTemplateMultipleRange(gamePlayDo.getBetType());
        AssertUtil.assertNotNull(templateMultipleRange, ErrorCode.ERR_INTER);
        gamePlayDo.setTemplateMultipleRange(templateMultipleRange);
        List<Long> betRecordIdList = betRecordTemplateEsService.listIdByMultipleRange(TemplateTypeEnum.MALAKAS, gamePlayDo.getBetType(), templateMultipleRange.getStart(), templateMultipleRange.getEnd(), MalakasBetRecordTemplateEsDo.class);
        AssertUtil.assertNotEmpty(betRecordIdList, ErrorCode.ERR_INTER);
        Long betRecordId = getRandomElement(betRecordIdList);
        log.info("template betRecordId:{}", betRecordId);
        if (TemplateCacheWarmUpUtil.isWarmUpMultipleRange(GameTypeEnum.MALAKAS, gamePlayDo.getBetType(), templateMultipleRange)) {
            return betRecordTemplateEsService.getTemplateWithLocalCache(TemplateTypeEnum.MALAKAS, betRecordId, MalakasBetRecordTemplateEsDo.class);
        } else {
            return betRecordTemplateEsService.findById(TemplateTypeEnum.MALAKAS, betRecordId, MalakasBetRecordTemplateEsDo.class);
        }
    }

    private TemplateMultipleRange getTemplateMultipleRange(BetTypeEnum betTypeEnum) {
        String templateMultipleRange = randomService.randomKeyByWeight(Objects.equals(betTypeEnum, BetTypeEnum.NORMAL_BET)
                ? malakasConfig.getTemplateRangeConfig().getMainGame()
                : malakasConfig.getTemplateRangeConfig().getFreeGame());
        AssertUtil.assertStrNotEmpty(templateMultipleRange, ErrorCode.ERR_INTER);
        return TemplateMultipleRange.build(templateMultipleRange);
    }
}