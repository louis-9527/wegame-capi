package com.wegame.capi.biz.config.pulsar;

import com.wegame.capi.biz.listener.OperationEventPulsarListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 干预事件Pulsar配置
 */
@Slf4j
@Configuration
public class OperationEventPulsarConfig {
    private static final String operationEventPulsarClient = "operationEventPulsarClient";
    private static final String mariaMakilingOperationEventConsumer = "mariaMakilingOperationEventConsumer";
    // todo fix
    @Value("${dynamic.operation.pulsar_url}")
    String dynamicOperationTopicUrl;
    @Value("${dynamic.operation.pulsar_topic}")
    String dynamicOperationTopic;

    @Value("${dynamic.operation.pulsar_group.maria_makiling}")
    String dynamicOperationGroupMariaMakiling;

    @Bean(name = operationEventPulsarClient, destroyMethod = "close")
    public PulsarClient operationEventPulsarClient() throws Exception {
        return PulsarClient.builder()
                .serviceUrl(dynamicOperationTopicUrl)
                .keepAliveInterval(30, TimeUnit.SECONDS)
                .build();
    }

    @Bean(name = mariaMakilingOperationEventConsumer, destroyMethod = "close")
    public Consumer<byte[]> mariaMakilingOperationEventConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                                  @Autowired OperationEventPulsarListener operationEventPulsarListener) throws PulsarClientException {
        String deadLetterTopic = dynamicOperationTopic;
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(deadLetterTopic)
                .subscriptionName(dynamicOperationGroupMariaMakiling)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(operationEventPulsarListener)
                .subscribe();
    }
}
