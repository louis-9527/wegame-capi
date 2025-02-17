package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.manager.MariaMakilingRecordManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public abstract class AbstractPulsarListener {

    @Autowired
    BetRecordService betRecordService;
    @Autowired
    MariaMakilingRecordManager mariaMakilingRecordManager;

    public abstract Consumer<byte[]> getConsumer();

    public abstract GameTypeEnum getGameType();

    public void onMessages(Messages<byte[]> messages) {
        if (Objects.isNull(messages) || messages.size() == 0) {
            return;
        }
        log.info("onConsumer messages size: {}", messages.size());
        List<BetRecordDo<MariaMakilingGameRecordDo>> addRecordList = new ArrayList<>();
        List<BetRecordDo> updateRecordList = new ArrayList<>();
        Map<Long, MessageId> addMessageIdMap = new HashMap<>(messages.size());
        Map<Long, MessageId> updateMessageIdMap = new HashMap<>(messages.size());
        messages.forEach(message -> {
            MQMessage<BetRecordDo<MariaMakilingGameRecordDo>> mqMessage = JSON.parseObject(new String(message.getData()),
                    new TypeReference<MQMessage<BetRecordDo<MariaMakilingGameRecordDo>>>() {
                    });
            BetRecordDo<MariaMakilingGameRecordDo> betRecord = mqMessage.getBody();
            if (message.getRedeliveryCount() > 0 && StringUtils.equals(mqMessage.getType(), MessageTypeEnum.ADD.getCode())) {
                log.info("processMessages, msgType:{}, msgId:{}, retryTimes: {}", mqMessage.getType(), mqMessage.getMsgId(), message.getRedeliveryCount());
            }
            if (mqMessage.getType().equals(MessageTypeEnum.ADD.getCode())) {
                addMessageIdMap.put(betRecord.getId(), message.getMessageId());
                addRecordList.add(betRecord);
            } else if (mqMessage.getType().equals(MessageTypeEnum.UPDATE.getCode())) {
                updateMessageIdMap.put(betRecord.getId(), message.getMessageId());
                updateRecordList.add(betRecord);
            }
        });
        // 批量新增
        batchAdd(addRecordList, addMessageIdMap);
        // 批量修改
        batchUpdate(updateRecordList, updateMessageIdMap);
    }

    private void batchAdd(List<BetRecordDo<MariaMakilingGameRecordDo>> recordList, Map<Long, MessageId> messageIdMap) {
        long startTime = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        try {
            boolean ret = mariaMakilingRecordManager.batchAdd(recordList, getGameType());
            if (ret) {
                getConsumer().acknowledge(messageIdMap.values().stream().collect(Collectors.toList()));
            }
            // help gc
            messageIdMap.clear();
        } catch (Exception e) {
            log.error("MariaMakilingMessageListener.batchAdd err, errMsg: {}", e.getMessage(), e);
            for (BetRecordDo<MariaMakilingGameRecordDo> betRecordDo : CollectionUtils.emptyIfNull(recordList)) {
                try {
                    mariaMakilingRecordManager.add(betRecordDo, getGameType());
                    getConsumer().acknowledge(messageIdMap.get(betRecordDo.getId()));
                    getConsumer().acknowledge(messageIdMap.get(betRecordDo.getId()));
                } catch (Exception ep) {
                    messageIdMap.remove(MessageTypeEnum.ADD.getCode() + "_" + betRecordDo.getId());
                    log.error("MariaMakilingMessageListener.batchAdd.add , betRecordId: {}, errMsg: {}", betRecordDo.getId(), ep.getMessage(), ep);
                }
            }
        } finally {
            // help gc
            messageIdMap.clear();
            recordList = null;
        }
        long endTime = System.currentTimeMillis();
        log.info("MariaMakilingMessageListener#batchAdd, size:{}, cost: {} ms", recordList.size(), endTime - startTime);
    }

    /**
     * 批量更新：
     * 优先批量更新，批量更新失败后单挑更新
     *
     * @param recordList
     * @param messageIdMap
     */
    private void batchUpdate(List<BetRecordDo> recordList, Map<Long, MessageId> messageIdMap) {
        long startTime = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        try {
            boolean ret = betRecordService.batchUpdate(recordList, getGameType());
            if (ret) {
                getConsumer().acknowledge(messageIdMap.values().stream().collect(Collectors.toList()));
                List<Long> nonExistIdList = betRecordService.listNotExistIdList(recordList.stream().map(BetRecordDo::getId).collect(Collectors.toList()), getGameType());
                if (CollectionUtils.isEmpty(nonExistIdList)) {
                    log.warn("batchUpdate nonExistIdList: {}", nonExistIdList);
                }
                Optional.ofNullable(nonExistIdList)
                        .orElse(Collections.emptyList())
                        .forEach(betRecordId -> messageIdMap.remove(betRecordId));
                getConsumer().acknowledge(messageIdMap.values().stream().collect(Collectors.toList()));
            }
        } catch (Exception e) {
            log.error("MariaMakilingMessageListener.batchUpdate err, errMsg: {}", e.getMessage(), e);
            for (BetRecordDo<MariaMakilingGameRecordDo> betRecordDo : CollectionUtils.emptyIfNull(recordList)) {
                try {
                    boolean ret = betRecordService.update(betRecordDo, getGameType());
                    if (ret) {
                        getConsumer().acknowledge(messageIdMap.get(betRecordDo.getId()));
                    }
                } catch (Exception ep) {
                    log.error("MariaMakilingMessageListener.batchUpdate.update , betRecordId: {}, errMsg: {}", betRecordDo.getId(), ep.getMessage(), ep);
                }
            }
        } finally {
            // help gc
            messageIdMap.clear();
            recordList = null;
        }
        long endTime = System.currentTimeMillis();
        log.info("MariaMakilingMessageListener#batchUpdate, size:{} cost: {} ms", recordList.size(), endTime - startTime);
    }
}
