package com.wegame.capi.biz.module.mangkukulam.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.MangkukulamConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import com.wegame.capi.biz.domain.biz.template.mangkukulam.MangkukulamBetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.mangkukulam.MangkukulamSymbolUtil;
import com.wegame.capi.biz.module.mangkukulam.reward.MangkukulamRewardHandler;
import com.wegame.capi.biz.service.operation.OperationEventConfigRecordService;
import com.wegame.capi.biz.service.template.BetRecordTemplateEsService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.VolatilityTypeEnum;
import com.wegame.capi.common.constants.TemplateTagConstants;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.TemplateMultipleWarmUpEnum;
import com.wegame.capi.common.utils.JsonGzipUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper.MANGKUKULAM_GAME_INFO;
import static com.wegame.capi.common.enums.TemplateTypeEnum.DIWATA_MAYARI;
import static com.wegame.capi.common.enums.TemplateTypeEnum.MANGKUKULAM;

@Slf4j
@Component("mangkukulamGameEventPlay")
public class MangkukulamGameTemplatePlay extends AbstractGame<MangkukulamGameRecordDo> {

    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final int RECORD_FIRST_INDEX = 0;
    private static final GameInfoDo gameInfo = MANGKUKULAM_GAME_INFO;

    @Autowired
    MangkukulamRewardHandler mangkukulamRewardHandler;

    @Autowired
    OperationEventConfigRecordService operationEventConfigRecordService;

    @Autowired
    MangkukulamConfig mangkukulamConfig;

    @Autowired
    BetRecordTemplateEsService<MangkukulamBetRecordTemplateEsDo> betRecordTemplateEsService;

    @Override
    public List<MangkukulamGameRecordDo> play(GamePlayDo gamePlay) {
        List<MangkukulamGameRecordDo> ret = new ArrayList<>();
        List<GameRecordTemplateDo> gameMultipleRecordDos = getGameRecordTemplate(gamePlay);
        if (BetSubTypeEnum.isFirstStep(gamePlay.getBetSubType().getCode()) && Objects.nonNull(gamePlay.getVolatility())) {
            // 执行完普通轮和免费轮
            processInitialStage(gamePlay, ret);
            processFreeStage(gamePlay, ret);
            setLastGameFlag(ret);
        } else if (BetSubTypeEnum.isFirstStep(gamePlay.getBetSubType().getCode()) && Objects.isNull(gamePlay.getVolatility())) {
            // 执行首轮
            processInitialStage(gamePlay, ret);
            MangkukulamGameRecordDo lastGame = ret.get(ret.size() - 1);
            //不可能达到最大倍数
//            boolean reachMaxMultiple = AmountHelper.reachMaxMultiple(totalRewardAmount.get(), gamePlay.getBetAmount(), mangkukulamConfig.getRewardMaxMultiple());
            boolean rewardFreeWheel = (Objects.nonNull(lastGame) && lastGame.getRewardResult().isHasRewardFreeWheel());
            if (!rewardFreeWheel) {
                setLastGameFlag(ret);
            }
        } else if (BetSubTypeEnum.isSecondStep(gamePlay.getBetSubType().getCode()) && Objects.nonNull(gamePlay.getVolatility())) {
            // 执行免费轮
            GamePlayDo.ContinuePlayContext context = gamePlay.getContinuePlayContext();
            AssertUtil.assertNotNull(gamePlay.getContinuePlayContext(), ErrorCode.ERR_INTER);
            processFreeStage(gamePlay, ret, context.getParentBetRecordId());
            setLastGameFlag(ret);
        }
        MangkukulamSymbolUtil.postProcessPlaceholderSymbol(ret);
        return ret;
    }

    private void processInitialStage(GamePlayDo gamePlay, List<MangkukulamGameRecordDo> ret) {
        List<GameRecordTemplateDo> gameMultipleRecordDos = getGameRecordTemplate(gamePlay);
    }

    private void processFreeStage(GamePlayDo gamePlay, List<MangkukulamGameRecordDo> ret, Long parentBetRecordId) {
        List<GameRecordTemplateDo> gameMultipleRecordDos = getGameRecordTemplate(gamePlay);
    }

