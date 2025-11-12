package com.wegame.capi.biz.module.mariamakiling.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.domain.biz.template.mariamakiling.MariaMakilingBetRecordTemplateEsDo;
import com.wegame.capi.biz.service.template.es.BetRecordTemplateEsService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetTypeEnum;
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
import java.util.stream.Collectors;

import static com.wegame.capi.biz.builder.RewardHandlerBuilder.build;
import static com.wegame.capi.biz.module.mariamakiling.MariaMakilingGameHelper.MARIA_MAKILING_GAME_INFO;
import static com.wegame.capi.common.Constants.MARIA_MAKILING_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Slf4j
@Component("mariaMakilingGameTemplatePlay")
@ConditionalOnProperty(name = MARIA_MAKILING_PROP, havingValue = TRUE_VAL)
public class MariaMakilingGameTemplatePlay extends AbstractGame<MariaMakilingGameRecordDo> {

    private static final Long GAME_RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo GAME_INFO = MARIA_MAKILING_GAME_INFO;

    @Autowired
    RewardHandler<SymbolDo> mariaMakilingRewardHandler;
    @Autowired
    MariaMakilingConfig mariaMakilingConfig;
    @Autowired
    RandomService randomService;
    @Autowired
    BetRecordTemplateEsService<MariaMakilingBetRecordTemplateEsDo> betRecordTemplateEsService;

    @Override
    public List<MariaMakilingGameRecordDo> play(GamePlayDo gamePlay) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        TemplateMultipleRange templateMultipleRange = getTemplateMultipleRange(gamePlay.getBetType());
        AssertUtil.assertNotNull(templateMultipleRange, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateMultipleRange.getStart(), ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateMultipleRange.getEnd(), ErrorCode.ERR_INTER);

        List<Long> betRecordIdList = betRecordTemplateEsService.listIdByMultipleRange(TemplateTypeEnum.MARIA_MAKILING, gamePlay.getBetType(), templateMultipleRange.getStart(), templateMultipleRange.getEnd(), MariaMakilingBetRecordTemplateEsDo.class);
        AssertUtil.assertNotEmpty(betRecordIdList, ErrorCode.ERR_INTER);
        Long templateBetRecordId = getRandomElement(betRecordIdList);

