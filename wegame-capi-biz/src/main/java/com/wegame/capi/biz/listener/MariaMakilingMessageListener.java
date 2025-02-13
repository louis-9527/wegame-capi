package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordBatchOptResultDo;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.manager.MariaMakilingRecordManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MariaMakilingMessageListener extends AbstractPulsarListener {

    @Autowired
    BetRecordService betRecordService;
    @Autowired
    MariaMakilingRecordManager mariaMakilingRecordManager;

    @PostConstruct
    public void init() {
        initConsumer(GameTypeEnum.MARIA_MAKILING);
    }

    @Override
    protected List<MessageId> processMessages(Messages<byte[]> messages) {
        List<BetRecordDo<MariaMakilingGameRecordDo>> addRecordList = new ArrayList<>();
        List<BetRecordDo> updateRecordList = new ArrayList<>();
        Map<String, MessageId> messageIdMap = new HashMap<>(messages.size());
        messages.forEach(tt -> {
            String message = new String(tt.getData());
            MQMessage<BetRecordDo<MariaMakilingGameRecordDo>> betRecordDoMQMessage = JSON.parseObject(message, new TypeReference<MQMessage<BetRecordDo<MariaMakilingGameRecordDo>>>() {
            });
            BetRecordDo<MariaMakilingGameRecordDo> body = betRecordDoMQMessage.getBody();
            // todo delete log
            log.info("processMessages, msgType:{}, msgId:{}", betRecordDoMQMessage.getType(), betRecordDoMQMessage.getMsgId());
            if (tt.getRedeliveryCount() > 0 && StringUtils.equals(betRecordDoMQMessage.getType(), MessageTypeEnum.ADD.getCode())) {
                log.info("processMessages, msgType:{}, msgId:{}, retryTimes: {}", betRecordDoMQMessage.getType(), betRecordDoMQMessage.getMsgId(), tt.getRedeliveryCount());
            }
            messageIdMap.put(betRecordDoMQMessage.getType() + "_" + body.getId(), tt.getMessageId());
            if (betRecordDoMQMessage.getType().equals(MessageTypeEnum.ADD.getCode())) {
                addRecordList.add(body);
            } else if (betRecordDoMQMessage.getType().equals(MessageTypeEnum.UPDATE.getCode())) {
                updateRecordList.add(body);
            }
        });
        // 批量新增
        batchAdd(addRecordList, messageIdMap);
        // 批量修改
        batchUpdate(updateRecordList, messageIdMap);
        //
        return new ArrayList<>(messageIdMap.values());
    }

    /**
     * 优先批量处理，批量处理失败后改为单条处理
     *
     * @param recordList
     * @param messageIdMap
     */
    private void batchAdd(List<BetRecordDo<MariaMakilingGameRecordDo>> recordList, Map<String, MessageId> messageIdMap) {
        long startTime = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        try {
            boolean ret = mariaMakilingRecordManager.batchAdd(recordList, GameTypeEnum.MARIA_MAKILING);
            // 移除处理失败的key
            if (!ret) {
                for (BetRecordDo record : recordList) {
                    messageIdMap.remove(MessageTypeEnum.ADD.getCode() + "_" + record.getId());
                }
            }
        } catch (Exception e) {
            log.error("MariaMakilingMessageListener.batchAdd err, errMsg: {}", e.getMessage(), e);
            for (BetRecordDo<MariaMakilingGameRecordDo> betRecordDo : CollectionUtils.emptyIfNull(recordList)) {
                try {
                    mariaMakilingRecordManager.add(betRecordDo, GameTypeEnum.MARIA_MAKILING);
                } catch (Exception ep) {
                    messageIdMap.remove(MessageTypeEnum.ADD.getCode() + "_" + betRecordDo.getId());
                    log.error("MariaMakilingMessageListener.batchAdd.add , betRecordId: {}, errMsg: {}", betRecordDo.getId(), ep.getMessage(), ep);
                }
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("MariaMakilingMessageListener#batchAdd, cost: {} ms", endTime - startTime);
    }

    /**
     * 批量更新：
     * 优先批量更新，批量更新失败后单挑更新
     * @param recordList
     * @param messageIdMap
     */
    private void batchUpdate(List<BetRecordDo> recordList, Map<String, MessageId> messageIdMap) {
        long startTime = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        try {
            boolean ret = betRecordService.batchUpdate(recordList, GameTypeEnum.MARIA_MAKILING);
            // 移除处理失败的key
            if (!ret) {
                for (BetRecordDo record : recordList) {
                    messageIdMap.remove(MessageTypeEnum.UPDATE.getCode() + "_" + record.getId());
                }
            }
            List<Long> nonExistIdList = betRecordService.listNotExistIdList(recordList.stream().map(BetRecordDo::getId).collect(Collectors.toList()), GameTypeEnum.MARIA_MAKILING);
            Optional.ofNullable(nonExistIdList)
                    .orElse(Collections.emptyList())
                    .forEach(id -> messageIdMap.remove(MessageTypeEnum.UPDATE.getCode() + "_" + id));
        } catch (Exception e) {
            log.error("MariaMakilingMessageListener.batchUpdate err, errMsg: {}", e.getMessage(), e);
            for (BetRecordDo<MariaMakilingGameRecordDo> betRecordDo : CollectionUtils.emptyIfNull(recordList)) {
                try {
                    boolean ret = betRecordService.update(betRecordDo, GameTypeEnum.MARIA_MAKILING);
                    if (!ret) {
                        messageIdMap.remove(MessageTypeEnum.UPDATE.getCode() + "_" + betRecordDo.getId());
                    }
                } catch (Exception ep) {
                    messageIdMap.remove(MessageTypeEnum.UPDATE.getCode() + "_" + betRecordDo.getId());
                    log.error("MariaMakilingMessageListener.batchUpdate.update , betRecordId: {}, errMsg: {}", betRecordDo.getId(), ep.getMessage(), ep);
                }
            }
            long endTime = System.currentTimeMillis();
            log.info("MariaMakilingMessageListener#batchUpdate, cost: {} ms", endTime - startTime);
        }
    }
}
