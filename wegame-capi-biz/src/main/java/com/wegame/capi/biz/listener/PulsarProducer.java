package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.IpUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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

    public <T> MessageId sendMessage(GameTypeEnum gameTypeEnum, MQMessage<T> mqMessage, int tryTimes) {
        long startTime = System.currentTimeMillis();
        MessageId messageId;
        int times = 0;
        do {
            messageId = this.sendMessage(gameTypeEnum, mqMessage);
            times++;
        } while (Objects.isNull(messageId) && times < tryTimes);
        long endTime = System.currentTimeMillis();
        log.info("sendMessage cost {} ms", endTime - startTime);
        return messageId;
    }

    public <T> MessageId sendMessage(GameTypeEnum gameType, MQMessage<T> mqMessage) {
        try {
            log.info("{}Producer sendMessage--:type:{},msgId:{}", gameType.getName(), mqMessage.getType(), mqMessage.getMsgId());
            return producerCache.get(gameType)
                    .newMessage()
                    .key(mqMessage.getMsgId())
                    .value(JSON.toJSONBytes(mqMessage))
                    .send();
        } catch (PulsarClientException e) {
            log.error("{}Producer send fail, msgId={}", gameType.getName(), mqMessage.getMsgId(), e);
            throw new BusinessException(ErrorCode.ERR_SINGLE_WALLET_PLATFORM, e);
        }
    }

    private Producer<byte[]> createProducer(String topicName, String gameName) {
        try {
            String producerName = name + "_" + gameName + "_" + UUID.randomUUID() + "_" + IpUtil.getLocalIp();
            return pulsarClient.newProducer(Schema.BYTES)
                    .topic(topicName)
                    .producerName(producerName)
                    .enableBatching(true) // 开启批量发送
                    .batchingMaxMessages(200) // 每批最大 200 条消息
                    .batchingMaxPublishDelay(10, TimeUnit.MILLISECONDS) // 10ms 内合并批量消息
                    //.sendTimeout(5, TimeUnit.SECONDS) // 5 秒超时
                    .compressionType(CompressionType.LZ4) // 使用 LZ4 压缩，减少带宽占用
                    .create();
        } catch (PulsarClientException e) {
            throw new RuntimeException("Failed to create Pulsar producer for topic error: " + topicName, e);
        }
    }
}
