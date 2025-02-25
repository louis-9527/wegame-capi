package com.wegame.capi.biz.listener;

import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public abstract class AbstractPulsarListener {

    @Autowired
    BetRecordService betRecordService;

    public abstract Consumer<byte[]> getConsumer();

    public abstract GameTypeEnum getGameType();

    /**
     * 批量更新：
     * 优先批量更新，批量更新失败后单条逐个更新
     *
     * @param recordList
     * @param messageIdMap
     */
    public void batchUpdate(List<BetRecordDo> recordList, Map<Long, MessageId> messageIdMap) {
        long startTime = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        try {
            int sucCount = betRecordService.batchUpdate(recordList, getGameType());
            if (sucCount == recordList.size()) {
                getConsumer().acknowledge(messageIdMap.values().stream().collect(Collectors.toList()));
            } else {
                log.warn("{}, batchUpdate not all success", getGameType().getName());
                Map<Long, MessageId> failMessageIdMap = new HashMap<>();
                List<Long> nonExistIdList = betRecordService.listNotExistIdList(recordList.stream().map(BetRecordDo::getId).collect(Collectors.toList()), getGameType());
                if (CollectionUtils.isNotEmpty(nonExistIdList)) {
                    log.warn("{}, batchUpdate nonExistIdList: {}", getGameType().getName(), nonExistIdList);
                }
                Optional.ofNullable(nonExistIdList)
                        .orElse(Collections.emptyList())
                        .forEach(nonExistBetRecordId -> {
                            failMessageIdMap.put(nonExistBetRecordId, messageIdMap.get(nonExistBetRecordId));
                            messageIdMap.remove(nonExistBetRecordId);
                        });
                List<BetRecordDo> failRecordList = recordList.stream()
                        .filter(item -> nonExistIdList.contains(item.getId()))
                        .collect(Collectors.toList());
                if (MapUtils.isNotEmpty(messageIdMap)) {
                    getConsumer().acknowledge(messageIdMap.values().stream().collect(Collectors.toList()));
                }
                if (CollectionUtils.isNotEmpty(failRecordList) && MapUtils.isNotEmpty(failMessageIdMap)) {
                    Thread.currentThread().sleep(1000);
                    update(failRecordList, failMessageIdMap);
                }
            }
        } catch (Exception e) {
            log.error("{}, batchUpdate error, errMsg: {}", getGameType().getName(), e.getMessage(), e);
            update(recordList, messageIdMap);
        } finally {
            //            // help gc
            //            messageIdMap.clear();
            //            recordList = null;
        }
        long endTime = System.currentTimeMillis();
        long costTime = endTime - startTime;
        if (costTime >= 50) {
            log.info("{}, batchUpdate, size:{} cost: {} ms", getGameType().getName(), recordList.size(), costTime);
        }
    }

    private void update(List<BetRecordDo> recordList, Map<Long, MessageId> messageIdMap) {
        for (BetRecordDo<MariaMakilingGameRecordDo> betRecordDo : CollectionUtils.emptyIfNull(recordList)) {
            try {
                boolean ret = betRecordService.update(betRecordDo, getGameType());
                if (ret) {
                    getConsumer().acknowledge(messageIdMap.get(betRecordDo.getId()));
                } else {
                    log.error("{}, update fail, betRecordId: {}", getGameType().getName(), betRecordDo.getId());
                }
            } catch (Exception e) {
                log.error("{}, update error, betRecordId: {}, errMsg: {}", getGameType().getName(), betRecordDo.getId(), e.getMessage(), e);
            }
        }
    }
}
