package com.wegame.capi.biz.module.bayanihan.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.BayanihanConfig;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.BayanihanGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.BayanihanSymbolDo;
import com.wegame.capi.biz.domain.biz.template.bayanihan.BayanihanBetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.bayanihan.BayanihanGameHelper;
import com.wegame.capi.biz.module.bayanihan.reward.BayanihanRewardHandler;
import com.wegame.capi.biz.service.template.BetRecordTemplateEsService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.AbTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.TemplateMultipleWarmUpEnum;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import com.wegame.capi.common.utils.JsonGzipUtil;
import com.wegame.capi.common.utils.TtlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.wegame.capi.common.Constants.TRUE_VAL;

@Slf4j
@Component("bayanihanGameTemplatePlay")
@ConditionalOnProperty(name = Constants.BAYANIHAN_PROP, havingValue = TRUE_VAL)
public class BayanihanGameTemplatePlay extends AbstractGame<BayanihanGameRecordDo> {

    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo GAME_INFO = BayanihanGameHelper.BAYANIHAN_GAME_INFO;

    @Autowired
    BayanihanRewardHandler bayanihanRewardHandler;
    @Autowired
    BayanihanConfig bayanihanConfig;
    @Autowired
    BetRecordTemplateEsService<BayanihanBetRecordTemplateEsDo> betRecordTemplateEsService;

    @Override
    public List<BayanihanGameRecordDo> play(BetDo bet, GamePlayDo gamePlay) {
        List<BayanihanGameRecordDo> ret = new ArrayList<>();
        List<GameRecordTemplateDo> gameMultipleRecordDos = getGameRecordTemplate(gamePlay, gamePlay.getBetType());
        // 获取首轮(batchId == 0 and index == 0)
        GameRecordTemplateDo firstRecord = gameMultipleRecordDos.stream()
                .filter(record -> record.getParentId() == 0L && record.getIndex() == 0L)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ERR_INTER));

        // 处理首轮
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        long firstGameRecordId = super.generateGameRecordId();
        BayanihanGameRecordDo firstGameRecord = this.processGameRecord(gamePlay, firstGameRecordId, RECORD_DEFAULT_PARENT_ID, firstGameRecordId, firstRecord, BigDecimal.ZERO);
        ret.add(firstGameRecord);
        // 按 batchId 分组 & 记录 ID 映射
        Map<Long, List<GameRecordTemplateDo>> groupedByBatchId = gameMultipleRecordDos.stream()
                .collect(Collectors.groupingBy(GameRecordTemplateDo::getBatchId));

        // 获取首批次并排序
        List<GameRecordTemplateDo> recordBatchList = groupedByBatchId.getOrDefault(firstRecord.getBatchId(), Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(GameRecordTemplateDo::getIndex))
                .collect(Collectors.toList());
        // 处理每个批次
        BayanihanGameRecordDo lastGameRecord = firstGameRecord;

        LinkedList<Pair<GameRecordTemplateDo,BayanihanGameRecordDo>> queue = new LinkedList<>();
        for (GameRecordTemplateDo recordDo : recordBatchList) {
            if (recordDo.getParentId() != 0L) {
                // 处理首轮普通轮
                long gameRecordId = generateGameRecordId();
                // 使用当前记录的免费轮次数作为中奖免费轮次数
                BayanihanGameRecordDo gameRecord = processGameRecord(gamePlay, gameRecordId, firstGameRecord.getId(), lastGameRecord.getBatchId(), recordDo, BigDecimal.ZERO);
                ret.add(gameRecord);
                lastGameRecord = gameRecord;
            }

            if (CollectionUtils.isEmpty(recordDo.getGameRecordIds())) {
                continue;
            }
            queue.add(Pair.of(recordDo, lastGameRecord));
        }
        // 首轮和免费轮分开处理，广度优先
        while (!queue.isEmpty()) {
            Pair<GameRecordTemplateDo,BayanihanGameRecordDo> currentBatch  = queue.removeFirst();
            processFreeRounds(ret, gamePlay, currentBatch.getKey(), currentBatch.getValue(), groupedByBatchId, totalRewardAmount, queue);
        }
        //// 总中奖金额 不准
        gamePlay.setOverallRewardAmount(totalRewardAmount.get());
