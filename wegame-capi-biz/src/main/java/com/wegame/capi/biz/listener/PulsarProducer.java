package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class PulsarProducer {

    private final ConcurrentHashMap<String, Producer<byte[]>> producerCache = new ConcurrentHashMap<>();

    @Value("${pulsar.tenant}")
    String tenant;
    @Value("${pulsar.namespaces}")
    String namespace;
    @Autowired
    PulsarClient pulsarClient;

    public <T> MessageId sendMessage(GameTypeEnum gameType, MQMessage<T> mqMessage, int tryTimes) {
        long startTime = System.currentTimeMillis();

        MessageId messageId = null;
        int times = 0;
        do {
            messageId = this.sendMessage(gameType, mqMessage);
            times++;
        } while (Objects.isNull(messageId) || times < tryTimes);
        long endTime = System.currentTimeMillis();
        log.info("sendMessage cost {} ms", endTime - startTime);
        return messageId;
    }

    public <T> MessageId sendMessage(GameTypeEnum gameType, MQMessage<T> mqMessage) {
        String topicName = String.format("persistent://%s/%s/%s", tenant, namespace, gameType.getName() + "_topic");
        try {
            return getProducer(topicName, gameType).newMessage()
                    // 关键点：设置 Key
                    .key(mqMessage.getMsgId())
                    .value(JSON.toJSONBytes(mqMessage))
                    .send();
        } catch (PulsarClientException e) {
            log.error("mariamakilingProducer send fail, msg={}", FastJsonUtils.toJSONString(mqMessage));
            throw new BusinessException(ErrorCode.ERR_SINGLE_WALLET_PLATFORM, e);
        }
    }

    private Producer<byte[]> getProducer(String topicName, GameTypeEnum gameType) {
        Producer<byte[]> producer = producerCache.get(topicName);
        if (Objects.isNull(producer)) {
            producer = createProducer(topicName, gameType);
            producerCache.put(topicName, producer);
        }
        return producer;
    }

    private Producer<byte[]> createProducer(String topicName, GameTypeEnum gameType) {
        try {
            return pulsarClient.newProducer(Schema.BYTES)
                    .topic(topicName)
                    .producerName(gameType.getName())
                    .create();
        } catch (PulsarClientException e) {
            throw new RuntimeException("Failed to create Pulsar producer for topic error: " + topicName, e);
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
}
