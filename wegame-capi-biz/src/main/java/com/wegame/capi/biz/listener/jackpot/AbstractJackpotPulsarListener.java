package com.wegame.capi.biz.listener.jackpot;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mongodb.DuplicateKeyException;
import com.wegame.base.executor.WsThreadPoolExecutor;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.biz.manager.jackpot.AbstractJackpotManager;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.MessageTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolInputTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Messages;
import org.apache.pulsar.client.api.PulsarClientException;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.wegame.capi.common.Constants.BUILD_BEAN_FORMAT;

/**
 * jackpot pulsar listener
 *
 * @param <T>
 */
@Slf4j
public abstract class AbstractJackpotPulsarListener<T extends ToVoConverter> implements Runnable, InitializingBean {
    private volatile boolean start = false;
    ExecutorService executor;

    @Override
    public void afterPropertiesSet() {
        log.debug(BUILD_BEAN_FORMAT, getClass().getSimpleName());
    }

    public abstract Consumer<byte[]> getConsumer();

    public abstract GameTypeEnum getGameType();

    public abstract AbstractJackpotManager jackpotManager();

    public abstract TypeReference<MQMessage<BetRecordDo<T>>> getTypeReference();

    public abstract int getCorePoolSize();

    public abstract int getMaximumPoolSize();

    @PostConstruct
    public void init() {
        CustomizableThreadFactory threadFactory = new CustomizableThreadFactory(getGameType().getName() + "-jackpot-consumber-pool-");
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
        List<MessageId> addMessageIdList = new ArrayList<>();
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
                addMessageIdList.add(message.getMessageId());
            } else if (mqMessage.getType().equals(MessageTypeEnum.UPDATE.getCode()) && Objects.equals(BetStatusEnum.SUCCESS.getCode(), betRecord.getStatus())) {
                // 避免一批次消息里面有重复消息
                if (!updateMessageIdMap.containsKey(betRecord.getId())) {
                    updateMessageIdMap.put(betRecord.getId(), message.getMessageId());
                    updateRecordList.add(betRecord);
                } else {
                    try {
                        this.getConsumer().acknowledge(message.getMessageId());
                    } catch (PulsarClientException e) {
                        log.error("Error ack message", e);
                    }
                }
            } else {
                try {
                    this.getConsumer().acknowledge(message.getMessageId());
                } catch (PulsarClientException e) {
                    log.error("Error ack message", e);
                }
            }
        });
        // 批量ack
        if (!addMessageIdList.isEmpty()) {
            this.acknowledge(addMessageIdList);
        }
        // 批量处理落库
        if (!updateRecordList.isEmpty()) {
            List<JackpotRewardPoolInputRecordDo> recordList = new ArrayList<>();
            updateRecordList.forEach(record -> {
                if (Objects.nonNull(record.getJackpot())) {
                    recordList.add(JackpotRewardPoolInputRecordDo.builder()
                            .id(record.getId())
                            .type(JackpotRewardPoolInputTypeEnum.PAYOUT_IN)
                            .grandAmount(record.getJackpot().getGrandContributionAmount())
                            .majorAmount(record.getJackpot().getMajorContributionAmount())
                            .minorAmount(record.getJackpot().getMinorContributionAmount())
                            .miniAmount(record.getJackpot().getMiniContributionAmount())
                            .build());
                } else {
                    try {
                        this.getConsumer().acknowledge(updateMessageIdMap.get(record.getId()));
                    } catch (PulsarClientException e) {
                        log.error("Error ack message", e);
                    }
                }
            });
            // 批量处理
            try {
                this.jackpotManager().batchAddRewardPoolInputRecord(recordList);
            } catch (Exception e) {
                log.error("batchAddRewardPoolInputRecord error", e);
                List<Long> failList = this.addRewardPoolInputRecord(recordList);
                if (CollectionUtils.isNotEmpty(failList)) {
                    failList.forEach(updateMessageIdMap::remove);
                }
            }
            this.acknowledge(new ArrayList<>(updateMessageIdMap.values()));
        }
    }

    /**
     * 循环单个添加,返回失败的ID
     */
    private List<Long> addRewardPoolInputRecord(List<JackpotRewardPoolInputRecordDo> recordList) {
        log.info("loop single addRewardPoolInputRecord");
        List<Long> failList = new ArrayList<>();
        recordList.forEach(record -> {
            try {
                this.jackpotManager().addRewardPoolInputRecord(record);
            } catch (DuplicateKeyException e) {
                log.warn("addRewardPoolInputRecord DuplicateKeyException", e);
            } catch (Exception e) {
                failList.add(record.getId());
                log.error("addRewardPoolInputRecord Exception", e);
            }
        });
        return failList;
    }

    private void acknowledge(List<MessageId> messageIds) {
        // 批量ack
        try {
            this.getConsumer().acknowledge(messageIds);
        } catch (Exception e) {
            log.error("Error batch ack message", e);
        }
    }
}