    private TemplateMultipleRange getTemplateMultipleRange(BetTypeEnum betType, BetSubTypeEnum subType, VolatilityTypeEnum volatilityType) {
        String templateMultipleRange;
        Map<String, Integer> configMap = mangkukulamConfig.getTemplateRangeConfig().getConfigByBetTypeAndVolatility(betType, subType, volatilityType);
        AssertUtil.assertNotNull(configMap, ErrorCode.ERR_INTER);
        templateMultipleRange = randomService.randomKeyByWeight(configMap);
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

    /**
     * 获取模版所用标签
     *
     * @param betType
     * @param subType
     * @return
     */
    private String getTemplateTag(BetTypeEnum betType, BetSubTypeEnum subType) {
        if (BetSubTypeEnum.isSecondStep(subType.getCode())) {
            return TemplateTagConstants.MANGKUKULAM_FREE_STAGE;
        }
        if (BetSubTypeEnum.isFirstStep(subType.getCode())) {
            if (Objects.equals(betType, BetTypeEnum.NORMAL_BET)) {
                return TemplateTagConstants.MANGKUKULAM_NORMAL_BET_INITIAL_STAGE;
            } else if (Objects.equals(betType, BetTypeEnum.BUY_BET)) {
                return TemplateTagConstants.MANGKUKULAM_BUY_BET_INITIAL_STAGE;
            }
        }
        return null;
    }

    private List<GameRecordTemplateDo> getGameRecordTemplate(BetTypeEnum betType, BetSubTypeEnum subType, VolatilityTypeEnum volatilityType) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        TemplateMultipleRange templateMultipleRange = getTemplateMultipleRange(betType, subType, volatilityType);
        AssertUtil.assertNotNull(templateMultipleRange, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateMultipleRange.getStart(), ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateMultipleRange.getEnd(), ErrorCode.ERR_INTER);
        String tag = getTemplateTag(betType, subType);
        AssertUtil.assertNotNull(tag, ErrorCode.ERR_INTER);
        List<Long> betRecordIdList = betRecordTemplateEsService.listIdByTagAndMultipleRange(MANGKUKULAM, tag, templateMultipleRange.getStart(), templateMultipleRange.getEnd(), MangkukulamBetRecordTemplateEsDo.class);
        AssertUtil.assertNotEmpty(betRecordIdList, ErrorCode.ERR_INTER);
        Long betRecordId = getRandomElement(betRecordIdList);
        log.info("betRecordId:{}", betRecordId);
        List<GameRecordTemplateDo> configRecordList;
        MangkukulamBetRecordTemplateEsDo template;
        TemplateMultipleWarmUpEnum warmUp = TemplateMultipleWarmUpEnum.valueOfCode(GameTypeEnum.MANGKUKULAM);
        if (warmUp.canWarmUp(tag, templateMultipleRange.getEnd())) {
            // 已经被预热的倍率，直接走缓存
            if (Objects.equals(betType, BetTypeEnum.NORMAL_BET)) {
                template = betRecordTemplateEsService.getTemplateWithLocalCache(DIWATA_MAYARI, betRecordId, MangkukulamBetRecordTemplateEsDo.class);
            } else if (Objects.equals(gamePlay.getBetType(), BetTypeEnum.BUY_BET)) {
                template = betRecordTemplateEsService.getTemplateWithRedisCache(DIWATA_MAYARI, betRecordId, MangkukulamBetRecordTemplateEsDo.class);
            } else {
                throw new BusinessException(ErrorCode.ERR_PARAM);
            }
        } else {
            // 没有被预热的倍率，需要重新查询
            template = betRecordTemplateEsService.findById(DIWATA_MAYARI, betRecordId, MangkukulamBetRecordTemplateEsDo.class);
        }
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        configRecordList = FastJsonUtils.fromJSONString(JsonGzipUtil.decompress(template.getBytes()), new TypeReference<List<GameRecordTemplateDo>>() {
        });
        if (CollectionUtils.isEmpty(configRecordList)) {
            log.error("template is empty, betType:{}, range start: {}, end: {}", gamePlay.getBetType(), templateMultipleRange.getStart(), templateMultipleRange.getEnd());
        }
        AssertUtil.assertNotEmpty(configRecordList, ErrorCode.ERR_INTER);
        // 结果验证数据添加
        gamePlay.setTemplateId(betRecordId);
        gamePlay.setTemplateMultipleRange(templateMultipleRange);
        return configRecordList;
    }


