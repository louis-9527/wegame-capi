package com.wegame.capi.biz.listener;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.executor.WsThreadPoolExecutor;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.manager.AbstractRecordManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
//@Component
public abstract class AbstractPulsarListener<T extends ToVoConverter> implements Runnable {
    private volatile boolean start = false;
    @Autowired
    BetRecordService betRecordService;
    ExecutorService executor;

    public abstract String getListenerName();

    public abstract String getPoolName();

    public abstract Consumer<byte[]> getConsumer();

    public abstract GameTypeEnum getGameType();

    public abstract TypeReference<MQMessage<BetRecordDo<T>>> getTypeReference();

    public abstract AbstractRecordManager<T> getRecordManager();

    public abstract int getCorePoolSize();

    public abstract int getMaximumPoolSize();

    @PostConstruct
    public void init() {
        executor = new WsThreadPoolExecutor(getCorePoolSize(),
                getMaximumPoolSize(),
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new CustomizableThreadFactory(this.getPoolName()),
                new ThreadPoolExecutor.CallerRunsPolicy());
        start = true;
        run();
    }

    @PreDestroy
    public void destroy() throws Exception {
        start = false;
        if (getConsumer() != null) {
            getConsumer().close();
        }
        if (executor != null) {
            try {
                executor.shutdown();
                boolean stopFlag = executor.awaitTermination(20, TimeUnit.SECONDS);
                if (stopFlag) {
                    log.info("{} executor graceful stop success ,{},{}", this.getListenerName(), executor, stopFlag);
                } else {
                    log.info("{} executor graceful stop fail after 20 seconds,force stop it,{},{}", this.getListenerName(), executor, stopFlag);
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                log.info("{} executorService graceful stop exception,force stop it,{}", this.getListenerName(), executor);
                executor.shutdownNow();
            }
        }
    }


    @Override
    public void run() {
        log.info("{} started...", this.getListenerName());
        for (int i = 0; i < getMaximumPoolSize(); i++) {
            executor.submit(() -> {
                while (start) {
                    try {
                        MDC.put(ThreadMdcUtil.TRACE_ID, UUID.randomUUID().toString(true));
                        Messages<byte[]> messages = getConsumer().batchReceive();
                        if (messages == null || messages.size() == 0) {
                            Thread.currentThread().sleep(1000);
                            continue;
                        }
                        onMessages(messages);
                    } catch (Exception e) {
                        log.error("{} Error consuming messages, errMsg: {}", getListenerName(), e.getMessage(), e);
                    } finally {
                        MDC.clear();
                    }
                }
            });
        }
        log.info("{} stopped...", this.getListenerName());
    }


    public void onMessages(Messages<byte[]> messages) {
        if (Objects.isNull(messages) || messages.size() == 0) {
            return;
        }
        log.info("{} onConsumer messages size: {}", getGameType().getName(), messages.size());
        List<BetRecordDo<T>> addRecordList = new ArrayList<>();
        List<BetRecordDo> updateRecordList = new ArrayList<>();
        Map<Long, MessageId> addMessageIdMap = new HashMap<>(messages.size());
        Map<Long, MessageId> updateMessageIdMap = new HashMap<>(messages.size());
        messages.forEach(message -> {
            MQMessage<BetRecordDo<T>> mqMessage = JSON.parseObject(new String(message.getData()),
                    this.getTypeReference());
            BetRecordDo<T> betRecord = mqMessage.getBody();
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


    public void batchAdd(List<BetRecordDo<T>> recordList, Map<Long, MessageId> messageIdMap) {
        long startTime = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        try {
            boolean ret = getRecordManager().batchAdd(recordList, getGameType());
            if (ret) {
                getConsumer().acknowledge(messageIdMap.values().stream().collect(Collectors.toList()));
            }
        } catch (Exception e) {
            log.error("{} batchAdd error, errMsg: {}", getGameType().getName(), e.getMessage(), e);
            add(recordList, messageIdMap);
        } finally {
            //            // help gc
            //            messageIdMap.clear();
            //            recordList = null;
        }
        long endTime = System.currentTimeMillis();
        log.info("{} batchAdd, size:{}, cost: {} ms", getGameType().getName(), recordList.size(), endTime - startTime);
    }


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
        log.info("{}, batchUpdate, size:{} cost: {} ms", getGameType().getName(), recordList.size(), endTime - startTime);
    }

    private void add(List<BetRecordDo<T>> recordList, Map<Long, MessageId> messageIdMap) {
        for (BetRecordDo<T> betRecordDo : CollectionUtils.emptyIfNull(recordList)) {
            try {
                getRecordManager().add(betRecordDo, getGameType());
                getConsumer().acknowledge(messageIdMap.get(betRecordDo.getId()));
            } catch (Exception e) {
                log.error("{} add error, betRecordId: {}, errMsg: {}", getGameType().getName(), betRecordDo.getId(), e.getMessage(), e);
            }
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
