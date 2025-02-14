package com.wegame.capi.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class PulsarConsumerConfig {

    @Value("${pulsar.dynamic.maria_makiling.url}")
    private String mariaMakilingUrl;
    @Value("${pulsar.dynamic.maria_makiling.tenant}")
    private String mariaMakilingTenant;

    @Value("${pulsar.dynamic.maria_makiling.namespace}")
    private String mariaMakilingNamespace;

    @Value("${pulsar.dynamic.maria_makiling.bet_topic}")
    private String mariaMakilingBetTopic;

    @Value("${pulsar.dynamic.maria_makiling.bet_deadLetter_topic}")
    private String mariaMakilingBetDeadLetterTopic;

    @Bean(name = "mariamakilingConsumer", destroyMethod = "close")
    public Consumer<byte[]> consumer(PulsarClient mariaMakilingPulsarClient) throws PulsarClientException {
        String fullTopic = String.format("persistent://%s/%s/%s", mariaMakilingTenant, mariaMakilingNamespace, mariaMakilingBetTopic);
        String deadLetterTopic = String.format("persistent://%s/%s/%s", mariaMakilingTenant, mariaMakilingNamespace, mariaMakilingBetDeadLetterTopic);

        return mariaMakilingPulsarClient.newConsumer()
                .topic(fullTopic)
                .subscriptionName(mariaMakilingTenant)
                .subscriptionType(SubscriptionType.Key_Shared) // 关键点：使用 Key_Shared 模式
                .receiverQueueSize(200) // 增加内部缓存，提高吞吐量
                .ackTimeout(30, TimeUnit.SECONDS)
                .batchReceivePolicy(BatchReceivePolicy.builder()
                        .maxNumMessages(50)  // 批量最多100条
                        .timeout(10, TimeUnit.MILLISECONDS)
                        .build())
                .deadLetterPolicy(DeadLetterPolicy.builder()
                        .maxRedeliverCount(10)  // **最多重试 10 次**
                        .deadLetterTopic(deadLetterTopic) // **死信队列**
                        .build())
                // .keySharedPolicy(KeySharedPolicy.stickyHashRange())
                .subscribe();
    }

    @Bean(name = "mariamakilingDeadLetterConsumer", destroyMethod = "close")
    public Consumer<byte[]> deadLetterConsumer(PulsarClient mariaMakilingPulsarClient) throws PulsarClientException {
        String deadLetterTopic = String.format("persistent://%s/%s/%s", mariaMakilingTenant, mariaMakilingNamespace, mariaMakilingBetDeadLetterTopic);
        return mariaMakilingPulsarClient.newConsumer(Schema.BYTES)
                .topic(deadLetterTopic)
                .subscriptionName(mariaMakilingTenant + "_deadLetter")
                .subscriptionType(SubscriptionType.Shared)
                .messageListener((consumer, msg) -> {
                    String message = new String(msg.getData());
                    log.warn("Message moved to DLQ, consumerName: {},msgCount:{}", consumer.getConsumerName(), message);
                    try {
                        consumer.acknowledge(msg);
                    } catch (Exception e) {
                        log.error("Message moved acknowledge error:", e);
                    }
                }).subscribe();
    }
}
