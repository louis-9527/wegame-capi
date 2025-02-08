package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordBatchOptResultDo;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.MessageTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.pulsar.client.api.*;
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
    private BetRecordService betRecordService;

    @Override
    protected List<MessageId> processMessages(Messages<byte[]> messages) {
        List<BetRecordDo> betAddRecordList = new ArrayList<>();
        List<BetRecordDo> betUpdateRecordList = new ArrayList<>();
        Map<Long, MessageId> messageIdMap = new HashMap<>(messages.size());
        messages.forEach(tt -> {
            String message = new String(tt.getData());
            MQMessage<BetRecordDo> betRecordDoMQMessage = JSON.parseObject(message, new TypeReference<MQMessage<BetRecordDo>>() {
            });
            BetRecordDo body = betRecordDoMQMessage.getBody();
            messageIdMap.put(body.getId(), tt.getMessageId());
            if (betRecordDoMQMessage.getType().equals(MessageTypeEnum.ADD.getCode())) {
                betAddRecordList.add(body);
            } else if (betRecordDoMQMessage.getType().equals(MessageTypeEnum.UPDATE.getCode())) {
                betUpdateRecordList.add(body);
            }
        });
        // 批量新增
        if (CollectionUtils.isNotEmpty(betAddRecordList)) {
            try {
                BetRecordBatchOptResultDo addResultDo = betRecordService.batchAdd(betAddRecordList, GameTypeEnum.MARIA_MAKILING);
                // 移除处理失败的key
                List<Long> failIdList = addResultDo.getFailIdList();
                if (CollectionUtils.isNotEmpty(failIdList)) {
                    for (Long id : failIdList) {
                        messageIdMap.remove(id);
                    }
                }
            } catch (Exception e) {
                log.error("betRecordService.batchAdd error:", e);
                for (BetRecordDo recordDo : betAddRecordList) {
                    messageIdMap.remove(recordDo.getId());
                }
            }
        }
        // 批量修改
        if (CollectionUtils.isNotEmpty(betUpdateRecordList)) {
            try {
                BetRecordBatchOptResultDo updateResultDo = betRecordService.batchUpdate(betUpdateRecordList, GameTypeEnum.MARIA_MAKILING);
                // 移除处理失败的key
                List<Long> failIdList = updateResultDo.getFailIdList();
                if (CollectionUtils.isNotEmpty(failIdList)) {
                    for (Long id : failIdList) {
                        messageIdMap.remove(id);
                    }
                }
            } catch (Exception e) {
                log.error("betRecordService.batchUpdate error:", e);
                for (BetRecordDo recordDo : betUpdateRecordList) {
                    messageIdMap.remove(recordDo.getId());
                }
            }
        }
        return new ArrayList<>(messageIdMap.values());
    }

    @PostConstruct
    public void init() {
        initConsumer(GameTypeEnum.MARIA_MAKILING);
    }
}
