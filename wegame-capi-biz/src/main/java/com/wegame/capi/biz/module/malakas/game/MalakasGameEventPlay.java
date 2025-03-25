package com.wegame.capi.biz.module.malakas.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.domain.biz.game.GameEventDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.biz.module.malakas.MalakasProbabilisticUtil;
import com.wegame.capi.biz.module.malakas.MalakasSymbolUtil;
import com.wegame.capi.biz.service.operation.OperationEventConfigRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.common.enums.RewardTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.common.enums.SymbolSizeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_GAME_INFO;
import static com.wegame.capi.biz.module.malakas.MalakasSymbolUtil.*;

@Slf4j
@Component("malakasGameEventPlay")
public class MalakasGameEventPlay extends AbstractGame<MalakasGameRecordDo> {

    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo gameInfo = MALAKAS_GAME_INFO;

    @Autowired
    MalakasConfig malakasConfig;
    @Autowired
    RewardHandler malakasRewardHandler;
    @Autowired
    SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    OperationEventConfigRecordService operationEventConfigRecordService;

    @Override
    public List<MalakasGameRecordDo> play(GamePlayDo gamePlay) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        List<MalakasGameRecordDo> ret = new ArrayList<>();
        GameEventDo gameEventDo = gamePlay.getGameEventDo();
        List<OperationEventConfigRecordDo> gameMultipleRecordDos = operationEventConfigRecordService.listByBetRecordId(1902971398703054848L, OperationEventTypeEnum.MALAKAS_DYNAMIC);