    /**
     * 递归处理免费轮，确保同层级先处理完，再进入下一层
     */
    private void processFreeRounds(
            List<MangkukulamGameRecordDo> ret,
            GamePlayDo gamePlay,
            OperationEventConfigRecordDo recordDo,
            MangkukulamGameRecordDo parent,
            Map<Long, List<OperationEventConfigRecordDo>> groupedByBatchId) {
        Map<OperationEventConfigRecordDo, MangkukulamGameRecordDo> nextLevelGameRecordMap = new HashMap<>();

        List<Long> gameRecordIds = new ArrayList<>();
        for (Long batchId : recordDo.getGameRecordIds()) {
            List<OperationEventConfigRecordDo> currentLevelRecords = groupedByBatchId.get(batchId);
            currentLevelRecords.sort(Comparator.comparing(OperationEventConfigRecordDo::getIndex));
            // 处理当前层级的每个记录
            MangkukulamGameRecordDo preGameRecord = null;
            for (OperationEventConfigRecordDo record : currentLevelRecords) {
                long gameRecordId = generateGameRecordId();
                MangkukulamGameRecordDo gameRecord = null;
                if (record.getIndex() == 0) {
                    gameRecordIds.add(gameRecordId);
                    // 首回合倍率为空
                    gameRecord = processGameRecord(gamePlay, gameRecordId, parent.getId(), gameRecordId, record, null);
                    ret.add(gameRecord);
                } else {
                    // 后续回合倍率传入前一回合的倍率
                    BigDecimal totalMultipleAmount = ret.get(ret.size() - 1).getRewardResult().getTotalMultiple();
                    gameRecord = processGameRecord(gamePlay, gameRecordId, preGameRecord.getId(), preGameRecord.getBatchId(), record, totalMultipleAmount);
                    ret.add(gameRecord);
                }
                preGameRecord = gameRecord;
                if (CollectionUtils.isNotEmpty(record.getGameRecordIds())) {
                    nextLevelGameRecordMap.put(record, gameRecord);
                }
            }
        }
        parent.setGameRecordIds(gameRecordIds);
        for (Map.Entry<OperationEventConfigRecordDo, MangkukulamGameRecordDo> entry : nextLevelGameRecordMap.entrySet()) {
            OperationEventConfigRecordDo key = entry.getKey();
            MangkukulamGameRecordDo value = entry.getValue();
            processFreeRounds(ret, gamePlay, key, value, groupedByBatchId);
        }
    }

    /**
     * 构建并处理投注记录
     */
    private MangkukulamGameRecordDo processGameRecord(
            GamePlayDo gamePlay,
            long gameRecordId,
            long parentId,
            long batchId,
            OperationEventConfigRecordDo recordDo,
            BigDecimal totalMultiple) {

        // 确定 `TotalMultiple` 参数
        WheelDo<MangkukulamSymbolDo> lamangSymbolWheel = this.convertWheel(recordDo.getWheel());
        MangkukulamSymbolDo[][] symbolArr = lamangSymbolWheel.getSymbolArr();
        // 处理空值
        lamangSymbolWheel.setSymbolArr(MangkukulamSymbolUtil.copySymbolArrWithPlaceHolder(symbolArr));
        RewardResultDo<MangkukulamSymbolDo> rewardResult = mangkukulamRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, lamangSymbolWheel, totalMultiple));
        // 构建时还原
        lamangSymbolWheel.setSymbolArr(symbolArr);
        return buildGameRecord(gameRecordId, parentId, gamePlay.getBetRecordId(), batchId, lamangSymbolWheel, rewardResult);
    }

    private MangkukulamGameRecordDo buildGameRecord(Long id,
                                                    Long parentId,
                                                    Long betRecordId,
                                                    Long batchId,
                                                    WheelDo<MangkukulamSymbolDo> wheel,
                                                    RewardResultDo<MangkukulamSymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return MangkukulamGameRecordDo.builder()
                .id(id)
                .parentId(parentId)
                .betRecordId(betRecordId)
                .batchId(batchId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .index(wheel.getIndex())
                .freeWheelFlag(wheel.isFreeFlag())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .gameRecordIds(null)
                .lastGameFlag(false)
                .build();
    }

    private WheelDo<MangkukulamSymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<MangkukulamSymbolDo>>() {
        });
    }

    private void setLastGameFlag(List<MangkukulamGameRecordDo> gameRecordResult) {
        if (CollectionUtils.isNotEmpty(gameRecordResult)) {
            gameRecordResult.get(gameRecordResult.size() - 1).setLastGameFlag(true);
        }
    }
}