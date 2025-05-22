package com.wegame.capi.biz.listener.jackpot;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.executor.WsThreadPoolExecutor;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Messages;
import org.apache.pulsar.client.api.PulsarClientException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * jackpot pulsar listener
 * @param <T>
 */
@Slf4j
public abstract class AbstractJackPotPulsarListener <T extends ToVoConverter> implements Runnable {
    private volatile boolean start = false;
    ExecutorService executor;
    public abstract Consumer<byte[]> getConsumer();

    public abstract GameTypeEnum getGameType();

    public abstract TypeReference<MQMessage<BetRecordDo<T>>> getTypeReference();

    public abstract int getCorePoolSize();

    public abstract int getMaximumPoolSize();
    @PostConstruct
    public void init() {
        CustomizableThreadFactory threadFactory = new CustomizableThreadFactory(getGameType().getName() + "-jackPot-consumber-pool-");
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
        log.info("start destroy jackPot pulsar listener");
        start = false;
        if (executor != null) {
            log.info("start jackPot closing executor: {}", executor);
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
        log.info("{} started....", getClass().getSimpleName());
        for (int i = 0; i < getMaximumPoolSize(); i++) {
            executor.submit(() -> {
                while (start) {
                    try {
                        MDC.put(ThreadMdcUtil.TRACE_ID, UUID.randomUUID().toString(true));
                        Messages<byte[]> messages = getConsumer().batchReceive();
                        if (messages == null || messages.size() == 0) {
                            log.warn("{}, not received any messages, sleep one second", getGameType().getName());
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
        List<MessageId> addMessageIdLis = new ArrayList<>();
        Map<Long, MessageId> updateMessageIdMap = new HashMap<>(messages.size());
        List<BetRecordDo<T>> updateRecordList = new ArrayList<>();

        messages.forEach(message -> {
            MQMessage<BetRecordDo<T>> mqMessage = JSON.parseObject(new String(message.getData()),
                    this.getTypeReference());
            BetRecordDo<T> betRecord = mqMessage.getBody();
            if (message.getRedeliveryCount() > 0) {
                log.info("{}, processMessages, msgType:{}, msgId:{}, retryTimes: {}, messageId: {}", getGameType().getName(), mqMessage.getType(), mqMessage.getMsgId(), message.getRedeliveryCount(), message.getMessageId());
            }
            if (mqMessage.getType().equals(MessageTypeEnum.ADD.getCode())) {
                // 避免一批次消息里面有重复消息
                addMessageIdLis.add(message.getMessageId());
            } else if (mqMessage.getType().equals(MessageTypeEnum.UPDATE.getCode())) {
                // 避免一批次消息里面有重复消息
                if (!updateMessageIdMap.containsKey(betRecord.getId())) {
                    updateMessageIdMap.put(betRecord.getId(), message.getMessageId());
                    updateRecordList.add(betRecord);
                }else {
                    try {
                        this.getConsumer().acknowledge(message.getMessageId());
                    } catch (PulsarClientException e) {
                        log.error("Error ack message", e);
                    }
                }
            }
        });
        // 批量ack
        if (!addMessageIdLis.isEmpty()) {
            try {
                this.getConsumer().acknowledge(addMessageIdLis);
            } catch (PulsarClientException e) {
                log.error("Error batch ack message", e);
            }
        }
        // 批量处理落库

    }
}
