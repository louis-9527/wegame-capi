package com.wegame.capi.biz.module.ibongadarna.game;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.IbongAdarnaGameRecordDo;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.IbongAdarnaSymbolDo;
import com.wegame.capi.biz.domain.biz.template.ibong.IbongAdarnaBetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.innerdos.decider.InnerDosDecideResult;
import com.wegame.capi.biz.innerdos.decider.InnerDosDeciderManager;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.ibongadarna.IbongAdarnaGameHelper;
import com.wegame.capi.biz.module.ibongadarna.reward.IbongAdarnaRewardHandler;
import com.wegame.capi.biz.service.template.BetRecordTemplateEsService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import com.wegame.capi.common.utils.JsonGzipUtil;
import com.wegame.capi.common.utils.TemplateCacheWarmUpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static com.wegame.capi.biz.builder.RewardHandlerBuilder.build;
import static com.wegame.capi.common.Constants.IBONG_ADARNA_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;
import static com.wegame.capi.common.enums.TemplateTypeEnum.IBONG_ADARNA;

@Slf4j
@Component("ibongAdarnaGameTemplatePlay")
@ConditionalOnProperty(name = IBONG_ADARNA_PROP, havingValue = TRUE_VAL)
public class IbongAdarnaGameTemplatePlay extends AbstractGame<IbongAdarnaGameRecordDo> {

    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo GAME_INFO = IbongAdarnaGameHelper.IBONG_ADARNA_GAME_INFO;

    @Autowired
    IbongAdarnaRewardHandler ibongAdarnaRewardHandler;
    @Autowired
    IbongAdarnaConfig ibongAdarnaConfig;
    @Autowired
    BetRecordTemplateEsService<IbongAdarnaBetRecordTemplateEsDo> betRecordTemplateEsService;
    @Autowired
    private InnerDosDeciderManager innerDosDeciderManager;

    @Override
    public List<IbongAdarnaGameRecordDo> play(GamePlayDo gamePlay) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        // 内部干预决策逻辑
        TemplateMultipleRange templateMultipleRange = this.getTemplateMultipleRange(gamePlay);

        List<Long> betRecordIdList = betRecordTemplateEsService.listIdByMultipleRange(IBONG_ADARNA, gamePlay.getBetType(), gamePlay.getBetSubType(), templateMultipleRange.getStart(), templateMultipleRange.getEnd(), IbongAdarnaBetRecordTemplateEsDo.class);
        AssertUtil.assertNotEmpty(betRecordIdList, ErrorCode.ERR_INTER);
        Long betRecordId = this.getRandomElement(betRecordIdList);
        log.info("betRecordId:{}", betRecordId);
        List<GameRecordTemplateDo> configRecordList;
        IbongAdarnaBetRecordTemplateEsDo template;
        if (TemplateCacheWarmUpUtil.isWarmUpMultipleRange(GameTypeEnum.IBONG_ADARNA, gamePlay.getBetType(), gamePlay.getBetSubType(), templateMultipleRange)) {
            // 已经被预热的倍率，直接走缓存.ibong只存在本地缓存
            template = betRecordTemplateEsService.getTemplateWithLocalCache(TemplateTypeEnum.IBONG_ADARNA, betRecordId, IbongAdarnaBetRecordTemplateEsDo.class);
        } else {
            // 没有被预热的倍率，需要重新查询
            template = betRecordTemplateEsService.findById(TemplateTypeEnum.IBONG_ADARNA, betRecordId, IbongAdarnaBetRecordTemplateEsDo.class);
        }
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        configRecordList = FastJsonUtils.fromJSONString(JsonGzipUtil.decompress(template.getBytes()), new TypeReference<List<GameRecordTemplateDo>>() {
        });
        if (CollectionUtils.isEmpty(configRecordList)) {
            log.error("template is empty, betType:{}, range start: {}, end: {}", gamePlay.getBetType(), templateMultipleRange.getStart(), templateMultipleRange.getEnd());
        }

        AssertUtil.assertNotEmpty(configRecordList, ErrorCode.ERR_INTER, "configRecordList is empty");
        GameRecordTemplateDo configRecordDo = configRecordList.get(0);
        AssertUtil.assertNotNull(configRecordDo.getExtInfo(), ErrorCode.ERR_INTER, "extInfo is null");
        AssertUtil.assertTrue(NumberUtil.isNumber(configRecordDo.getExtInfo().toString()), ErrorCode.ERR_INTER, "extInfo is not number");
        WheelDo<IbongAdarnaSymbolDo> wheel = convertWheel(configRecordDo.getWheel());
        RewardResultDo<IbongAdarnaSymbolDo> firstWheelRewardResult = ibongAdarnaRewardHandler.handle(build(GAME_INFO, gamePlay, wheel, new BigDecimal(configRecordDo.getExtInfo().toString())));
        long overallRewardAmount = firstWheelRewardResult.getTotalRewardAmount();
        List<IbongAdarnaGameRecordDo> ret = Lists.newArrayList(this.buildGameRecord(userInfo, generateGameRecordId(), gamePlay.getBetRecordId(), wheel, firstWheelRewardResult, overallRewardAmount));
        // 结果验证数据添加
        gamePlay.setTemplateId(betRecordId);
        gamePlay.setOverallRewardAmount(overallRewardAmount);
        gamePlay.setTemplateMultipleRange(templateMultipleRange);
        return ret;
    }

    private IbongAdarnaGameRecordDo buildGameRecord(UserInfo userInfo,
                                                    Long gameRecordId,
                                                    Long betRecordId,
                                                    WheelDo<IbongAdarnaSymbolDo> wheel,
                                                    RewardResultDo<IbongAdarnaSymbolDo> rewardResult,
                                                    Long TotalRewardAmount) {
        return IbongAdarnaGameRecordDo.builder()
                .id(gameRecordId)
                .betRecordId(betRecordId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .overallRewardAmount(TotalRewardAmount)
                .build();
    }

    private TemplateMultipleRange getTemplateMultipleRange(GamePlayDo gamePlay) {
        // inner dos 决策
        InnerDosDecideResult decideResult = innerDosDeciderManager.decide(gamePlay.getGameType(), gamePlay);
        AssertUtil.assertNotNull(decideResult, ErrorCode.ERR_INTER, "inner dos decide result is null");
        String templateIdRange = decideResult.getTemplateIdRange();
        AssertUtil.assertStrNotEmpty(templateIdRange, ErrorCode.ERR_INTER);
        return TemplateMultipleRange.build(templateIdRange);
    }

    private Long getRandomElement(List<Long> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

    private WheelDo<IbongAdarnaSymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<IbongAdarnaSymbolDo>>() {
        });
    }
}