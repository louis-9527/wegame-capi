package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.base.utils.IpUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PulsarProducer {

    // private final ConcurrentHashMap<String, Producer<byte[]>> producerCache = new ConcurrentHashMap<>();

    @Value("${pulsar.tenant}")
    String tenant;
    @Value("${pulsar.namespaces}")
    String namespace;
    @Autowired
    PulsarClient pulsarClient;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private Producer<byte[]> producer;

    public <T> MessageId sendMessage(GameTypeEnum gameType, MQMessage<T> mqMessage, int tryTimes) {
        long startTime = System.currentTimeMillis();
        MessageId messageId;
        int times = 0;
        do {
            messageId = this.sendMessage(gameType, mqMessage);
            times++;
        } while (Objects.isNull(messageId) && times < tryTimes);
        long endTime = System.currentTimeMillis();
        log.info("sendMessage cost {} ms", endTime - startTime);
        return messageId;
    }

    public <T> MessageId sendMessage(GameTypeEnum gameType, MQMessage<T> mqMessage) {
        try {
            log.info("sendMessage--:type:{},msgId:{}", mqMessage.getType(), mqMessage.getMsgId());
            if (mqMessage.getType().equals(MessageTypeEnum.ADD.getCode())) {
                redisTemplate.opsForValue().increment("zhumeng001_send_msg_count", 1);
            }
            return producer.newMessage()
                    .key(mqMessage.getMsgId())
                    .value(JSON.toJSONBytes(mqMessage))
                    .send();
//
//            return getProducer(topicName, gameType).newMessage()
//                    // 关键点：设置 Key
//                    .key(mqMessage.getMsgId())
//                    .value(JSON.toJSONBytes(mqMessage))
//                    .send();
        } catch (PulsarClientException e) {
            log.error("mariamakilingProducer send fail, msg={}", FastJsonUtils.toJSONString(mqMessage));
            throw new BusinessException(ErrorCode.ERR_SINGLE_WALLET_PLATFORM, e);
        }
    }
//
//    private Producer<byte[]> getProducer(String topicName, GameTypeEnum gameType) {
//        return producerCache.computeIfAbsent(topicName, k -> this.createProducer(topicName, gameType));
//    }

    private Producer<byte[]> createProducer(String topicName, GameTypeEnum gameType) {
        try {
            String producerName = gameType.getName() + "_" + UUID.randomUUID() + "_" + IpUtil.getLocalIp();
            return pulsarClient.newProducer(Schema.BYTES)
                    .topic(topicName)
                    .producerName(producerName)
                    .enableBatching(true) // 开启批量发送
                    .batchingMaxMessages(200) // 每批最大 200 条消息
                    .batchingMaxPublishDelay(10, TimeUnit.MILLISECONDS) // 10ms 内合并批量消息
                    .sendTimeout(5, TimeUnit.SECONDS) // 5 秒超时
                    .compressionType(CompressionType.LZ4) // 使用 LZ4 压缩，减少带宽占用
                    .create();
        } catch (PulsarClientException e) {
            throw new RuntimeException("Failed to create Pulsar producer for topic error: " + topicName, e);
        }
    }

    @PostConstruct
    public void init() {
        GameTypeEnum gameType = GameTypeEnum.MARIA_MAKILING;
        String topicName = String.format("persistent://%s/%s/%s", tenant, namespace, gameType.getName() + "_topic");
        producer = this.createProducer(topicName, gameType);
        // producerCache.computeIfAbsent(topicName, k -> this.createProducer(topicName, gameType));
    }

    @PreDestroy
    public void shutdown() {
        try {
            producer.close();
        } catch (Exception e) {
            log.error("Failed to close Pulsar producer for topic error", e);
        }
//        producerCache.forEach((key, value) -> {
//            try {
//                value.close();
//            } catch (PulsarClientException e) {
//                log.error("Failed to close Pulsar producer for topic error, topic={}", key, e);
//            }
//        });
//        producerCache.clear();
    }
}
