package com.wegame.capi.biz.module.bayanihan.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.BayanihanGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.BayanihanSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.bayanihan.BayanihanGameHelper;
import com.wegame.capi.biz.module.bayanihan.reward.BayanihanRewardHandler;
import com.wegame.capi.biz.service.operation.OperationEventConfigRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.utils.TtlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.wegame.capi.common.Constants.TRUE_VAL;

@Slf4j
@Component("bayanihanGameEventPlay")
@ConditionalOnProperty(name = Constants.BAYANIHAN_PROP, havingValue = TRUE_VAL)
public class BayanihanGameEventPlay extends AbstractGame<BayanihanGameRecordDo> {

    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo gameInfo = BayanihanGameHelper.BAYANIHAN_GAME_INFO;

    @Autowired
    BayanihanRewardHandler bayanihanRewardHandler;

    @Autowired
    OperationEventConfigRecordService operationEventConfigRecordService;

    @Override
    public List<BayanihanGameRecordDo> play(BetDo bet, GamePlayDo gamePlay) {
        List<BayanihanGameRecordDo> ret = new ArrayList<>();
        List<OperationEventConfigRecordDo> gameMultipleRecordDos = operationEventConfigRecordService.listByBetRecordId(gamePlay.getGameEventDo().getConfigId(), OperationEventTypeEnum.ATLANTIS);
        // 获取首轮(batchId == 0 and index == 0)
        OperationEventConfigRecordDo firstRecord = gameMultipleRecordDos.stream()
                .filter(record -> record.getParentId() == 0L && record.getIndex() == 0L)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ERR_INTER));

        // 处理首轮
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        long firstGameRecordId = super.generateGameRecordId();
        BayanihanGameRecordDo firstGameRecord = this.processGameRecord(gamePlay, firstGameRecordId, RECORD_DEFAULT_PARENT_ID, firstGameRecordId, firstRecord, BigDecimal.ZERO);
        ret.add(firstGameRecord);
        // 按 batchId 分组 & 记录 ID 映射
        Map<Long, List<OperationEventConfigRecordDo>> groupedByBatchId = gameMultipleRecordDos.stream()
                .collect(Collectors.groupingBy(OperationEventConfigRecordDo::getBatchId));

        // 获取首批次并排序
        List<OperationEventConfigRecordDo> recordBatchList = groupedByBatchId.getOrDefault(firstRecord.getBatchId(), Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(OperationEventConfigRecordDo::getIndex))
                .collect(Collectors.toList());
        // 处理每个批次
        BayanihanGameRecordDo lastGameRecord = firstGameRecord;

        for (OperationEventConfigRecordDo recordDo : recordBatchList) {
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

            // 处理免费轮
            processFreeRounds(ret, gamePlay, recordDo, lastGameRecord, groupedByBatchId, totalRewardAmount);
        }
        //// 总中奖金额
//        gamePlay.setOverallRewardAmount(totalRewardAmount.get());
        return ret;
    }


    /**
     * 递归处理免费轮，确保同层级先处理完，再进入下一层
     */
    private void processFreeRounds(
            List<BayanihanGameRecordDo> ret,
            GamePlayDo gamePlay,
            OperationEventConfigRecordDo recordDo,
            BayanihanGameRecordDo parent,
            Map<Long, List<OperationEventConfigRecordDo>> groupedByBatchId,
            AtomicLong totalRewardAmount) {

        Map<OperationEventConfigRecordDo, BayanihanGameRecordDo> nextLevelGameRecordMap = new LinkedHashMap<>();

        List<Long> gameRecordIds = new ArrayList<>();
        for (Long batchId : recordDo.getGameRecordIds()) {
            List<OperationEventConfigRecordDo> currentLevelRecords = groupedByBatchId.get(batchId);
            currentLevelRecords.sort(Comparator.comparing(OperationEventConfigRecordDo::getIndex));
            // 处理当前层级的每个记录
            BayanihanGameRecordDo preGameRecord = null;
            for (OperationEventConfigRecordDo record : currentLevelRecords) {
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
                    nextLevelGameRecordMap.put(record, gameRecord);
                }
            }
        }
        parent.setGameRecordIds(gameRecordIds);
        for (Map.Entry<OperationEventConfigRecordDo, BayanihanGameRecordDo> entry : nextLevelGameRecordMap.entrySet()) {
            OperationEventConfigRecordDo key = entry.getKey();
            BayanihanGameRecordDo value = entry.getValue();
            processFreeRounds(ret, gamePlay, key, value, groupedByBatchId, totalRewardAmount);
        }
    }

    /**
     * 构建并处理投注记录
     */
    private BayanihanGameRecordDo processGameRecord(
            GamePlayDo gamePlay,
            long gameRecordId,
            long parentId,
            long batchId,
            OperationEventConfigRecordDo recordDo,
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
        RewardHandleDo<BayanihanSymbolDo> rewardHandle = RewardHandlerBuilder.build(gameInfo, gamePlay, wheel, currentMultiple, rewardFreeWheelCount);
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
}