        List<GameRecordTemplateDo> configRecordList;
        MariaMakilingBetRecordTemplateEsDo template;
        if (TemplateCacheWarmUpUtil.isWarmUpMultipleRange(GameTypeEnum.MARIA_MAKILING, gamePlay.getBetType(), templateMultipleRange)) {
            // 已经被预热的倍率，直接走缓存
            if (Objects.equals(gamePlay.getBetType(), BetTypeEnum.NORMAL_BET)) {
                template = betRecordTemplateEsService.getTemplateWithLocalCache(TemplateTypeEnum.MARIA_MAKILING, templateBetRecordId, MariaMakilingBetRecordTemplateEsDo.class);
            } else if (Objects.equals(gamePlay.getBetType(), BetTypeEnum.BUY_BET)) {
                template = betRecordTemplateEsService.getTemplateWithRedisCache(TemplateTypeEnum.MARIA_MAKILING, templateBetRecordId, MariaMakilingBetRecordTemplateEsDo.class);
            } else {
                throw new BusinessException(ErrorCode.ERR_PARAM);
            }
        } else {
            // 没有被预热的倍率，需要重新查询
            template = betRecordTemplateEsService.findById(TemplateTypeEnum.MARIA_MAKILING, templateBetRecordId, MariaMakilingBetRecordTemplateEsDo.class);
        }
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        configRecordList = FastJsonUtils.fromJSONString(JsonGzipUtil.decompress(template.getBytes()), new TypeReference<List<GameRecordTemplateDo>>() {
        });
        if (CollectionUtils.isEmpty(configRecordList)) {
            log.error("template is empty, betType:{}, range start: {}, end: {}", gamePlay.getBetType(), templateMultipleRange.getStart(), templateMultipleRange.getEnd());
        }
        AssertUtil.assertNotEmpty(configRecordList, ErrorCode.ERR_INTER);
        // 获取首轮(parent ==0 and index =0)
        GameRecordTemplateDo firstConfigRecord = configRecordList.stream()
                .filter(record -> record.getParentId() == 0L && record.getIndex() == 0L)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ERR_INTER));
        // 生成首轮结果
        long firstGameRecordId = generateGameRecordId();
        WheelDo<SymbolDo> wheel = convertWheel(firstConfigRecord.getWheel());
        RewardResultDo<SymbolDo> firstWheelRewardResult = mariaMakilingRewardHandler.handle(build(GAME_INFO, gamePlay, wheel, BigDecimal.ZERO));
        long overallRewardAmount = firstWheelRewardResult.getTotalRewardAmount();
        List<MariaMakilingGameRecordDo> freeWheelGameRecordList = new ArrayList<>();
        List<Long> freeWheelGameRecordIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(firstConfigRecord.getGameRecordIds())) {
            List<Long> gameRecordIds = firstConfigRecord.getGameRecordIds();
            Map<Long, GameRecordTemplateDo> configRecordMap = configRecordList.stream()
                    .collect(Collectors.toMap(GameRecordTemplateDo::getId, p -> p));
            for (Long recordId : gameRecordIds) {
                GameRecordTemplateDo configRecord = configRecordMap.get(recordId);
                WheelDo<SymbolDo> wheelRecord = convertWheel(configRecord.getWheel());
                RewardResultDo<SymbolDo> rewardResultDo = mariaMakilingRewardHandler.handle(build(GAME_INFO, gamePlay, wheelRecord, BigDecimal.ZERO));
                overallRewardAmount = overallRewardAmount + rewardResultDo.getTotalRewardAmount();
                MariaMakilingGameRecordDo gameRecord = buildGameRecord(
                        userInfo,
                        generateGameRecordId(),
                        gamePlay.getBetRecordId(),
                        wheelRecord,
                        rewardResultDo,
                        null,
                        overallRewardAmount,
                        false,
                        false);
                freeWheelGameRecordList.add(gameRecord);
                freeWheelGameRecordIds.add(gameRecord.getId());
            }
        }
        // 构建首轮
        MariaMakilingGameRecordDo firstGameRecord = this.buildGameRecord(userInfo,
                firstGameRecordId,
                gamePlay.getBetRecordId(),
                wheel,
                firstWheelRewardResult,
                freeWheelGameRecordIds,
                firstWheelRewardResult.getTotalRewardAmount(),
                false,
                firstWheelRewardResult.isHasRewardFreeWheel()
        );
        List<MariaMakilingGameRecordDo> ret = new ArrayList<>();
        ret.add(firstGameRecord);
        if (CollectionUtils.isNotEmpty(freeWheelGameRecordList)) {
            ret.addAll(freeWheelGameRecordList);
        }

        // 结果验证数据添加
        gamePlay.setTemplateId(templateBetRecordId);
        gamePlay.setOverallRewardAmount(overallRewardAmount);
        gamePlay.setTemplateMultipleRange(templateMultipleRange);
        return ret;
    }

    private MariaMakilingGameRecordDo buildGameRecord(UserInfo userInfo,
                                                      Long gameRecordId,
                                                      Long betRecordId,
                                                      WheelDo<SymbolDo> wheel,
                                                      RewardResultDo<SymbolDo> rewardResult,
                                                      List<Long> freeWheelGameRecordIds,
                                                      Long TotalRewardAmount,
                                                      Boolean accumulateReachMaxMultiple,
                                                      Boolean lastGameFlag) {
        return MariaMakilingGameRecordDo.builder()
                .id(gameRecordId)
                .parentId(GAME_RECORD_DEFAULT_PARENT_ID)
                .betRecordId(betRecordId)
                .batchId(0L)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .index(wheel.getIndex())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .gameRecordIds(freeWheelGameRecordIds)
                .overallRewardAmount(TotalRewardAmount)
                .accumulateReachMaxMultiple(accumulateReachMaxMultiple)
                .lastGameFlag(lastGameFlag)
                .build();
    }

    private WheelDo<SymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<SymbolDo>>() {
        });
    }

    private TemplateMultipleRange getTemplateMultipleRange(BetTypeEnum betTypeEnum) {
        String templateMultipleRange;
        if (Objects.equals(betTypeEnum, BetTypeEnum.NORMAL_BET)) {
            templateMultipleRange = randomService.randomKeyByWeight(mariaMakilingConfig.getTemplateRangeConfig().getMainGame());
        } else {
            templateMultipleRange = randomService.randomKeyByWeight(mariaMakilingConfig.getTemplateRangeConfig().getFreeGame());
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
