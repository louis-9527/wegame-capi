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
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Messages;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractPulsarListener<T extends ToVoConverter> implements Runnable {
    private volatile boolean start = false;
    @Autowired
    BetRecordService betRecordService;
    ExecutorService executor;

    public abstract Consumer<byte[]> getConsumer();

    public abstract GameTypeEnum getGameType();

    public abstract TypeReference<MQMessage<BetRecordDo<T>>> getTypeReference();

    public abstract AbstractBetRecordManager<T> betRecordManager();

    public abstract int getCorePoolSize();

    public abstract int getMaximumPoolSize();

    @PostConstruct
    public void init() {
        CustomizableThreadFactory threadFactory = new CustomizableThreadFactory(getGameType().getName() + "-consumber-pool-");
        threadFactory.setDaemon(true);
        executor = new WsThreadPoolExecutor(getCorePoolSize(),
                getMaximumPoolSize(),
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());
        start = true;
        run();
    }

    @PreDestroy
    public void destroy() throws Exception {
        log.info("start destroy pulsar listener");
        start = false;
        if (executor != null) {
            log.info("start closing executor: {}", executor);
            executor.shutdown();
            while (!executor.isTerminated()) {
                // 等待线程池中所有任务完成
                Thread.currentThread().sleep(1000);
            }
            log.info("after closing executor: {}", executor);
        }
        if (getConsumer() != null) {
            getConsumer().close();
        }
        log.info("finish destroy pulsar listener");
    }

    @Override
    public void run() {
        log.info("{} started...", getClass().getSimpleName());
        for (int i = 0; i < getMaximumPoolSize(); i++) {
            executor.submit(() -> {
                while (start) {
                    try {
                        MDC.put(ThreadMdcUtil.TRACE_ID, UUID.randomUUID().toString(true));
                        Messages<byte[]> messages = getConsumer().batchReceive();
                        if (messages == null || messages.size() == 0) {
                            log.warn("not received any messages, sleep one second");
                            Thread.currentThread().sleep(2000);
                            continue;
                        }
                        onMessages(messages);
                    } catch (Throwable e) {
                        log.error("Error consuming messages", e);
                    } finally {
                        MDC.clear();
                    }
                }
            });
        }
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
                log.info("{}, processMessages, msgType:{}, msgId:{}, retryTimes: {}, messageId: {}", getGameType().getName(), mqMessage.getType(), mqMessage.getMsgId(), message.getRedeliveryCount(), message.getMessageId());
            }
            if (mqMessage.getType().equals(MessageTypeEnum.ADD.getCode())) {
                // 避免一批次消息里面有重复消息
                if (!addMessageIdMap.containsKey(betRecord.getId())) {
                    addMessageIdMap.put(betRecord.getId(), message.getMessageId());
                    addRecordList.add(betRecord);
                }
            } else if (mqMessage.getType().equals(MessageTypeEnum.UPDATE.getCode())) {
                // 避免一批次消息里面有重复消息
                if (!updateMessageIdMap.containsKey(betRecord.getId())) {
                    updateMessageIdMap.put(betRecord.getId(), message.getMessageId());
                    updateRecordList.add(betRecord);
                }
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
            boolean ret = betRecordManager().batchAdd(recordList, getGameType());
            if (ret) {
                getConsumer().acknowledge(messageIdMap.values().stream().collect(Collectors.toList()));
            }
        } catch (Exception e) {
            log.error("{} batchAdd error", getGameType().getName(), e);
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
                update(recordList, messageIdMap);
                //                Map<Long, MessageId> failMessageIdMap = new HashMap<>();
                //                List<Long> nonExistIdList = betRecordService.listNotExistIdList(recordList.stream().map(BetRecordDo::getId).collect(Collectors.toList()), getGameType());
                //                if (CollectionUtils.isNotEmpty(nonExistIdList)) {
                //                    log.warn("{}, batchUpdate nonExistIdList: {}", getGameType().getName(), nonExistIdList);
                //                }
                //                Optional.ofNullable(nonExistIdList)
                //                        .orElse(Collections.emptyList())
                //                        .forEach(nonExistBetRecordId -> {
                //                            failMessageIdMap.put(nonExistBetRecordId, messageIdMap.get(nonExistBetRecordId));
                //                            messageIdMap.remove(nonExistBetRecordId);
                //                        });
                //                List<BetRecordDo> failRecordList = recordList.stream()
                //                        .filter(item -> nonExistIdList.contains(item.getId()))
                //                        .collect(Collectors.toList());
                //                if (MapUtils.isNotEmpty(messageIdMap)) {
                //                    getConsumer().acknowledge(messageIdMap.values().stream().collect(Collectors.toList()));
                //                }
                //                if (CollectionUtils.isNotEmpty(failRecordList) && MapUtils.isNotEmpty(failMessageIdMap)) {
                //                    Thread.currentThread().sleep(1000);
                //                    update(failRecordList, failMessageIdMap);
                //                }
            }
        } catch (Exception e) {
            log.error("{}, batchUpdate error", getGameType().getName(), e);
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
                betRecordManager().add(betRecordDo, getGameType());
                getConsumer().acknowledge(messageIdMap.get(betRecordDo.getId()));
            } catch (DuplicateKeyException e) {
                log.error("{} add error DuplicateKey, betRecordId: {}", getGameType().getName(), betRecordDo.getId());
                try {
                    getConsumer().acknowledge(messageIdMap.get(betRecordDo.getId()));
                } catch (Exception e1) {
                    log.error("{} add error, ack fail. betRecordId: {}", getGameType().getName(), betRecordDo.getId(), e1);
                }
            } catch (Exception e) {
                log.error("{} add error, betRecordId: {}", getGameType().getName(), betRecordDo.getId(), e);
            }
        }
    }

    private void update(List<BetRecordDo> recordList, Map<Long, MessageId> messageIdMap) {
        for (BetRecordDo<MariaMakilingGameRecordDo> betRecordDo : CollectionUtils.emptyIfNull(recordList)) {
            try {
                boolean ret = betRecordService.update(betRecordDo, getGameType());
                if (ret) {
                    MessageId messageId = messageIdMap.get(betRecordDo.getId());
                    if (Objects.nonNull(messageId)) {
                        getConsumer().acknowledge(messageId);
                    } else {
                        log.error("{} update success but acknowledge fail, messageId is null, betRecordId: {}, ", getGameType().getName(), betRecordDo.getId());
                    }
                } else {
                    log.error("{}, update fail, betRecordId: {}", getGameType().getName(), betRecordDo.getId());
                }
            } catch (Exception e) {
                log.error("{}, update error, betRecordId: {}", getGameType().getName(), betRecordDo.getId(), e);
            }
        }
    }
}
