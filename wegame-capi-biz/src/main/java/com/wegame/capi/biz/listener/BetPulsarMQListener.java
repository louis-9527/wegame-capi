//package com.wegame.capi.biz.listener;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.TypeReference;
//import com.wegame.base.enums.ErrorCode;
//import com.wegame.base.exception.BusinessException;
//import com.wegame.base.rocketmq.MQMessage;
//import com.wegame.capi.biz.domain.biz.BetRecordBatchResultDo;
//import com.wegame.capi.biz.domain.biz.BetRecordDo;
//import com.wegame.capi.biz.service.BetRecordService;
//import com.wegame.capi.client.enums.BetOperationTypeEnum;
//import com.wegame.capi.client.enums.GameTypeEnum;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections4.CollectionUtils;
//import org.apache.pulsar.client.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Component
//public class BetPulsarMQListener {
//    @Value("${pulsar.tenant}")
//    private String tenant;
//
//    @Value("${pulsar.namespaces}")
//    private String namespaces;
//
//    @Value("${pulsar.mariamakiling-topic}")
//    private String topic;
//
//    @Autowired
//    private BetRecordService betRecordService;
//
//    @Autowired
//    private PulsarClient pulsarClient;
//    private Consumer<byte[]> consumer;
//
//    @PostConstruct
//    public void init() throws PulsarClientException {
//        String fullTopic = String.format("persistent://%s/%s/%s", tenant, namespaces, topic);
//        consumer = pulsarClient.newConsumer()
//                .topic(fullTopic)
//                .subscriptionName(GameTypeEnum.MARIA_MAKILING.getName())
//                .subscriptionType(SubscriptionType.Key_Shared) // 关键点：使用 Key_Shared 模式
//                .receiverQueueSize(200) // 增加内部缓存，提高吞吐量
//                .batchReceivePolicy(BatchReceivePolicy.builder()
//                        .maxNumMessages(100)  // 批量最多100条
//                        .timeout(500, java.util.concurrent.TimeUnit.MILLISECONDS)
//                        .build())
//                .subscribe();
//
//        new Thread(this::consumeMessages).start();
//    }
//
//    @PreDestroy
//    public void close() throws PulsarClientException {
//        if (consumer != null) {
//            consumer.close();
//        }
//    }
//
//    private void consumeMessages() {
//        while (true) {
//            try {
//                Messages<byte[]> messages = consumer.batchReceive();
//                if (messages.size() == 0) {
//                    continue;
//                }
//                List<MessageId> msgIds = this.execMessages(messages);
//                consumer.acknowledge(msgIds);
//            } catch (PulsarClientException e) {
//                log.error("consumeMessages error:", e);
//                throw new BusinessException(ErrorCode.ERR_INTER, "consumeMessages error");
//            }
//        }
//    }
//
//    /**
//     * 消息处理,返回处理成的msgId
//     *
//     * @param messages
//     * @return
//     */
//    private List<MessageId> execMessages(Messages<byte[]> messages) {
//        List<BetRecordDo> betAddRecordList = new ArrayList<>();
//        List<BetRecordDo> betUpdateRecordList = new ArrayList<>();
//
//        Map<Long, MessageId> messageIdMap = new HashMap<>(messages.size());
//        messages.forEach(tt -> {
//            String message = new String(tt.getData());
//            MQMessage<BetRecordDo> betRecordDoMQMessage = JSON.parseObject(message, new TypeReference<MQMessage<BetRecordDo>>() {
//            });
//            BetRecordDo body = betRecordDoMQMessage.getBody();
//            messageIdMap.put(body.getId(), tt.getMessageId());
//            if (betRecordDoMQMessage.getType().equals(BetOperationTypeEnum.ADD.getCode())) {
//                betAddRecordList.add(body);
//            } else if (betRecordDoMQMessage.getType().equals(BetOperationTypeEnum.UPDATE.getCode())) {
//                betUpdateRecordList.add(body);
//            }
//        });
//        // 批量新增
//        if (CollectionUtils.isNotEmpty(betAddRecordList)) {
//            try {
//                BetRecordBatchResultDo addResultDo = betRecordService.batchAdd(betAddRecordList, GameTypeEnum.MARIA_MAKILING);
//                // 移除处理失败的key
//                List<Long> normalFailIdList = addResultDo.getNormalFailIdList();
//                if (CollectionUtils.isNotEmpty(normalFailIdList)) {
//                    for (Long id : normalFailIdList) {
//                        messageIdMap.remove(id);
//                    }
//                }
//
//            } catch (Exception e) {
//                log.error("betRecordService.batchAdd error:", e);
//            }
//        }
//        // 批量修改
//        if (CollectionUtils.isNotEmpty(betUpdateRecordList)) {
//
//            try {
//                BetRecordBatchResultDo updateResultDo = betRecordService.batchUpdate(betUpdateRecordList, GameTypeEnum.MARIA_MAKILING);
//                // 移除处理失败的key
//                List<Long> normalFailIdList = updateResultDo.getNormalFailIdList();
//                if (CollectionUtils.isNotEmpty(normalFailIdList)) {
//                    for (Long id : normalFailIdList) {
//                        messageIdMap.remove(id);
//                    }
//                }
//            } catch (Exception e) {
//                log.error("betRecordService.batchUpdate error:", e);
//            }
//        }
//        return new ArrayList<>(messageIdMap.values());
//    }
//
//
//}
