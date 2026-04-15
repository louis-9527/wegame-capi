package com.wegame.capi.biz.module.malakas.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.config.domain.common.CommonInventoryControlConfig;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.domain.biz.template.malakas.MalakasBetRecordTemplateEsDo;
import com.wegame.capi.biz.module.base.game.AbstractGameInventoryTemplatePlay;
import com.wegame.capi.biz.module.base.game.TemplateGame;
import com.wegame.capi.biz.service.template.BetRecordTemplateEsService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import com.wegame.capi.common.utils.TemplateCacheWarmUpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.wegame.capi.biz.module.base.AmountHelper.maxAmountControl;
import static com.wegame.capi.common.Constants.MALAKAS_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;
import static com.wegame.capi.common.utils.ListUtil.getRandomElement;

@Slf4j
@Component("malakasGameInventoryTemplatePlay")
@ConditionalOnProperty(name = MALAKAS_PROP, havingValue = TRUE_VAL)
public class MalakasGameInventoryTemplatePlay extends AbstractGameInventoryTemplatePlay<MalakasGameRecordDo, MalakasBetRecordTemplateEsDo> {

    @Autowired
    MalakasConfig malakasConfig;
    @Autowired
    MalakasGameCommonTemplatePlay malakasGameCommonTemplatePlay;
    @Autowired
    BetRecordTemplateEsService<MalakasBetRecordTemplateEsDo> betRecordTemplateEsService;

    @Override
    public CommonInventoryControlConfig inventoryControlConfig() {
        return malakasConfig.getInventoryControlConfig();
    }

    @Override
    public TemplateGame templateGame() {
        return malakasGameCommonTemplatePlay;
    }

    // todo 改造， 支持通用模版获取， 迁移到 AbstractGameTemplatePlay
    @Override
    public MalakasBetRecordTemplateEsDo getTemplateByRange(BetDo betDo, GamePlayDo gamePlayDo, TemplateMultipleRange templateMultipleRange) {
        AssertUtil.assertNotNull(templateMultipleRange, ErrorCode.ERR_INTER);
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

    @Override
    public long getTotalRewardAmount(BetDo betDo, GamePlayDo gamePlayDo, List<MalakasGameRecordDo> gameRecordList) {
        long totalRewardAmount = Optional.ofNullable(gameRecordList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        return maxAmountControl(totalRewardAmount, betDo.getBetAmount(), malakasConfig.getRewardMaxMultiple());
    }
}