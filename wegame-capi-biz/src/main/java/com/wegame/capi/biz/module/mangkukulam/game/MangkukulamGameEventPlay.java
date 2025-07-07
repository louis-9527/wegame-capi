package com.wegame.capi.biz.module.mangkukulam.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.mangkukulam.MangkukulamSymbolUtil;
import com.wegame.capi.biz.module.mangkukulam.reward.MangkukulamRewardHandler;
import com.wegame.capi.biz.service.operation.OperationEventConfigRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.lamang.LamangGameHelper.LAMANG_GAME_INFO;

@Slf4j
@Component("mangkukulamGameEventPlay")
public class MangkukulamGameEventPlay extends AbstractGame<MangkukulamGameRecordDo> {

    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final int RECORD_FIRST_INDEX = 0;
    private static final GameInfoDo gameInfo = LAMANG_GAME_INFO;

    @Autowired
    MangkukulamRewardHandler mangkukulamRewardHandler;

    @Autowired
    OperationEventConfigRecordService operationEventConfigRecordService;

    @Override
    public List<MangkukulamGameRecordDo> play(GamePlayDo gamePlay) {
        List<MangkukulamGameRecordDo> ret = new ArrayList<>();
        List<OperationEventConfigRecordDo> gameMultipleRecordDos = operationEventConfigRecordService.listByBetRecordId(gamePlay.getGameEventDo().getConfigId(), OperationEventTypeEnum.LAM_ANG);
        // 获取首轮(batchId == 0 and index == 0)
        OperationEventConfigRecordDo firstRecord = gameMultipleRecordDos.stream()
                .filter(record -> Objects.equals(RECORD_DEFAULT_PARENT_ID, record.getParentId()) && Objects.equals(RECORD_FIRST_INDEX, record.getIndex()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ERR_INTER));

        // 处理首轮
        long firstGameRecordId = super.generateGameRecordId();
        MangkukulamGameRecordDo firstGameRecord = this.processGameRecord(gamePlay, firstGameRecordId, RECORD_DEFAULT_PARENT_ID, firstGameRecordId, firstRecord, null);
        ret.add(firstGameRecord);
        // 按 batchId 分组 & 记录 ID 映射
        Map<Long, List<OperationEventConfigRecordDo>> groupedByBatchId = gameMultipleRecordDos.stream()
                .collect(Collectors.groupingBy(OperationEventConfigRecordDo::getBatchId));

        // 获取首批次并排序
        List<OperationEventConfigRecordDo> firstBatchList = groupedByBatchId.getOrDefault(firstRecord.getBatchId(), Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(OperationEventConfigRecordDo::getIndex))
                .collect(Collectors.toList());
        // 处理每个批次
        MangkukulamGameRecordDo lastGameRecord = firstGameRecord;

        for (OperationEventConfigRecordDo recordDo : firstBatchList) {
            // 非首回合
            if (!Objects.equals(RECORD_DEFAULT_PARENT_ID, recordDo.getParentId())) {
                // 处理首轮普通轮
                long gameRecordId = generateGameRecordId();
                // 用上一回合的倍率
                MangkukulamGameRecordDo gameRecord = processGameRecord(gamePlay, gameRecordId, firstGameRecord.getId(), lastGameRecord.getBatchId(), recordDo, lastGameRecord.getRewardResult().getTotalMultiple());
                ret.add(gameRecord);
                lastGameRecord = gameRecord;
            }

            if (CollectionUtils.isEmpty(recordDo.getGameRecordIds())) {
                continue;
            }
            // 处理免费轮
            processFreeRounds(ret, gamePlay, recordDo, lastGameRecord, groupedByBatchId);
        }
        // 设置结束
        if (CollectionUtils.isNotEmpty(ret)) {
            ret.get(ret.size() - 1).setLastGameFlag(true);
        }
        return ret;
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
        int drawTimes = MangkukulamSymbolUtil.countNewScatterAndScatter1InWheel(wheel.getSymbolArr());
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
                .jackpotDrawTimes(drawTimes)
                .build();
    }

    private WheelDo<MangkukulamSymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<MangkukulamSymbolDo>>() {
        });
    }
}