package com.wegame.capi.biz.listener.jackpot;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mongodb.DuplicateKeyException;
import com.wegame.base.exception.RateLimitException;
import com.wegame.base.executor.WsThreadPoolExecutor;
import com.wegame.base.limiter.DistributeLimiter;
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
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final String PRE_KEY = "rate_limiter";
    private static final String limitMethodName = "batchAddRewardPoolInputRecord";

    private volatile boolean start = false;
    ExecutorService executor;
    DistributeLimiter distributeLimiter;

    @Autowired
    private RedissonClient redissonClient;

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

    public int getLimitRatePerSeconds() {
        return 500;
    }

    @PostConstruct
    public void init() {
        // 构建分布式限流器
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(String.format("%s:%s:%s",
                PRE_KEY,
                getGameType().getId(),
                limitMethodName));
        // 设置速率，1 秒中产生 x 个令牌
        rateLimiter.trySetRate(RateType.OVERALL, 1, getLimitRatePerSeconds(), RateIntervalUnit.SECONDS);
        distributeLimiter = new DistributeLimiter(rateLimiter);
        // 构建执行任务线程池
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
                            long sleepSeconds = RandomUtil.randomInt(5, 10);
                            log.warn("{}, not received any messages, sleep {} second", getGameType().getName(), sleepSeconds);
                            Thread.sleep( sleepSeconds * 1000);
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
                log.warn("{}, processMessages, msgType:{}, msgId:{}, retryTimes: {}, messageId: {}", getGameType().getName(), mqMessage.getType(), mqMessage.getMsgId(), message.getRedeliveryCount(), message.getMessageId());
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
                distributeLimiter.run(() -> this.jackpotManager().batchAddRewardPoolInputRecord(recordList));
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
                distributeLimiter.run(()-> this.jackpotManager().batchAddRewardPoolInputRecord(Arrays.asList(record)));
            } catch (DuplicateKeyException e) {
                log.warn("addRewardPoolInputRecord DuplicateKeyException", e);
            } catch (RateLimitException e) {
                failList.add(record.getId());
                log.warn("addRewardPoolInputRecord RateLimitException", e);
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
