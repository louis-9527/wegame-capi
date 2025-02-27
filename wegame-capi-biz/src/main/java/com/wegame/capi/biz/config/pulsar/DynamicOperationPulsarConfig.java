package com.wegame.capi.biz.config.pulsar;

import com.wegame.capi.biz.listener.DynamicOperationPulsarListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DynamicOperationPulsarConfig {
    private static final String dynamicOperationMariaMakilingPulsarClient = "dynamicOperationMariaMakilingPulsarClient";
    private static final String dynamicOperationMariaMakilingConsumer = "dynamicOperationMariaMakilingConsumer";
    @Value("${dynamic.operation.pulsar_url}")
    String dynamicOperationTopicUrl;
    @Value("${dynamic.operation.pulsar_topic}")
    String dynamicOperationTopic;
    @Value("${dynamic.operation.pulsar_group.maria_makiling}")
    String dynamicOperationGroupMariaMakiling;
    @Autowired
    DynamicOperationPulsarListener dynamicOperationPulsarHandler;

    @Bean(name = dynamicOperationMariaMakilingPulsarClient, destroyMethod = "close")
    public PulsarClient dynamicOperationMariaMakilingPulsarClient() throws Exception {
        return PulsarClient.builder()
                .serviceUrl(dynamicOperationTopicUrl)
                .build();
    }

    @Bean(name = dynamicOperationMariaMakilingConsumer, destroyMethod = "close")
    public Consumer<byte[]> dynamicOperationMariaMakilingConsumer(@Autowired PulsarClient dynamicOperationMariaMakilingPulsarClient,
                                                                  @Autowired DynamicOperationPulsarListener dynamicOperationPulsarListener) throws PulsarClientException {
        String deadLetterTopic = dynamicOperationTopic;
        return dynamicOperationMariaMakilingPulsarClient.newConsumer(Schema.BYTES)
                .topic(deadLetterTopic)
                .subscriptionName(dynamicOperationGroupMariaMakiling)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(dynamicOperationPulsarListener)
                .subscribe();
    }


}
