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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PulsarProducer {

    private final ConcurrentHashMap<GameTypeEnum, Producer<byte[]>> producerCache = new ConcurrentHashMap<>();

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
            producerCache.put(gameType, createProducer(topicName, gameType.getName()));
        }
    }

    @PreDestroy
    public void shutdown() {
        producerCache.forEach((key, value) -> {
            try {
                value.close();
            } catch (PulsarClientException e) {
                log.error("Failed to close Pulsar producer for topic error, topic={}", key, e);
            }
        });
        producerCache.clear();
    }

    public <T> MessageId syncSendMessage(GameTypeEnum gameTypeEnum, MQMessage<T> mqMessage, int tryTimes) {
        long startTime = System.currentTimeMillis();
        MessageId messageId;
        int times = 0;
        do {
            messageId = syncSendMessage(gameTypeEnum, mqMessage);
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

    public <T> CompletableFuture<MessageId> asyncSendMessage(GameTypeEnum gameType, MQMessage<T> mqMessage) {
        log.info("{}Producer asyncSendMessage msgType:{}, msgId:{}", gameType.getName(), mqMessage.getType(), mqMessage.getMsgId());
        return getProducer(gameType)
                .newMessage()
                .key(mqMessage.getMsgId())
                .value(JSON.toJSONBytes(mqMessage))
                .sendAsync();
    }

    Producer<byte[]> getProducer(GameTypeEnum gameType) {
        return producerCache.get(gameType);
    }

    private Producer<byte[]> createProducer(String topicName, String gameName) {
        try {
            String producerName = name + "_" + gameName + "_" + UUID.randomUUID() + "_" + IpUtil.getLocalIp();
            return pulsarClient.newProducer(Schema.BYTES)
                    .topic(topicName)
                    .producerName(producerName)
                    .enableBatching(true) // 启用批量发送
                    .batchingMaxMessages(10) // 每个批次最多包含 10 条消息
                    .batchingMaxBytes(4 * 1024) // 每个批次最大 4 KB
                    .batcherBuilder(BatcherBuilder.KEY_BASED)
                    .batchingMaxPublishDelay(1, TimeUnit.MILLISECONDS) // 最大延迟 1 毫秒
                    .compressionType(CompressionType.LZ4)
                    .create();
        } catch (PulsarClientException e) {
            throw new RuntimeException("Failed to create Pulsar producer for topic error: " + topicName, e);
        }
    }
}
