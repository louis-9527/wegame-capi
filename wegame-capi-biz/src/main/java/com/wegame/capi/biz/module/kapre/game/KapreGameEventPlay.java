package com.wegame.capi.biz.module.kapre.game;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.KapreGameRecordDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.KapreSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.kapre.KapreGameHelper;
import com.wegame.capi.biz.module.kapre.KapreSymbolUtil;
import com.wegame.capi.biz.module.kapre.reward.KapreRewardHandler;
import com.wegame.capi.biz.service.operation.OperationEventConfigRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("kapreGameEventPlay")
public class KapreGameEventPlay extends AbstractGame<KapreGameRecordDo> {

    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo gameInfo = KapreGameHelper.KAPRE_GAME_INFO;

    @Autowired
    KapreRewardHandler kapreRewardHandler;

    @Autowired
    OperationEventConfigRecordService operationEventConfigRecordService;

    @Override
    public List<KapreGameRecordDo> play(GamePlayDo gamePlay) {
        List<KapreGameRecordDo> ret = new ArrayList<>();
        List<OperationEventConfigRecordDo> gameMultipleRecordDos = operationEventConfigRecordService.listByBetRecordId(gamePlay.getGameEventDo().getConfigId(), OperationEventTypeEnum.KAPRE);
        AssertUtil.assertNotEmpty(gameMultipleRecordDos, ErrorCode.ERR_INTER, "gameMultipleRecordDos is empty");
        // 获取首轮(parentId == 0 and index == 0（kapre 所有index都是0）)
        OperationEventConfigRecordDo firstRecord = gameMultipleRecordDos.stream()
                .filter(record -> record.getParentId() == 0L && record.getIndex() == 0L)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ERR_INTER));

        // 按 batchId 分组,batchId和gameRecordId是同一个值。kapre没有消除轮，一个batchId只对应一条记录
        Map<Long, OperationEventConfigRecordDo> batchIdMap = gameMultipleRecordDos.stream()
                .collect(Collectors.toMap(OperationEventConfigRecordDo::getBatchId, recordDo -> recordDo, (recordDo1, recordDo2) -> recordDo1));

        // 处理首轮
        KapreGameRecordDo firstGameRecord = this.processGameRecord(gamePlay, super.generateGameRecordId(), RECORD_DEFAULT_PARENT_ID, firstRecord);
        ret.add(firstGameRecord);
        if (CollectionUtil.isNotEmpty(firstRecord.getGameRecordIds())) {
            // 首轮有免费轮
            LinkedList<KapreEventFreeWheelHandleDo> taskLinkedList = new LinkedList<>();
            taskLinkedList.addLast(new KapreEventFreeWheelHandleDo(firstGameRecord, firstRecord.getGameRecordIds()));
            // 迭代替代递归处理
            this.loopHandle(taskLinkedList, ret, gamePlay, batchIdMap);
        }
        return ret;
    }

    private void loopHandle(LinkedList<KapreEventFreeWheelHandleDo> taskLinkedList, List<KapreGameRecordDo> ret, GamePlayDo gamePlay, Map<Long, OperationEventConfigRecordDo> batchIdMap) {
        // 迭代替代递归
        while (!taskLinkedList.isEmpty()) {
            // 逐个处理
            KapreEventFreeWheelHandleDo task = taskLinkedList.pollFirst();
            KapreGameRecordDo parentGameRecord = task.getParentGameRecord();
            for (Long oldSubGameRecordId : task.getOldSubGameRecordIds()) {
                OperationEventConfigRecordDo configRecordDo = batchIdMap.get(oldSubGameRecordId);
                AssertUtil.assertNotNull(configRecordDo, ErrorCode.ERR_INTER, "configRecordDo is null of batchId " + oldSubGameRecordId);
                KapreGameRecordDo gameRecordDo = this.processGameRecord(gamePlay, super.generateGameRecordId(), parentGameRecord.getId(), configRecordDo);
                // 添加新的gameRecordId
                parentGameRecord.addGameRecordId(gameRecordDo.getId());
                ret.add(gameRecordDo);
                // 如果还有免费轮，则加入任务
                if (CollectionUtil.isNotEmpty(configRecordDo.getGameRecordIds())) {
                    taskLinkedList.addLast(new KapreEventFreeWheelHandleDo(gameRecordDo, configRecordDo.getGameRecordIds()));
                }
            }
        }
    }

    /**
     * 构建并处理投注记录
     */
    private KapreGameRecordDo processGameRecord(
            GamePlayDo gamePlay,
            long gameRecordId,
            long parentId,
            OperationEventConfigRecordDo recordDo) {

        // 确定 `TotalMultiple` 参数
        WheelDo<KapreSymbolDo> kapreSymbolWheel = this.convertWheel(recordDo.getWheel());
        KapreSymbolDo[][] symbolArr = kapreSymbolWheel.getSymbolArr();
        // 填充wild的嵌套元素:如果wild嵌套元素为空，则填充一个倍率为0的现金元素
        kapreSymbolWheel.setSymbolArr(KapreSymbolUtil.handleWildSymbolArrNest(symbolArr));
        RewardResultDo<KapreSymbolDo> rewardResult = kapreRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, kapreSymbolWheel, BigDecimal.ZERO));
        // 构建时还原
        kapreSymbolWheel.setSymbolArr(symbolArr);
        return buildGameRecord(gameRecordId, parentId, gamePlay.getBetRecordId(), kapreSymbolWheel, rewardResult);
    }

    private KapreGameRecordDo buildGameRecord(Long id,
                                              Long parentId,
                                              Long betRecordId,
                                              WheelDo<KapreSymbolDo> wheel,
                                              RewardResultDo<KapreSymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return KapreGameRecordDo.builder()
                .id(id)
                .parentId(parentId)
                .betRecordId(betRecordId)
                .batchId(id)
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


    @Data
    @SuperBuilder
    private static class KapreEventFreeWheelHandleDo {

        public KapreEventFreeWheelHandleDo(KapreGameRecordDo parentGameRecord, List<Long> oldSubGameRecordIds) {
            AssertUtil.assertNotNull(parentGameRecord, ErrorCode.ERR_INTER, "parentGameRecord is null");
            AssertUtil.assertTrue(CollectionUtil.isNotEmpty(oldSubGameRecordIds), ErrorCode.ERR_INTER, "oldSubGameRecordIds is empty");
            this.parentGameRecord = parentGameRecord;
            this.oldSubGameRecordIds = oldSubGameRecordIds;
        }

        // parentRecord
        private KapreGameRecordDo parentGameRecord;
        // 时间配置中的子gameRecordId列表
        private List<Long> oldSubGameRecordIds;
    }

    private WheelDo<KapreSymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<KapreSymbolDo>>() {
        });
    }
}