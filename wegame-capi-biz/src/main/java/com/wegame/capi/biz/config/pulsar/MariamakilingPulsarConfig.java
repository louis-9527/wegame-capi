package com.wegame.capi.biz.config.pulsar;

import com.wegame.base.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.wegame.base.PulsarConstants.PULSAR_TOPIC_FORMAT;
import static com.wegame.capi.common.Constants.MARIA_MAKILING_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * @author apple
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = MARIA_MAKILING_PROP, havingValue = TRUE_VAL)
public class MariamakilingPulsarConfig {

    @Value("${pulsar.dynamic.maria_makiling.url}")
    String mariaMakilingUrl;
    @Value("${pulsar.dynamic.maria_makiling.tenant}")
    String mariaMakilingTenant;
    @Value("${pulsar.dynamic.maria_makiling.namespace}")
    String mariaMakilingNamespace;
    @Value("${pulsar.dynamic.maria_makiling.bet_topic}")
    String mariaMakilingBetTopic;
    @Value("${pulsar.dynamic.maria_makiling.bet_deadLetter_topic}")
    String mariaMakilingBetDeadLetterTopic;

    @Bean(name = "mariaMakilingPulsarClient", destroyMethod = "close")
    public PulsarClient mariaMakilingPulsarClient() throws Exception {
        PulsarClient pulsarClient = PulsarClient.builder()
                .serviceUrl(mariaMakilingUrl)
                .keepAliveInterval(30, TimeUnit.SECONDS)
                //                .operationTimeout(100, TimeUnit.MILLISECONDS)
                .connectionTimeout(2000, TimeUnit.MILLISECONDS)
                .ioThreads(Runtime.getRuntime().availableProcessors() * 2)
                .listenerThreads(Runtime.getRuntime().availableProcessors())
                .connectionsPerBroker(10)
                .connectionMaxIdleSeconds(3000)
                .enableTcpNoDelay(true)
                // 10分钟打印一次统计信息
                .statsInterval(600, TimeUnit.SECONDS)
                .build();
        return pulsarClient;
    }

    @Bean(name = "mariamakilingProducer", destroyMethod = "close")
    public Producer<byte[]> mariamakilingProducer(@Autowired PulsarClient mariaMakilingPulsarClient) throws PulsarClientException {
        return mariaMakilingPulsarClient.newProducer(Schema.BYTES)
                .topic(String.format(PULSAR_TOPIC_FORMAT, mariaMakilingTenant, mariaMakilingNamespace, mariaMakilingBetTopic))
                .producerName(mariaMakilingTenant + "_" + UUID.randomUUID() + "_" + IpUtil.getLocalIp())
                // 开启批量发送
                .enableBatching(true)
                .batcherBuilder(BatcherBuilder.KEY_BASED)
                .batchingMaxMessages(100) // 每批最大 200 条消息
                // .batchingMaxBytes(4 * 1024) // 每个批次最大 4 KB
                // 10ms 内合并批量消息
                // 最大延迟 10 毫秒
                .batchingMaxPublishDelay(10, TimeUnit.MILLISECONDS)
                .sendTimeout(100, TimeUnit.MILLISECONDS)
                // 使用 LZ4 压缩，减少带宽占用
                .compressionType(CompressionType.LZ4)
                .autoUpdatePartitionsInterval(600, TimeUnit.SECONDS)
                .create();
    }


    @Bean(name = "mariamakilingConsumer", destroyMethod = "close")
    public Consumer<byte[]> mariamakilingConsumer(@Autowired PulsarClient mariaMakilingPulsarClient) throws PulsarClientException {
        String fullTopic = String.format(PULSAR_TOPIC_FORMAT, mariaMakilingTenant, mariaMakilingNamespace, mariaMakilingBetTopic);
        String deadLetterTopic = String.format(PULSAR_TOPIC_FORMAT, mariaMakilingTenant, mariaMakilingNamespace, mariaMakilingBetDeadLetterTopic);
        return mariaMakilingPulsarClient.newConsumer()
                .topic(fullTopic)
                .subscriptionName(mariaMakilingTenant)
                // 关键点：使用 Key_Shared 模式, 集群消费
                .subscriptionType(SubscriptionType.Key_Shared)
                // 增加内部缓存，提高吞吐量
                .receiverQueueSize(2000)
                .ackTimeout(60, TimeUnit.SECONDS)
                .batchReceivePolicy(BatchReceivePolicy.builder()
                        .maxNumMessages(500)  // 批量最多100条
                        .timeout(100, TimeUnit.MILLISECONDS)
                        .build())
                .deadLetterPolicy(DeadLetterPolicy.builder()
                        // **最多重试 10 次**
                        .maxRedeliverCount(15)
                        // **死信队列**
                        .deadLetterTopic(deadLetterTopic)
                        .build())
                // .keySharedPolicy(KeySharedPolicy.stickyHashRange())
                .autoUpdatePartitionsInterval(600, TimeUnit.SECONDS)
                .subscribe();
    }

    @Bean(name = "mariamakilingDeadLetterConsumer", destroyMethod = "close")
    public Consumer<byte[]> mariamakilingDeadLetterConsumer(@Autowired PulsarClient mariaMakilingPulsarClient) throws PulsarClientException {
        String deadLetterTopic = String.format(PULSAR_TOPIC_FORMAT, mariaMakilingTenant, mariaMakilingNamespace, mariaMakilingBetDeadLetterTopic);
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
