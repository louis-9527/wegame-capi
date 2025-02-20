package com.wegame.capi.biz.config.pulsar;

import com.alibaba.fastjson.JSON;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.listener.DynamicOperationPulsarHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.wegame.base.PulsarConstants.PULSAR_TOPIC_FORMAT;

@Slf4j
@Configuration
public class DynamicOperationPulsarConfig {
    @Value("${dynamic.operation.pulsar_url}")
    String dynamicOperationTopicUrl;
    @Value("${dynamic.operation.pulsar_tenant}")
    String dynamicOperationTenant;
    @Value("${dynamic.operation.pulsar_namespace}")
    String dynamicOperationNamespace;
    @Value("${dynamic.operation.pulsar_topic}")
    String dynamicOperationTopic;

    @Value("${dynamic.operation.pulsar_group.maria_makiling}")
    String dynamicOperationGroupMariaMakiling;

    @Autowired
    private DynamicOperationPulsarHandler dynamicOperationPulsarHandler;

    @Bean(name = "dynamicOperationPulsarClient", destroyMethod = "close")
    public PulsarClient mariaMakilingPulsarClient() throws Exception {
        return PulsarClient.builder()
                .serviceUrl(dynamicOperationTopicUrl)
                .build();
    }

    @Bean(name = "dynamicOperationConsumer", destroyMethod = "close")
    public Consumer<byte[]> dynamicOperationConsumer(@Autowired PulsarClient mariaMakilingPulsarClient) throws PulsarClientException {
        String deadLetterTopic = String.format(PULSAR_TOPIC_FORMAT, dynamicOperationTenant, dynamicOperationNamespace, dynamicOperationTopic);
        return mariaMakilingPulsarClient.newConsumer(Schema.BYTES)
                .topic(deadLetterTopic)
                .subscriptionName(dynamicOperationGroupMariaMakiling)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener((consumer, msg) -> {
                    DynamicOperationEventDo dynamicOperationMsgDo = JSON.parseObject(new String(msg.getData()), DynamicOperationEventDo.class);
                    Boolean result = dynamicOperationPulsarHandler.handleMessage(dynamicOperationMsgDo);
                    if (result) {
                        try {
                            consumer.acknowledge(msg);
                        } catch (Exception e) {
                            log.error("dynamicOperation msg ack error:", e);
                        }
                    }
                }).subscribe();
    }

}
