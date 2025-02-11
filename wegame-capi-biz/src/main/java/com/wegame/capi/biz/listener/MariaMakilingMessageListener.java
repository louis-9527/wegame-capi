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
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<Long, MessageId> messageIdMap = new HashMap<>(messages.size());
        messages.forEach(tt -> {
            String message = new String(tt.getData());
            MQMessage<BetRecordDo<MariaMakilingGameRecordDo>> betRecordDoMQMessage = JSON.parseObject(message, new TypeReference<MQMessage<BetRecordDo<MariaMakilingGameRecordDo>>>() {
            });
            BetRecordDo<MariaMakilingGameRecordDo> body = betRecordDoMQMessage.getBody();
            messageIdMap.put(body.getId(), tt.getMessageId());
            if (betRecordDoMQMessage.getType().equals(MessageTypeEnum.ADD.getCode())) {
                addRecordList.add(body);
            } else if (betRecordDoMQMessage.getType().equals(MessageTypeEnum.UPDATE.getCode())) {
                updateRecordList.add(body);
            }
        });
        log.info("beforeHandle messageIdMap: {}", messageIdMap);
        // 批量新增
        batchAdd(addRecordList, messageIdMap);
        // 批量修改
        batchUpdate(updateRecordList, messageIdMap);
        log.info("afterHandle messageIdMap: {}", messageIdMap);
        return new ArrayList<>(messageIdMap.values());
    }

    private void batchAdd(List<BetRecordDo<MariaMakilingGameRecordDo>> recordList, Map<Long, MessageId> messageIdMap) {
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        try {
            boolean ret = mariaMakilingRecordManager.batchAdd(recordList, GameTypeEnum.MARIA_MAKILING);
            // 移除处理失败的key
            if (!ret) {
                for (BetRecordDo record : recordList) {
                    messageIdMap.remove(record.getId());
                }
            }
        } catch (Exception e) {
            for (BetRecordDo record : recordList) {
                messageIdMap.remove(record.getId());
            }
            log.error("MariaMakilingMessageListener.batchAdd error: {}", e.getMessage(), e);
        }
    }

    private void batchUpdate(List<BetRecordDo> recordList, Map<Long, MessageId> messageIdMap) {
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        try {
            BetRecordBatchOptResultDo updateResultDo = betRecordService.batchUpdate(recordList, GameTypeEnum.MARIA_MAKILING);
            // 移除处理失败的key
            List<Long> failIdList = updateResultDo.getFailIdList();
            if (CollectionUtils.isNotEmpty(failIdList)) {
                for (Long id : failIdList) {
                    messageIdMap.remove(id);
                }
            }
        } catch (Exception e) {
            for (BetRecordDo record : recordList) {
                messageIdMap.remove(record.getId());
            }
            log.error("MariaMakilingMessageListener.batchUpdate error: {}", e.getMessage(), e);
        }
    }
}
