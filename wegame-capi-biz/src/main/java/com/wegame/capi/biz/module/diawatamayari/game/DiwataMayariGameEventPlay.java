package com.wegame.capi.biz.module.diawatamayari.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.domain.biz.game.DiwataMayariGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.diawatamayari.DiwataMayariGameHelper;
import com.wegame.capi.biz.module.diawatamayari.DiwataMayariSymbolUtil;
import com.wegame.capi.biz.module.diawatamayari.reward.DiwataMayariRewardHandler;
import com.wegame.capi.biz.service.operation.OperationEventConfigRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.common.utils.TtlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component("diwataMayariGameEventPlay")
public class DiwataMayariGameEventPlay extends AbstractGame<DiwataMayariGameRecordDo> {

    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo gameInfo = DiwataMayariGameHelper.DIWATA_MAYARI_GAME_INFO;

    @Autowired
    DiwataMayariRewardHandler diwataMayariRewardHandler;

    @Autowired
    OperationEventConfigRecordService operationEventConfigRecordService;

    @Override
    public List<DiwataMayariGameRecordDo> play(GamePlayDo gamePlay) {
        List<DiwataMayariGameRecordDo> ret = new ArrayList<>();
        List<OperationEventConfigRecordDo> gameMultipleRecordDos = operationEventConfigRecordService.listByBetRecordId(gamePlay.getGameEventDo().getConfigId(), OperationEventTypeEnum.DIWATA_MAYARI);
        // 获取首轮(batchId == 0 and index == 0)
        OperationEventConfigRecordDo firstRecord = gameMultipleRecordDos.stream()
                .filter(record -> record.getParentId() == 0L && record.getIndex() == 0L)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ERR_INTER));

        // 处理首轮
        long firstGameRecordId = super.generateGameRecordId();
        DiwataMayariGameRecordDo firstGameRecord = this.processGameRecord(gamePlay, firstGameRecordId, RECORD_DEFAULT_PARENT_ID, firstGameRecordId, firstRecord, BigDecimal.ZERO);
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
        DiwataMayariGameRecordDo lastGameRecord = firstGameRecord;

        for (OperationEventConfigRecordDo recordDo : recordBatchList) {
            if (recordDo.getParentId() != 0L) {
                // 处理首轮普通轮
                long gameRecordId = generateGameRecordId();
                DiwataMayariGameRecordDo gameRecord = processGameRecord(gamePlay, gameRecordId, firstGameRecord.getId(), lastGameRecord.getBatchId(), recordDo, lastGameRecord.getRewardResult().getTotalMultiple());
                ret.add(gameRecord);
                lastGameRecord = gameRecord;
            }

            if (CollectionUtils.isEmpty(recordDo.getGameRecordIds())) {
                continue;
            }
            // 处理免费轮
            processFreeRounds(ret, gamePlay, recordDo, lastGameRecord, groupedByBatchId);
        }
        //// 总中奖金额
        long totalRewardAmount = ret.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        gamePlay.setOverallRewardAmount(totalRewardAmount);
        return ret;
    }


    /**
     * 递归处理免费轮，确保同层级先处理完，再进入下一层
     */
    private void processFreeRounds(
            List<DiwataMayariGameRecordDo> ret,
            GamePlayDo gamePlay,
            OperationEventConfigRecordDo recordDo,
            DiwataMayariGameRecordDo parent,
            Map<Long, List<OperationEventConfigRecordDo>> groupedByBatchId) {
        Map<OperationEventConfigRecordDo, DiwataMayariGameRecordDo> nextLevelGameRecordMap = new HashMap<>();

        List<Long> gameRecordIds = new ArrayList<>();
        for (Long batchId : recordDo.getGameRecordIds()) {
            List<OperationEventConfigRecordDo> currentLevelRecords = groupedByBatchId.get(batchId);
            currentLevelRecords.sort(Comparator.comparing(OperationEventConfigRecordDo::getIndex));
            // 处理当前层级的每个记录
            DiwataMayariGameRecordDo preGameRecord = null;
            for (OperationEventConfigRecordDo record : currentLevelRecords) {
                BigDecimal totalMultipleAmount = ret.get(ret.size() - 1).getRewardResult().getTotalMultiple();
                long gameRecordId = generateGameRecordId();
                DiwataMayariGameRecordDo gameRecord = null;
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
        for (Map.Entry<OperationEventConfigRecordDo, DiwataMayariGameRecordDo> entry : nextLevelGameRecordMap.entrySet()) {
            OperationEventConfigRecordDo key = entry.getKey();
            DiwataMayariGameRecordDo value = entry.getValue();
            processFreeRounds(ret, gamePlay, key, value, groupedByBatchId);
        }
    }

    /**
     * 构建并处理投注记录
     */
    private DiwataMayariGameRecordDo processGameRecord(
            GamePlayDo gamePlay,
            long gameRecordId,
            long parentId,
            long batchId,
            OperationEventConfigRecordDo recordDo,
            BigDecimal totalMultiple) {

        // 确定 `TotalMultiple` 参数
        WheelDo<DiwataMayariSymbolDo> diwataMayariSymbolWheel = this.convertWheel(recordDo.getWheel());
        DiwataMayariSymbolDo[][] symbolArr = diwataMayariSymbolWheel.getSymbolArr();
        // 处理空值
        diwataMayariSymbolWheel.setSymbolArr(DiwataMayariSymbolUtil.handleSymbolNullArr(symbolArr));
        RewardResultDo<DiwataMayariSymbolDo> rewardResult = diwataMayariRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, diwataMayariSymbolWheel, totalMultiple));
        // 构建时还原
        diwataMayariSymbolWheel.setSymbolArr(symbolArr);
        return buildGameRecord(gameRecordId, parentId, gamePlay.getBetRecordId(), batchId, diwataMayariSymbolWheel, rewardResult);


    }

    private DiwataMayariGameRecordDo buildGameRecord(Long id,
                                                     Long parentId,
                                                     Long betRecordId,
                                                     Long batchId,
                                                     WheelDo<DiwataMayariSymbolDo> wheel,
                                                     RewardResultDo<DiwataMayariSymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return DiwataMayariGameRecordDo.builder()
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
                .creator(userInfo.getPlayerId())
                .createTime(DateUtil.getCurrentMilli())
                .updater(userInfo.getPlayerId())
                .updateTime(DateUtil.getCurrentMilli())
                .expireAt(TtlUtil.calculateExpiredAt(DateUtil.getCurrentMilli()))
                .build();
    }

    private WheelDo<DiwataMayariSymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<DiwataMayariSymbolDo>>() {
        });
    }
}