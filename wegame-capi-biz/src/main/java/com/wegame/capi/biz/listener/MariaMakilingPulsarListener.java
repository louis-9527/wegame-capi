package com.wegame.capi.biz.listener;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.executor.WsThreadPoolExecutor;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.ThreadMdcUtil;
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
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MariaMakilingPulsarListener extends AbstractPulsarListener implements Runnable {
    private static final GameTypeEnum gameType = GameTypeEnum.MARIA_MAKILING;
    private static final int corePoolSize = 1;
    private static final int maximumPoolSize = 1;
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
            if (message.getRedeliveryCount() > 0) {
                log.info("{}, processMessages, msgType:{}, msgId:{}, retryTimes: {}", getGameType().getName(), mqMessage.getType(), mqMessage.getMsgId(), message.getRedeliveryCount());
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


    public void batchAdd(List<BetRecordDo<MariaMakilingGameRecordDo>> recordList, Map<Long, MessageId> messageIdMap) {
        long startTime = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        try {
            boolean ret = mariaMakilingRecordManager.batchAdd(recordList, getGameType());
            if (ret) {
                getConsumer().acknowledge(messageIdMap.values().stream().collect(Collectors.toList()));
            }
        } catch (Exception e) {
            log.error("batchAdd error, errMsg: {}", e.getMessage(), e);
            add(recordList, messageIdMap);
        } finally {
            //            // help gc
            //            messageIdMap.clear();
            //            recordList = null;
        }
        long endTime = System.currentTimeMillis();
        log.info("batchAdd, size:{}, cost: {} ms", recordList.size(), endTime - startTime);
    }

    private void add(List<BetRecordDo<MariaMakilingGameRecordDo>> recordList, Map<Long, MessageId> messageIdMap) {
        for (BetRecordDo<MariaMakilingGameRecordDo> betRecordDo : CollectionUtils.emptyIfNull(recordList)) {
            try {
                mariaMakilingRecordManager.add(betRecordDo, getGameType());
                getConsumer().acknowledge(messageIdMap.get(betRecordDo.getId()));
            } catch (Exception e) {
                log.error("add error, betRecordId: {}, errMsg: {}", betRecordDo.getId(), e.getMessage(), e);
            }
        }
    }
}