//        for (BayanihanGameRecordDo recordDo : ret) {
//            log.info("current collectionInfo: {}", recordDo.getCollectionInfo());
//            BayanihanSymbolUtil.print(recordDo.getWheel().getSymbolArr(), recordDo.getWheel().getIndex());
//        }
        return ret;
    }

    private List<GameRecordTemplateDo> getGameRecordTemplate(GamePlayDo gamePlay, BetTypeEnum betType) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        AbTypeEnum abType = AbTypeEnum.valueOfId(userInfo.getUt());
        AssertUtil.assertNotNull(abType, ErrorCode.ERR_INTER);
        TemplateTypeEnum templateType = bayanihanConfig.getTemplateConfig().getTemplateType(abType);
        AssertUtil.assertNotNull(templateType, ErrorCode.ERR_INTER);
        TemplateMultipleRange templateMultipleRange = getTemplateMultipleRange(templateType, betType);
        AssertUtil.assertNotNull(templateMultipleRange, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateMultipleRange.getStart(), ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateMultipleRange.getEnd(), ErrorCode.ERR_INTER);
        List<Long> betRecordIdList = betRecordTemplateEsService.listIdByMultipleRange(templateType, betType, templateMultipleRange.getStart(), templateMultipleRange.getEnd(), BayanihanBetRecordTemplateEsDo.class);
        AssertUtil.assertNotEmpty(betRecordIdList, ErrorCode.ERR_INTER);
        Long betRecordId = getRandomElement(betRecordIdList);
        log.info("templatePlay: template: {}, es index: {}, template betRecordId :{}, range:{} - {} ", templateType.name(),
                templateType.getBetRecordTemplateName(), betRecordId, templateMultipleRange.getStart(), templateMultipleRange.getEnd());
        List<GameRecordTemplateDo> configRecordList;
        BayanihanBetRecordTemplateEsDo template;
        TemplateMultipleWarmUpEnum warmUp = TemplateMultipleWarmUpEnum.valueOfCode(GameTypeEnum.MANGKUKULAM);
        AssertUtil.assertNotNull(warmUp, ErrorCode.ERR_INTER, "error warmUp");
        if (bayanihanConfig.getTemplateConfig().supportWarmUp(betType, templateMultipleRange)) {
            // 已经被预热的倍率，直接走缓存
            template = betRecordTemplateEsService.getTemplateWithLocalCache(templateType, betRecordId, BayanihanBetRecordTemplateEsDo.class);
        } else {
            // 没有被预热的倍率，需要重新查询
            template = betRecordTemplateEsService.findById(templateType, betRecordId, BayanihanBetRecordTemplateEsDo.class);
        }
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
//        log.info("templatePlay: template betRecordId :{}, range:{} - {}, actual multi:{} ", betRecordId, templateMultipleRange.getStart(), templateMultipleRange.getEnd(), template.getMultiple());
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

    private TemplateMultipleRange getTemplateMultipleRange(TemplateTypeEnum templateType, BetTypeEnum betType) {
        String templateMultipleRange;
        Map<String, Integer> weightMap;
        BayanihanConfig.TemplateRangeConfig templateRangeConfig = bayanihanConfig.getTemplateConfig().getTemplateRangeConfig(templateType);
        if (Objects.equals(BetTypeEnum.NORMAL_BET, betType)) {
            weightMap = templateRangeConfig.getMainGame();
        } else {
            weightMap = templateRangeConfig.getFreeGame();
        }

        AssertUtil.assertNotNull(weightMap, ErrorCode.ERR_INTER);
        templateMultipleRange = randomService.randomKeyByWeight(weightMap);
        AssertUtil.assertStrNotEmpty(templateMultipleRange, ErrorCode.ERR_INTER);
        return TemplateMultipleRange.build(templateMultipleRange);
    }


    /**
     * 递归处理免费轮，确保同层级先处理完，再进入下一层
     */
    private void processFreeRounds(
            List<BayanihanGameRecordDo> ret,
            GamePlayDo gamePlay,
            GameRecordTemplateDo recordDo,
            BayanihanGameRecordDo parent,
            Map<Long, List<GameRecordTemplateDo>> groupedByBatchId,
            AtomicLong totalRewardAmount,
            LinkedList<Pair<GameRecordTemplateDo,BayanihanGameRecordDo>> queue) {

        List<Long> gameRecordIds = new ArrayList<>();
        for (Long batchId : recordDo.getGameRecordIds()) {
            List<GameRecordTemplateDo> currentLevelRecords = groupedByBatchId.get(batchId);
            currentLevelRecords.sort(Comparator.comparing(GameRecordTemplateDo::getIndex));
            // 处理当前层级的每个记录
            BayanihanGameRecordDo preGameRecord = null;
            for (GameRecordTemplateDo record : currentLevelRecords) {
                BigDecimal totalMultipleAmount = ret.get(ret.size() - 1).getRewardResult().getTotalMultiple();
                long gameRecordId = generateGameRecordId();
                BayanihanGameRecordDo gameRecord = null;
                if (record.getIndex() == 0) {
                    gameRecordIds.add(gameRecordId);
                    gameRecord = processGameRecord(gamePlay, gameRecordId, parent.getId(), gameRecordId, record, totalMultipleAmount);
                    ret.add(gameRecord);
                } else {
                    gameRecord = processGameRecord(gamePlay, gameRecordId, preGameRecord.getId(), preGameRecord.getBatchId(), record, totalMultipleAmount);
                    ret.add(gameRecord);
                }
                preGameRecord = gameRecord;
                if (CollectionUtils.isNotEmpty(record.getGameRecordIds())) {
                    queue.offer(Pair.of(record, gameRecord));
                }
            }
        }
        parent.setGameRecordIds(gameRecordIds);
    }

    /**
     * 构建并处理投注记录
     */
    private BayanihanGameRecordDo processGameRecord(
            GamePlayDo gamePlay,
            long gameRecordId,
            long parentId,
            long batchId,
            GameRecordTemplateDo recordDo,
            BigDecimal totalMultiple) {

        // 确定 `TotalMultiple` 参数
        WheelDo<BayanihanSymbolDo> symbolWheel = this.convertWheel(recordDo.getWheel());
        // 处理空值
        RewardResultDo<BayanihanSymbolDo> rewardResult = this.rewardHandler(gamePlay, symbolWheel, totalMultiple, CollectionUtils.isNotEmpty(recordDo.getGameRecordIds()) ?  recordDo.getGameRecordIds().size() : 0);
        // 构建时还原
        BayanihanGameRecordDo.CollectionSnapshot snapshot = convertCollectionSnapshot(recordDo.getExtInfo());
        return buildGameRecord(gameRecordId, parentId, gamePlay.getBetRecordId(), batchId, symbolWheel, rewardResult, snapshot);
    }

    private RewardResultDo<BayanihanSymbolDo> rewardHandler(GamePlayDo gamePlay, WheelDo<BayanihanSymbolDo> wheel, BigDecimal currentMultiple, Integer rewardFreeWheelCount) {
        // 中奖处理
        RewardHandleDo<BayanihanSymbolDo> rewardHandle = RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, currentMultiple, rewardFreeWheelCount);
        RewardResultDo<BayanihanSymbolDo> rewardResult = bayanihanRewardHandler.handle(rewardHandle);
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        return rewardResult;
    }

    private BayanihanGameRecordDo buildGameRecord(Long id,
                                                  Long parentId,
                                                  Long betRecordId,
                                                  Long batchId,
                                                  WheelDo<BayanihanSymbolDo> wheel,
                                                  RewardResultDo<BayanihanSymbolDo> rewardResult,
                                                  BayanihanGameRecordDo.CollectionSnapshot snapshot) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return BayanihanGameRecordDo.builder()
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
                .collectionInfo(snapshot)
                .gameRecordIds(null)
                .lastGameFlag(false)
                .creator(userInfo.getPlayerId())
                .createTime(DateUtil.getCurrentMilli())
                .updater(userInfo.getPlayerId())
                .updateTime(DateUtil.getCurrentMilli())
                .expireAt(TtlUtil.calculateExpiredAt(DateUtil.getCurrentMilli()))
                .build();
    }

    private BayanihanGameRecordDo.CollectionSnapshot convertCollectionSnapshot(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), BayanihanGameRecordDo.CollectionSnapshot.class);
    }

    private WheelDo<BayanihanSymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<BayanihanSymbolDo>>() {
        });
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