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
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import com.wegame.capi.biz.domain.biz.template.mangkukulam.MangkukulamBetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.mangkukulam.MangkukulamSymbolUtil;
import com.wegame.capi.biz.module.mangkukulam.reward.MangkukulamRewardHandler;
import com.wegame.capi.biz.service.template.BetRecordTemplateEsService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.VolatilityTypeEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.MangkukulamTagEnum;
import com.wegame.capi.common.enums.TemplateMultipleWarmUpEnum;
import com.wegame.capi.common.utils.JsonGzipUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper.MANGKUKULAM_GAME_INFO;
import static com.wegame.capi.common.enums.TemplateTypeEnum.DIWATA_MAYARI;
import static com.wegame.capi.common.enums.TemplateTypeEnum.MANGKUKULAM;

@Slf4j
@Component("mangkukulamGameTemplatePlay")
public class MangkukulamGameTemplatePlay extends AbstractGame<MangkukulamGameRecordDo> {

    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final int RECORD_FIRST_INDEX = 0;
    private static final GameInfoDo gameInfo = MANGKUKULAM_GAME_INFO;

    @Autowired
    MangkukulamRewardHandler mangkukulamRewardHandler;

    @Autowired
    MangkukulamConfig mangkukulamConfig;

    @Autowired
    BetRecordTemplateEsService<MangkukulamBetRecordTemplateEsDo> betRecordTemplateEsService;

    @Override
    public List<MangkukulamGameRecordDo> play(GamePlayDo gamePlay) {
        List<MangkukulamGameRecordDo> ret = new ArrayList<>();
        if (BetSubTypeEnum.isFirstStep(gamePlay.getBetSubType().getCode()) && Objects.nonNull(gamePlay.getVolatility())) {
            // 执行完普通轮和免费轮
            processInitialStage(gamePlay, ret);
            Long parentGameRecordId = ret.get(ret.size()-1).getId();
            processFreeStage(gamePlay, ret , null, parentGameRecordId);
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
            processFreeStage(gamePlay, ret, context.getParentBetRecordId(), context.getParentGameRecordId());
            setLastGameFlag(ret);
        }
        MangkukulamSymbolUtil.postProcessPlaceholderSymbol(ret);
        // 结果验证数据添加
        //// 总中奖金额
        long totalRewardAmount = ret.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        gamePlay.setOverallRewardAmount(totalRewardAmount);
        return ret;
    }