        // 获取首轮(batchId == 0 and index == 0)
        OperationEventConfigRecordDo firstRecord = gameMultipleRecordDos.stream()
                .filter(record -> record.getParentId() == 0L && record.getIndex() == 0L)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ERR_INTER));

        // 处理首轮
        long firstGameRecordId = super.generateGameRecordId();
        MalakasGameRecordDo firstGameRecord = this.processGameRecord(gamePlay, firstGameRecordId, RECORD_DEFAULT_PARENT_ID, firstGameRecordId, firstRecord,BigDecimal.ONE);
        ret.add(firstGameRecord);
        // 按 batchId 分组 & 记录 ID 映射
        Map<Long, List<OperationEventConfigRecordDo>> groupedByBatchId = gameMultipleRecordDos.stream()
                .collect(Collectors.groupingBy(OperationEventConfigRecordDo::getBatchId));

        Map<Long, OperationEventConfigRecordDo> multipleRecordMap = gameMultipleRecordDos.stream()
                .collect(Collectors.toMap(OperationEventConfigRecordDo::getId, record -> record));

        // 获取首批次并排序
        List<OperationEventConfigRecordDo> recordBatchList = groupedByBatchId.getOrDefault(firstRecord.getBatchId(), Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(OperationEventConfigRecordDo::getIndex))
                .collect(Collectors.toList());
        // 处理每个批次
        MalakasGameRecordDo lastGameRecord = firstGameRecord;

        for (OperationEventConfigRecordDo recordDo : recordBatchList) {
            if (recordDo.getParentId() != 0L) {
                // 处理首轮普通轮
                long gameRecordId = generateGameRecordId();
                MalakasGameRecordDo gameRecord = processGameRecord(gamePlay, gameRecordId, firstGameRecord.getId(), lastGameRecord.getBatchId(), recordDo,BigDecimal.ONE);
                ret.add(gameRecord);
                lastGameRecord = gameRecord;
            }

            if (CollectionUtils.isEmpty(recordDo.getGameRecordIds())) {
                continue;
            }
            // 处理免费轮
            processFreeRounds(ret, gamePlay, recordDo, multipleRecordMap, firstGameRecord, groupedByBatchId);
        }
        return ret;
    }


    /**
     * 递归处理免费轮，确保同层级先处理完，再进入下一层
     */
    private void processFreeRounds(
            List<MalakasGameRecordDo> ret,
            GamePlayDo gamePlay,
            OperationEventConfigRecordDo recordDo,
            Map<Long, OperationEventConfigRecordDo> multipleRecordMap,
            MalakasGameRecordDo parent,
            Map<Long, List<OperationEventConfigRecordDo>> groupedByBatchId) {
        Map<OperationEventConfigRecordDo, MalakasGameRecordDo> nextLevelGameRecordMap = new HashMap<>();

        List<Long> gameRecordIds = new ArrayList<>();
        for (Long batchId : recordDo.getGameRecordIds()) {
            List<OperationEventConfigRecordDo> currentLevelRecords = groupedByBatchId.get(batchId);
            currentLevelRecords.sort(Comparator.comparing(OperationEventConfigRecordDo::getIndex));
            // 处理当前层级的每个记录
            MalakasGameRecordDo preGameRecord = null;
            for (OperationEventConfigRecordDo record : currentLevelRecords) {
                BigDecimal totalMultipleAmount = ret.get(ret.size() - 1).getRewardResult().getTotalMultiple();
                long gameRecordId = generateGameRecordId();
                MalakasGameRecordDo gameRecord = null;
                if (record.getIndex() == 0) {
                    gameRecordIds.add(gameRecordId);
                    gameRecord = processGameRecord(gamePlay, gameRecordId, parent.getId(), gameRecordId, record,totalMultipleAmount);
                    ret.add(gameRecord);
                } else {
                    gameRecord = processGameRecord(gamePlay, gameRecordId, preGameRecord.getId(), preGameRecord.getBatchId(), record,totalMultipleAmount);
                    ret.add(gameRecord);
                }
                preGameRecord = gameRecord;
                if (CollectionUtils.isNotEmpty(record.getGameRecordIds())) {
                    nextLevelGameRecordMap.put(record, gameRecord);
                }
            }
        }
        parent.setGameRecordIds(gameRecordIds);
        for (Map.Entry<OperationEventConfigRecordDo, MalakasGameRecordDo> entry : nextLevelGameRecordMap.entrySet()) {
            OperationEventConfigRecordDo key = entry.getKey();
            MalakasGameRecordDo value = entry.getValue();
            processFreeRounds(ret, gamePlay, key, multipleRecordMap, value, groupedByBatchId);
        }
    }

    /**
     * 构建并处理投注记录
     */
    private MalakasGameRecordDo processGameRecord(
            GamePlayDo gamePlay,
            long gameRecordId,
            long parentId,
            long batchId,
            OperationEventConfigRecordDo recordDo,
            BigDecimal totalMultiple) {

        // 确定 `TotalMultiple` 参数
        WheelDo<MalakasSymbolDo> malakasSymbolWheel = this.convertWheel(recordDo.getWheel());
        MalakasSymbolDo[][] symbolArr = malakasSymbolWheel.getSymbolArr();

        malakasSymbolWheel.setSymbolArr(handleSymbolNullArr(symbolArr));
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(
                RewardHandlerBuilder.build(gameInfo, gamePlay, malakasSymbolWheel, totalMultiple)
        );
        return buildGameRecord(gameRecordId, parentId, gamePlay.getBetRecordId(), batchId, malakasSymbolWheel, rewardResult);


    }

    private MalakasGameRecordDo buildGameRecord(Long id,
                                                Long parentId,
                                                Long betRecordId,
                                                Long batchId,
                                                WheelDo<MalakasSymbolDo> wheel,
                                                RewardResultDo<MalakasSymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return MalakasGameRecordDo.builder()
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

    private WheelDo<MalakasSymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<MalakasSymbolDo>>() {
        });
    }

    private boolean canContinuePlayOnPreWheel(RewardResultDo<MalakasSymbolDo> rewardResult) {
        if (Objects.isNull(rewardResult)) {
            return false;
        }
        if (Objects.equals(rewardResult.getRewardType(), RewardTypeEnum.NONE)) {
            return false;
        }
        return CollectionUtils.isNotEmpty(rewardResult.getRewardLineDetailList());
    }

}