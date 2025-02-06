package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.rocketmq.MQMessage;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class PulsarProducer {
    @Value("${pulsar.tenant}")
    private String tenant;

    @Value("${pulsar.namespaces}")
    private String namespace;
    @Autowired
    private PulsarClient pulsarClient;

    private final ConcurrentHashMap<String, Producer<byte[]>> producerCache = new ConcurrentHashMap<>();

    public <T> void sendMessage(GameTypeEnum gameType, MQMessage<T> mqMessage) {
        String topicName = String.format("persistent://%s/%s/%s", tenant, namespace, gameType.getName() + "_topic");
        try {
            Producer<byte[]> producer = producerCache.computeIfAbsent(topicName, this::createProducer);
            producer.newMessage()
                    .key(mqMessage.getMsgId()) // 关键点：设置 Key
                    .value(JSON.toJSONBytes(mqMessage))
                    .send();
        } catch (PulsarClientException e) {
            log.error("mariamakilingProducer send fail, msg={}", FastJsonUtils.toJSONString(mqMessage));
            throw new BusinessException(ErrorCode.ERR_SINGLE_WALLET_PLATFORM, e);
        }
    }

    private Producer<byte[]> createProducer(String topicName) {
        try {
            return pulsarClient.newProducer(Schema.BYTES)
                    .topic(topicName)
                    .producerName(GameTypeEnum.MARIA_MAKILING.getName())
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