    /**
     * 处理初始轮
     * @param gamePlay
     * @param ret
     */
    private void processInitialStage(GamePlayDo gamePlay, List<MangkukulamGameRecordDo> ret) {
        List<GameRecordTemplateDo> gameMultipleRecordDos = getGameRecordTemplate(gamePlay);

        // 获取首轮(batchId == 0 and index == 0)
        GameRecordTemplateDo firstRecord = gameMultipleRecordDos.stream()
                .filter(record -> record.getParentId() == 0L && record.getIndex() == 0L)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ERR_INTER));

        // 处理首轮
        long firstGameRecordId = super.generateGameRecordId();
        MangkukulamGameRecordDo firstGameRecord = this.processGameRecord(gamePlay, firstGameRecordId, RECORD_DEFAULT_PARENT_ID, firstGameRecordId, null, firstRecord, BigDecimal.ZERO);
        ret.add(firstGameRecord);
        // 按 batchId 分组 & 记录 ID 映射
        Map<Long, List<GameRecordTemplateDo>> groupedByBatchId = gameMultipleRecordDos.stream()
                .collect(Collectors.groupingBy(GameRecordTemplateDo::getBatchId));

        // 获取首批次并排序
        List<GameRecordTemplateDo> recordBatchList = groupedByBatchId.getOrDefault(firstRecord.getBatchId(), Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(GameRecordTemplateDo::getIndex))
                .collect(Collectors.toList());
        // 处理首个批次
        MangkukulamGameRecordDo lastGameRecord = firstGameRecord;

        for (GameRecordTemplateDo recordDo : recordBatchList) {
            if (recordDo.getParentId() != 0L) {
                // 处理首轮普通轮
                long gameRecordId = generateGameRecordId();
                MangkukulamGameRecordDo gameRecord = processGameRecord(gamePlay, gameRecordId, firstGameRecord.getId(),
                        lastGameRecord.getBatchId(), null, recordDo, lastGameRecord.getRewardResult().getTotalMultiple());
                ret.add(gameRecord);
                lastGameRecord = gameRecord;
            }
        }
    }

    /**
     * 处理免费轮
     * @param gamePlay
     * @param ret
     * @param parentBetRecordId
     */
    private void processFreeStage(GamePlayDo gamePlay, List<MangkukulamGameRecordDo> ret, Long parentBetRecordId, Long parentGameRecordId) {
        List<GameRecordTemplateDo> gameMultipleRecordDos = getGameRecordTemplate(gamePlay);

        List<Long> gameRecordIds = new ArrayList<>();
        // 获取第一批免费轮
        List<GameRecordTemplateDo> firstBatchFreeWheel = gameMultipleRecordDos.stream()
                .filter(record -> record.getParentId() == 0L && record.getIndex() == 0L)
                .collect(Collectors.toList());

        // 按 batchId 分组 & 记录 ID 映射
        Map<Long, List<GameRecordTemplateDo>> groupedByBatchId = gameMultipleRecordDos.stream()
                .collect(Collectors.groupingBy(GameRecordTemplateDo::getBatchId));

        Map<GameRecordTemplateDo, MangkukulamGameRecordDo> nextLevelGameRecordMap = new HashMap<>();

        // 将第一轮免费轮的id作为第一批的id
        for (Long batchId : firstBatchFreeWheel.stream().map(GameRecordTemplateDo::getId).collect(Collectors.toList())) {
            processOneBatch(gamePlay, groupedByBatchId, batchId, nextLevelGameRecordMap, gameRecordIds, parentGameRecordId, parentBetRecordId, ret);
        }

        for (Map.Entry<GameRecordTemplateDo, MangkukulamGameRecordDo> entry : nextLevelGameRecordMap.entrySet()) {
            GameRecordTemplateDo key = entry.getKey();
            MangkukulamGameRecordDo value = entry.getValue();
            processFreeRound(ret, gamePlay, key, value, groupedByBatchId, parentBetRecordId);
        }

    }

    private void processOneBatch(GamePlayDo gamePlay,
                                 Map<Long, List<GameRecordTemplateDo>> groupedByBatchId,
                                 Long batchId,
                                 Map<GameRecordTemplateDo, MangkukulamGameRecordDo> nextLevelGameRecordMap,
                                 List<Long> gameRecordIds,
                                 Long parentBetRecordId,
                                 Long parentGameRecordId,
                                 List<MangkukulamGameRecordDo> ret) {
        List<GameRecordTemplateDo> currentLevelRecords = groupedByBatchId.get(batchId);
        currentLevelRecords.sort(Comparator.comparing(GameRecordTemplateDo::getIndex));
        // 处理当前层级的每个记录
        MangkukulamGameRecordDo preGameRecord = null;
        for (GameRecordTemplateDo record : currentLevelRecords) {
            BigDecimal totalMultipleAmount = ret.get(ret.size() - 1).getRewardResult().getTotalMultiple();
            long gameRecordId = generateGameRecordId();
            MangkukulamGameRecordDo gameRecord = null;
            if (record.getIndex() == 0) {
                gameRecordIds.add(gameRecordId);
                gameRecord = processGameRecord(gamePlay, gameRecordId, parentGameRecordId, gameRecordId, parentBetRecordId, record, totalMultipleAmount);
                ret.add(gameRecord);
            } else {
                gameRecord = processGameRecord(gamePlay, gameRecordId, preGameRecord.getId(), preGameRecord.getBatchId(), parentBetRecordId, record, totalMultipleAmount);
                ret.add(gameRecord);
            }
            preGameRecord = gameRecord;
            if (CollectionUtils.isNotEmpty(record.getGameRecordIds())) {
                nextLevelGameRecordMap.put(record, gameRecord);
            }
        }
    }

    private void processFreeRound(List<MangkukulamGameRecordDo> ret,
                                  GamePlayDo gamePlay,
                                  GameRecordTemplateDo recordDo,
                                  MangkukulamGameRecordDo parent,
                                  Map<Long, List<GameRecordTemplateDo>> groupedByBatchId,
                                  Long parentBetRecordId) {

        Map<GameRecordTemplateDo, MangkukulamGameRecordDo> nextLevelGameRecordMap = new HashMap<>();

        List<Long> gameRecordIds = new ArrayList<>();
        for (Long batchId : recordDo.getGameRecordIds()) {
            processOneBatch(gamePlay, groupedByBatchId, batchId, nextLevelGameRecordMap, gameRecordIds, parent.getId(),parentBetRecordId,ret);
        }
        parent.setGameRecordIds(gameRecordIds);
        for (Map.Entry<GameRecordTemplateDo, MangkukulamGameRecordDo> entry : nextLevelGameRecordMap.entrySet()) {
            GameRecordTemplateDo key = entry.getKey();
            MangkukulamGameRecordDo value = entry.getValue();
            processFreeRound(ret, gamePlay, key, value, groupedByBatchId, parentBetRecordId);
        }
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
            return MangkukulamTagEnum.FREE_GAME_STAGE.getCode();
        }
        if (BetSubTypeEnum.isFirstStep(subType.getCode())) {
            if (Objects.equals(betType, BetTypeEnum.NORMAL_BET)) {
                return MangkukulamTagEnum.NORMAL_BET_INITIAL_STAGE.getCode();
            } else if (Objects.equals(betType, BetTypeEnum.BUY_BET)) {
                return MangkukulamTagEnum.BUY_BET_INITIAL_STAGE.getCode();
            }
        }
        return null;
    }

    private List<GameRecordTemplateDo> getGameRecordTemplate(GamePlayDo gamePlay) {
        BetTypeEnum betType = gamePlay.getBetType();
        BetSubTypeEnum subType = gamePlay.getBetSubType();
        VolatilityTypeEnum volatilityType = gamePlay.getVolatility();
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
        log.info("templatePlay: template betRecordId :{}, range:{} - {} ", betRecordId, templateMultipleRange.getStart(), templateMultipleRange.getEnd());
        List<GameRecordTemplateDo> configRecordList;
        MangkukulamBetRecordTemplateEsDo template;
        TemplateMultipleWarmUpEnum warmUp = TemplateMultipleWarmUpEnum.valueOfCode(GameTypeEnum.MANGKUKULAM);
        AssertUtil.assertNotNull(warmUp, ErrorCode.ERR_INTER, "error warmUp");
        if (warmUp.canWarmUp(tag, templateMultipleRange.getEnd())) {
            // 已经被预热的倍率，直接走缓存
            if (Objects.equals(betType, BetTypeEnum.NORMAL_BET)) {
                template = betRecordTemplateEsService.getTemplateWithLocalCache(MANGKUKULAM, betRecordId, MangkukulamBetRecordTemplateEsDo.class);
            } else if (Objects.equals(betType, BetTypeEnum.BUY_BET)) {
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
            log.error("template is empty, betType:{}, range start: {}, end: {}", betType, templateMultipleRange.getStart(), templateMultipleRange.getEnd());
        }
        AssertUtil.assertNotEmpty(configRecordList, ErrorCode.ERR_INTER);
        // 结果验证数据添加
        gamePlay.setTemplateId(betRecordId);
        gamePlay.setTemplateMultipleRange(templateMultipleRange);
        return configRecordList;
    }

    /**
     * 构建并处理投注记录
     */
    private MangkukulamGameRecordDo processGameRecord(
            GamePlayDo gamePlay,
            Long currentGameRecordId,
            Long parentGameRecordId,
            Long batchId,
            Long parentBetRecordId,
            GameRecordTemplateDo templateDo,
            BigDecimal totalMultiple) {

        // 确定 `TotalMultiple` 参数
        WheelDo<MangkukulamSymbolDo> lamangSymbolWheel = this.convertWheel(templateDo.getWheel());
        MangkukulamSymbolDo[][] symbolArr = lamangSymbolWheel.getSymbolArr();
        // 处理空值
        lamangSymbolWheel.setSymbolArr(MangkukulamSymbolUtil.copySymbolArrWithPlaceHolder(symbolArr));
        RewardResultDo<MangkukulamSymbolDo> rewardResult = mangkukulamRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, lamangSymbolWheel, totalMultiple));
        // 构建时还原
        lamangSymbolWheel.setSymbolArr(symbolArr);
        return buildGameRecord(currentGameRecordId, parentGameRecordId, gamePlay.getBetRecordId(), batchId, parentBetRecordId, lamangSymbolWheel, rewardResult);
    }

    private MangkukulamGameRecordDo buildGameRecord(Long currentGameRecordId,
                                                    Long parentGameRecordId,
                                                    Long betRecordId,
                                                    Long batchId,
                                                    Long parentBetRecordId,
                                                    WheelDo<MangkukulamSymbolDo> wheel,
                                                    RewardResultDo<MangkukulamSymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return MangkukulamGameRecordDo.builder()
                .id(currentGameRecordId)
                .parentId(parentGameRecordId)
                .betRecordId(betRecordId)
                .batchId(batchId)
                .parentBetRecordId(parentBetRecordId)
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