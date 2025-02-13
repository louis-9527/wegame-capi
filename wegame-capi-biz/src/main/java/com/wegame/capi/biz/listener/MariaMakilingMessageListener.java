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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
public class MariaMakilingMessageListener extends AbstractPulsarListener {

    @Autowired
    BetRecordService betRecordService;
    @Autowired
    MariaMakilingRecordManager mariaMakilingRecordManager;
    @Autowired
    ThreadPoolExecutor betRecordThreadPoolExecutor;

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
            messageIdMap.put(betRecordDoMQMessage.getType() + "_" + body.getId(), tt.getMessageId());
            // todo delete log
            log.info("processMessages, msgType:{}, msgId:{}", betRecordDoMQMessage.getType(), betRecordDoMQMessage.getMsgId());
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
     * @param recordList
     * @param messageIdMap
     */
    private void batchAdd(List<BetRecordDo<MariaMakilingGameRecordDo>> recordList, Map<String, MessageId> messageIdMap) {
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
                    log.error("MariaMakilingMessageListener.batchAdd.add , betRecord: {}, errMsg: {}", betRecordDo, e.getMessage(), e);
                }
            }
        }
    }

    private void batchUpdate(List<BetRecordDo> recordList, Map<String, MessageId> messageIdMap) {
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        try {
            BetRecordBatchOptResultDo updateResultDo = betRecordService.batchUpdate(recordList, GameTypeEnum.MARIA_MAKILING);
            // 移除处理失败的key
            List<Long> failIdList = updateResultDo.getFailIdList();
            if (CollectionUtils.isNotEmpty(failIdList)) {
                for (Long id : failIdList) {
                    messageIdMap.remove(MessageTypeEnum.UPDATE.getCode() + "_" + id);
                }
            }
        } catch (Exception e) {
            for (BetRecordDo record : recordList) {
                messageIdMap.remove(MessageTypeEnum.UPDATE.getCode() + "_" + record.getId());
            }
            log.error("MariaMakilingMessageListener.batchUpdate error: {}", e.getMessage(), e);
        }
    }
}
