package com.wegame.capi.biz.listener;

import cn.hutool.core.lang.UUID;
import com.wegame.base.executor.WsThreadPoolExecutor;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.biz.manager.MariaMakilingRecordManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Messages;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MariaMakilingPulsarListener extends AbstractPulsarListener implements Runnable {
    private static final GameTypeEnum gameType = GameTypeEnum.MARIA_MAKILING;
    private static final int corePoolSize = 10;
    private static final int maximumPoolSize = 10;
    private volatile boolean start = false;
    @Autowired
    BetRecordService betRecordService;
    @Autowired
    MariaMakilingRecordManager mariaMakilingRecordManager;
    @Autowired
    Consumer<byte[]> mariamakilingConsumer;
    ExecutorService executor;

    @PostConstruct
    public void init() {
        executor = new WsThreadPoolExecutor(corePoolSize,
                maximumPoolSize,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new CustomizableThreadFactory("mariamakiling-consumber-pool-"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        start = true;
        run();
    }

    @PreDestroy
    public void destroy() throws Exception {
        start = false;
        if (mariamakilingConsumer != null) {
            mariamakilingConsumer.close();
        }
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @Override
    public void run() {
        log.info("MariaMakilingPulsarListener started...");
        for (int i = 0; i < maximumPoolSize; i++) {
            executor.submit(() -> {
                while (start) {
                    try {
                        MDC.put(ThreadMdcUtil.TRACE_ID, UUID.randomUUID().toString(true));
                        Messages<byte[]> messages = mariamakilingConsumer.batchReceive();
                        if (messages == null || messages.size() == 0) {
                            Thread.currentThread().sleep(1000);
                            continue;
                        }
                        onMessages(messages);
                    } catch (Exception e) {
                        log.error("Error consuming messages, errMsg: {}", e.getMessage(), e);
                    } finally {
                        MDC.clear();
                    }
                }
            });
        }
        log.info("MariaMakilingPulsarListener stopped...");
    }

    @Override
    public Consumer<byte[]> getConsumer() {
        return mariamakilingConsumer;
    }

    @Override
    public GameTypeEnum getGameType() {
        return gameType;
    }

    //    private void onConsumer(Messages<byte[]> messages) {
    //        if (Objects.isNull(messages) || messages.size() == 0) {
    //            return;
    //        }
    //        log.info("onConsumer messages size: {}", messages.size());
    //        List<BetRecordDo<MariaMakilingGameRecordDo>> addRecordList = new ArrayList<>();
    //        List<BetRecordDo> updateRecordList = new ArrayList<>();
    //        Map<Long, MessageId> addMessageIdMap = new HashMap<>(messages.size());
    //        Map<Long, MessageId> updateMessageIdMap = new HashMap<>(messages.size());
    //        messages.forEach(message -> {
    //            MQMessage<BetRecordDo<MariaMakilingGameRecordDo>> mqMessage = JSON.parseObject(new String(message.getData()),
    //                    new TypeReference<MQMessage<BetRecordDo<MariaMakilingGameRecordDo>>>() {
    //                    });
    //            BetRecordDo<MariaMakilingGameRecordDo> body = mqMessage.getBody();
    //            if (message.getRedeliveryCount() > 0 && StringUtils.equals(mqMessage.getType(), MessageTypeEnum.ADD.getCode())) {
    //                log.info("processMessages, msgType:{}, msgId:{}, retryTimes: {}", mqMessage.getType(), mqMessage.getMsgId(), message.getRedeliveryCount());
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
    //
    //
    //    private void batchAdd(List<BetRecordDo<MariaMakilingGameRecordDo>> recordList, Map<Long, MessageId> messageIdMap) {
    //        long startTime = System.currentTimeMillis();
    //        if (CollectionUtils.isEmpty(recordList)) {
    //            return;
    //        }
    //        try {
    //            boolean ret = mariaMakilingRecordManager.batchAdd(recordList, gameType);
    //            if (ret) {
    //                mariamakilingConsumer.acknowledge(messageIdMap.values().stream().collect(Collectors.toList()));
    //            }
    //        } catch (Exception e) {
    //            log.error("MariaMakilingMessageListener.batchAdd err, errMsg: {}", e.getMessage(), e);
    //            for (BetRecordDo<MariaMakilingGameRecordDo> betRecordDo : CollectionUtils.emptyIfNull(recordList)) {
    //                try {
    //                    mariaMakilingRecordManager.add(betRecordDo, gameType);
    //                    mariamakilingConsumer.acknowledge(messageIdMap.get(betRecordDo.getId()));
    //                    mariamakilingConsumer.acknowledge(messageIdMap.get(betRecordDo.getId()));
    //                } catch (Exception ep) {
    //                    messageIdMap.remove(MessageTypeEnum.ADD.getCode() + "_" + betRecordDo.getId());
    //                    log.error("MariaMakilingMessageListener.batchAdd.add , betRecordId: {}, errMsg: {}", betRecordDo.getId(), ep.getMessage(), ep);
    //                }
    //            }
    //        }
    //        long endTime = System.currentTimeMillis();
    //        log.info("MariaMakilingMessageListener#batchAdd, size:{}, cost: {} ms", recordList.size(), endTime - startTime);
    //    }
    //
    //    /**
    //     * 批量更新：
    //     * 优先批量更新，批量更新失败后单挑更新
    //     *
    //     * @param recordList
    //     * @param messageIdMap
    //     */
    //    private void batchUpdate(List<BetRecordDo> recordList, Map<Long, MessageId> messageIdMap) {
    //        long startTime = System.currentTimeMillis();
    //        if (CollectionUtils.isEmpty(recordList)) {
    //            return;
    //        }
    //        try {
    //            boolean ret = betRecordService.batchUpdate(recordList, gameType);
    //            if (ret) {
    //                mariamakilingConsumer.acknowledge(messageIdMap.values().stream().collect(Collectors.toList()));
    //            }
    //            // todo fix
    //            //            List<Long> nonExistIdList = betRecordService.listNotExistIdList(recordList.stream().map(BetRecordDo::getId).collect(Collectors.toList()), gameType);
    //            //            Optional.ofNullable(nonExistIdList)
    //            //                    .orElse(Collections.emptyList())
    //            //                    .forEach(id -> messageIdMap.remove(MessageTypeEnum.UPDATE.getCode() + "_" + id));
    //        } catch (Exception e) {
    //            log.error("MariaMakilingMessageListener.batchUpdate err, errMsg: {}", e.getMessage(), e);
    //            for (BetRecordDo<MariaMakilingGameRecordDo> betRecordDo : CollectionUtils.emptyIfNull(recordList)) {
    //                try {
    //                    boolean ret = betRecordService.update(betRecordDo, gameType);
    //                    if (ret) {
    //                        mariamakilingConsumer.acknowledge(messageIdMap.get(betRecordDo.getId()));
    //                    }
    //                } catch (Exception ep) {
    //                    log.error("MariaMakilingMessageListener.batchUpdate.update , betRecordId: {}, errMsg: {}", betRecordDo.getId(), ep.getMessage(), ep);
    //                }
    //            }
    //        }
    //        long endTime = System.currentTimeMillis();
    //        log.info("MariaMakilingMessageListener#batchUpdate, size:{} cost: {} ms", recordList.size(), endTime - startTime);
    //    }
}
