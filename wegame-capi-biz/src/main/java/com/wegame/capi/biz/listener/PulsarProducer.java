package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.IpUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.utils.SecureRandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PulsarProducer {
    private static final ConcurrentHashMap<GameTypeEnum, List<Producer<byte[]>>> producerListCache = new ConcurrentHashMap<>();
    private static final Map<GameTypeEnum, Integer> PRODUCER_NUM_CACHE = new ImmutableMap.Builder<GameTypeEnum, Integer>()
            .put(GameTypeEnum.MARIA_MAKILING, 5)
            .build();
    @Value("${spring.application.env}")
    String env;
    @Value("${spring.application.name}")
    String name;
    @Autowired
    PulsarClient pulsarClient;

    @PostConstruct
    public void init() {
        for (GameTypeEnum gameType : GameTypeEnum.values()) {
            String topicName = String.format("persistent://%s/%s/%s", name + "_" + gameType.getName(), env, gameType.getName() + "_topic");
            List<Producer<byte[]>> producers = new ArrayList<>();
            int num = PRODUCER_NUM_CACHE.get(gameType);
            int tempNum = 0;
            while (tempNum < num) {
                producers.add(createProducer(topicName, gameType.getName(), tempNum));
                tempNum++;
            }
            producerListCache.put(gameType, producers);
        }
    }

    @PreDestroy
    public void shutdown() {
        producerListCache.forEach((key, value) -> {
            for (Producer<byte[]> producer : value) {
                try {
                    producer.close();
                } catch (Exception e) {
                    log.error("Failed to close Pulsar producer for topic error, topic={}", key, e);
                }
            }
        });
        producerListCache.clear();
    }

    public <T> MessageId syncSendMessage(GameTypeEnum gameTypeEnum, MQMessage<T> mqMessage, int tryTimes) {
        long startTime = System.currentTimeMillis();
        MessageId messageId;
        int times = 0;
        do {
            messageId = this.asyncSendMessage(gameTypeEnum, mqMessage);
            times++;
        } while (Objects.isNull(messageId) && times < tryTimes);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        if (duration <= 10) {
            log.info("sendMessage cost time less 10, {} ms", endTime - startTime);
        } else if (duration > 10 && duration <= 20) {
            log.info("sendMessage cost time(10-20], {} ms", endTime - startTime);
        } else if (duration > 20) {
            log.info("sendMessage cost time more than 20, {} ms", endTime - startTime);
        }
        return messageId;
    }

    public <T> MessageId syncSendMessage(GameTypeEnum gameType, MQMessage<T> mqMessage) {
        try {
            log.info("{}Producer syncSendMessage type:{},msgId:{}", gameType.getName(), mqMessage.getType(), mqMessage.getMsgId());
            return getProducer(gameType)
                    .newMessage()
                    .key(mqMessage.getMsgId())
                    .value(JSON.toJSONBytes(mqMessage))
                    .send();
        } catch (PulsarClientException e) {
            log.error("{}Producer send fail, msgId={}", gameType.getName(), mqMessage.getMsgId(), e);
            throw new BusinessException(ErrorCode.ERR_SINGLE_WALLET_PLATFORM, e);
        }
    }

    public <T> MessageId asyncSendMessage(GameTypeEnum gameType, MQMessage<T> mqMessage) {
        try {
            log.info("{}Producer asyncSendMessage msgType:{}, msgId:{}", gameType.getName(), mqMessage.getType(), mqMessage.getMsgId());
            return getProducer(gameType)
                    .newMessage()
                    .key(mqMessage.getMsgId())
                    .value(JSON.toJSONBytes(mqMessage))
                    .sendAsync().get();
        } catch (ExecutionException e) {
            log.error("{}Producer send fail, msgId={}", gameType.getName(), mqMessage.getMsgId(), e);
            throw new BusinessException(ErrorCode.ERR_SINGLE_WALLET_PLATFORM, e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    Producer<byte[]> getProducer(GameTypeEnum gameType) {
        int num = PRODUCER_NUM_CACHE.get(gameType);
        int index = SecureRandomUtil.randomIntInRange(0, num - 1);
        return producerListCache.get(gameType).get(index);
    }

    private Producer<byte[]> createProducer(String topicName, String gameName, Integer index) {
        try {
            String producerName = name + "_" + gameName + "_" + UUID.randomUUID() + "_" + IpUtil.getLocalIp() + "_" + index;
            return pulsarClient.newProducer(Schema.BYTES)
                    .topic(topicName)
                    .producerName(producerName)
                    .enableBatching(true) // 开启批量发送
                    .batchingMaxMessages(20) // 每批最大 200 条消息
                    // .batchingMaxPublishDelay(10, TimeUnit.MILLISECONDS) // 10ms 内合并批量消息
                    // .sendTimeout(5, TimeUnit.SECONDS) // 5 秒超时
                    .compressionType(CompressionType.LZ4) // 使用 LZ4 压缩，减少带宽占用
                    .create();
        } catch (PulsarClientException e) {
            throw new RuntimeException("Failed to create Pulsar producer for topic error: " + topicName, e);
        }
    }
}
