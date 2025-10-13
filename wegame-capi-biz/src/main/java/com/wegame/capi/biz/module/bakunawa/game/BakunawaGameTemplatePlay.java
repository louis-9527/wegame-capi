package com.wegame.capi.biz.module.bakunawa.game;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.config.domain.BakunawaConfig;
import com.wegame.capi.biz.domain.biz.game.BakunawaGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.template.bakunawa.BakunawaBetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.bakunawa.reward.BakunawaRewardHandler;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.service.template.es.BetRecordTemplateEsService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.MultiplePieTypeEnum;
import com.wegame.capi.common.utils.JsonGzipUtil;
import com.wegame.capi.common.utils.TemplateCacheWarmUpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.wegame.capi.biz.builder.RewardHandlerBuilder.build;
import static com.wegame.capi.biz.module.bakunawa.BakunawaGameHelper.BAKUNAWA_GAME_INFO;
import static com.wegame.capi.common.enums.TemplateTypeEnum.BAKUNAWA;

@Slf4j
@Component("bakunawaGameTemplatePlay")
public class BakunawaGameTemplatePlay extends AbstractGame<BakunawaGameRecordDo> {

    private static final GameInfoDo GAME_INFO = BAKUNAWA_GAME_INFO;
    @Autowired
    BakunawaRewardHandler bakunawaRewardHandler;
    @Autowired
    BetRecordTemplateEsService<BakunawaBetRecordTemplateEsDo> betRecordTemplateEsService;
    @Autowired
    BakunawaConfig bakunawaConfig;

    @Override
    public List<BakunawaGameRecordDo> play(GamePlayDo gamePlay) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        TemplateMultipleRange templateMultipleRange = getTemplateMultipleRange(gamePlay.getBetType());
        AssertUtil.assertNotNull(templateMultipleRange, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateMultipleRange.getStart(), ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateMultipleRange.getEnd(), ErrorCode.ERR_INTER);
        List<Long> betRecordIdList = betRecordTemplateEsService.listIdByMultipleRange(BAKUNAWA, gamePlay.getBetType(), new BigDecimal("1"), new BigDecimal("1"), BakunawaBetRecordTemplateEsDo.class);
        AssertUtil.assertNotEmpty(betRecordIdList, ErrorCode.ERR_INTER);
        Long betRecordId = getRandomElement(betRecordIdList);
        log.info("betRecordId:{}", betRecordId);
        List<GameRecordTemplateDo> configRecordList;
        BakunawaBetRecordTemplateEsDo template;
        if (TemplateCacheWarmUpUtil.isWarmUpMultipleRange(GameTypeEnum.BAKUNAWA, gamePlay.getBetType(), templateMultipleRange)) {
            // 已经被预热的倍率，直接走缓存
            if (Objects.equals(gamePlay.getBetType(), BetTypeEnum.NORMAL_BET)) {
                template = betRecordTemplateEsService.getTemplateWithLocalCache(BAKUNAWA, betRecordId, BakunawaBetRecordTemplateEsDo.class);
            } else {
                throw new BusinessException(ErrorCode.ERR_PARAM);
            }
        } else {
            // 没有被预热的倍率，需要重新查询
            template = betRecordTemplateEsService.findById(BAKUNAWA, betRecordId, BakunawaBetRecordTemplateEsDo.class);
        }
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        configRecordList = FastJsonUtils.fromJSONString(JsonGzipUtil.decompress(template.getBytes()), new TypeReference<List<GameRecordTemplateDo>>() {
        });
        if (CollectionUtils.isEmpty(configRecordList)) {
            log.error("template is empty, betType:{}, range start: [], end: {}", gamePlay.getBetType(), templateMultipleRange.getStart(), templateMultipleRange.getEnd());
        }
        AssertUtil.assertNotEmpty(configRecordList, ErrorCode.ERR_INTER, "configRecordList is empty");
        GameRecordTemplateDo configRecordDo = configRecordList.get(0);
        AssertUtil.assertNotNull(configRecordDo.getExtInfo(), ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(JSONUtil.isTypeJSON(configRecordDo.getExtInfo().toString()), ErrorCode.ERR_PARAM);
        WheelDo<SymbolDo> wheel = convertWheel(configRecordDo.getWheel());
        RewardHandleDo<SymbolDo> rewardHandleDo = build(GAME_INFO, gamePlay, wheel, BigDecimal.ZERO);
        JSONObject multipleJsonObject = JSON.parseObject(JSON.toJSONString(configRecordDo.getExtInfo()));
        MultiplePieTypeEnum multiplePieType = MultiplePieTypeEnum.valueOf(multipleJsonObject.getString("multiplePieType"));
        AssertUtil.assertNotNull(multiplePieType, ErrorCode.ERR_PARAM);
        rewardHandleDo.setMultiplePieType(multiplePieType);
        rewardHandleDo.setCurrentMultiple(new BigDecimal(multipleJsonObject.getString("multiplePie")));
        RewardResultDo<SymbolDo> rewardResult = bakunawaRewardHandler.handle(rewardHandleDo);
        long overallRewardAmount = rewardResult.getTotalRewardAmount();
        return Lists.newArrayList(this.buildGameRecord(userInfo, generateGameRecordId(), gamePlay.getBetRecordId(), wheel, rewardResult, overallRewardAmount));
    }

    private BakunawaGameRecordDo buildGameRecord(UserInfo userInfo,
                                                 Long gameRecordId,
                                                 Long betRecordId,
                                                 WheelDo<SymbolDo> wheel,
                                                 RewardResultDo<SymbolDo> rewardResult,
                                                 Long TotalRewardAmount) {
        return BakunawaGameRecordDo.builder()
                .id(gameRecordId)
                .betRecordId(betRecordId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .overallRewardAmount(TotalRewardAmount)
                .build();
    }

    private WheelDo<SymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<SymbolDo>>() {
        });
    }

    private TemplateMultipleRange getTemplateMultipleRange(BetTypeEnum betTypeEnum) {
        String templateMultipleRange;
        if (Objects.equals(betTypeEnum, BetTypeEnum.NORMAL_BET)) {
            templateMultipleRange = randomService.randomKeyByWeight(bakunawaConfig.getTemplateRangeConfig().getMainGame());
        } else {
            templateMultipleRange = randomService.randomKeyByWeight(bakunawaConfig.getTemplateRangeConfig().getFreeGame());
        }
        AssertUtil.assertStrNotEmpty(templateMultipleRange, ErrorCode.ERR_INTER);
        return TemplateMultipleRange.build(templateMultipleRange);
    }

    private Long getRandomElement(List<Long> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }
}
