package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.mq.MQMessage;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.manager.AbstractRecordManager;
import com.wegame.capi.biz.manager.MalakasRecordManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MalakasPulsarListener
        extends AbstractPulsarListener<MalakasGameRecordDo> {
    private static final String LISTENER_NAME = "MariaMakilingPulsarListener";
    private static final String POOL_NAME = "mariamakiling-consumber-pool-";
    private static final GameTypeEnum gameType = GameTypeEnum.MALAKAS;
    private static final int corePoolSize = 1;
    private static final int maximumPoolSize = 1;
    private volatile boolean start = false;
    @Autowired
    BetRecordService betRecordService;
    @Autowired
    MalakasRecordManager malakasRecordManager;
    @Autowired
    Consumer<byte[]> malakasConsumer;

    @Override
    public String getListenerName() {
        return LISTENER_NAME;
    }

    @Override
    public String getPoolName() {
        return POOL_NAME;
    }

    @Override
    public Consumer<byte[]> getConsumer() {
        return malakasConsumer;
    }

    @Override
    public GameTypeEnum getGameType() {
        return gameType;
    }

    @Override
    public TypeReference<MQMessage<BetRecordDo<MalakasGameRecordDo>>> getTypeReference() {
        return new TypeReference<MQMessage<BetRecordDo<MalakasGameRecordDo>>>() {
        };
    }

    @Override
    public AbstractRecordManager<MalakasGameRecordDo> getRecordManager() {
        return malakasRecordManager;
    }

    @Override
    public int getCorePoolSize() {
        return corePoolSize;
    }

    @Override
    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    //    @Override
    //    public Consumer<byte[]> getConsumer() {
    //        return malakasConsumer;
    //    }
    //
    //    @Override
    //    public GameTypeEnum getGameType() {
    //        return gameType;
    //    }
    //
    //    @PostConstruct
    //    public void init() {
    //        executor = new WsThreadPoolExecutor(corePoolSize,
    //                maximumPoolSize,
    //                3,
    //                TimeUnit.MINUTES,
    //                new ArrayBlockingQueue<>(1000),
    //                new CustomizableThreadFactory("malakas-consumber-pool-"),
    //                new ThreadPoolExecutor.CallerRunsPolicy());
    //        start = true;
    //        run();
    //    }
    //
    //    @PreDestroy
    //    public void destroy() throws Exception {
    //        start = false;
    //        if (malakasConsumer != null) {
    //            malakasConsumer.close();
    //        }
    //        if (executor != null) {
    //            executor.shutdownNow();
    //        }
    //    }
    //
    //    @Override
    //    public void run() {
    //        log.info("MalakasPulsarListener start...");
    //        for (int i = 0; i < maximumPoolSize; i++) {
    //            executor.submit(() -> {
    //                while (start) {
    //                    try {
    //                        MDC.put(ThreadMdcUtil.TRACE_ID, UUID.randomUUID().toString(true));
    //                        Messages<byte[]> messages = malakasConsumer.batchReceive();
    //                        if (messages == null || messages.size() == 0) {
    //                            continue;
    //                        }
    //                        onConsumer(messages);
    //                    } catch (Exception e) {
    //                        log.error("Error consuming messages, errMsg: {}", e.getMessage(), e);
    //                    } finally {
    //                        MDC.clear();
    //                    }
    //                }
    //            });
    //        }
    //        log.info("MalakasPulsarListener end...");
    //    }
    //
    //    private void onConsumer(Messages<byte[]> messages) {
    //        if (Objects.isNull(messages) || messages.size() == 0) {
    //            return;
    //        }
    //        log.info("MalakasPulsarListener onConsumer messages size: {}", messages.size());
    //        List<BetRecordDo<MalakasGameRecordDo>> addRecordList = new ArrayList<>();
    //        List<BetRecordDo> updateRecordList = new ArrayList<>();
    //        Map<Long, MessageId> addMessageIdMap = new HashMap<>(messages.size());
    //        Map<Long, MessageId> updateMessageIdMap = new HashMap<>(messages.size());
    //        messages.forEach(message -> {
    //            MQMessage<BetRecordDo<MalakasGameRecordDo>> mqMessage = JSON.parseObject(new String(message.getData()),
    //                    new TypeReference<MQMessage<BetRecordDo<MalakasGameRecordDo>>>() {
    //                    });
    //            BetRecordDo<MalakasGameRecordDo> body = mqMessage.getBody();
    //            if (message.getRedeliveryCount() > 0) {
    //                log.info("processMessages, msgType:{}, msgId:{}, retryTimes: {}",
    //                        mqMessage.getType(),
    //                        mqMessage.getMsgId(),
    //                        message.getRedeliveryCount());
    //            }
    //            if (mqMessage.getType().equals(MessageTypeEnum.ADD.getCode())) {
    //                addMessageIdMap.put(body.getId(), message.getMessageId());
    //                addRecordList.add(body);
    //            } else if (mqMessage.getType().equals(MessageTypeEnum.UPDATE.getCode())) {
    //                updateMessageIdMap.put(body.getId(), message.getMessageId());
    //                updateRecordList.add(body);
    //            }
    //        });
    //        // 批量新增
    //        batchAdd(addRecordList, addMessageIdMap);
    //        // 批量修改
    //        batchUpdate(updateRecordList, updateMessageIdMap);
    //    }
    //
    //    public void batchAdd(List<BetRecordDo<MalakasGameRecordDo>> recordList, Map<Long, MessageId> messageIdMap) {
    //        long startTime = System.currentTimeMillis();
    //        if (CollectionUtils.isEmpty(recordList)) {
    //            return;
    //        }
    //        try {
    //            boolean ret = malakasRecordManager.batchAdd(recordList, getGameType());
    //            if (ret) {
    //                getConsumer().acknowledge(messageIdMap.values().stream().collect(Collectors.toList()));
    //            }
    //        } catch (Exception e) {
    //            log.error("batchAdd error, errMsg: {}", e.getMessage(), e);
    //            add(recordList, messageIdMap);
    //        } finally {
    //            //            // help gc
    //            //            messageIdMap.clear();
    //            //            recordList = null;
    //        }
    //        long endTime = System.currentTimeMillis();
    //        log.info("batchAdd, size:{}, cost: {} ms", recordList.size(), endTime - startTime);
    //    }
    //
    //    private void add(List<BetRecordDo<MalakasGameRecordDo>> recordList, Map<Long, MessageId> messageIdMap) {
    //        for (BetRecordDo<MalakasGameRecordDo> betRecordDo : CollectionUtils.emptyIfNull(recordList)) {
    //            try {
    //                malakasRecordManager.add(betRecordDo, getGameType());
    //                getConsumer().acknowledge(messageIdMap.get(betRecordDo.getId()));
    //            } catch (Exception e) {
    //                log.error("add error, betRecordId: {}, errMsg: {}", betRecordDo.getId(), e.getMessage(), e);
    //            }
    //        }
    //    }
}
