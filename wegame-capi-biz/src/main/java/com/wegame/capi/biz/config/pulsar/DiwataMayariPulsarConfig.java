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
import static com.wegame.capi.common.Constants.*;

/**
 * @author apple
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = DIWATA_MAYARI_PROP, havingValue = TRUE_VAL)
public class DiwataMayariPulsarConfig {

    public static final String diwatamayariPulsarClient = "diwatamayariPulsarClient";
    public static final String diwatamayariProducer = "diwatamayariProducer";
    public static final String diwataMayariConsumer = "diwatamayariConsumer";
    public static final String diwatamayariDeadLetterConsumer = "diwatamayariDeadLetterConsumer";

    @Value("${pulsar.dynamic.diwataMayari.url}")
    String url;
    @Value("${pulsar.dynamic.diwataMayari.tenant}")
    String tenant;
    @Value("${pulsar.dynamic.diwataMayari.namespace}")
    String namespace;
    @Value("${pulsar.dynamic.diwataMayari.bet_topic}")
    String betTopic;
    @Value("${pulsar.dynamic.diwataMayari.bet_deadLetter_topic}")
    String betDeadLetterTopic;

    @Bean(name = diwatamayariPulsarClient, destroyMethod = COMMON_CLOSE_METHOD)
    public PulsarClient diwataMayariPulsarClient() throws Exception {
        log.debug(BUILD_BEAN_FORMAT, diwatamayariPulsarClient);
        PulsarClient pulsarClient = PulsarClient.builder()
                .serviceUrl(url)
                .keepAliveInterval(30, TimeUnit.SECONDS)
                //                .operationTimeout(100, TimeUnit.MILLISECONDS)
                .connectionTimeout(2000, TimeUnit.MILLISECONDS)
                .ioThreads(Runtime.getRuntime().availableProcessors() * 2)
                .listenerThreads(Runtime.getRuntime().availableProcessors())
                .connectionsPerBroker(6)
                .connectionMaxIdleSeconds(3000)
                .enableTcpNoDelay(true)
                // 10分钟打印一次统计信息
                .statsInterval(600, TimeUnit.SECONDS)
                .build();
        return pulsarClient;
    }

    @Bean(name = diwatamayariProducer, destroyMethod = COMMON_CLOSE_METHOD)
    public Producer<byte[]> diwataMayariProducer(@Autowired PulsarClient diwatamayariPulsarClient) throws PulsarClientException {
        log.debug(BUILD_BEAN_FORMAT, diwatamayariProducer);
        return diwatamayariPulsarClient.newProducer(Schema.BYTES)
                .topic(String.format(PULSAR_TOPIC_FORMAT, tenant, namespace, betTopic))
                .producerName(tenant + "_" + UUID.randomUUID() + "_" + IpUtil.getLocalIp())
                // 开启批量发送
                .enableBatching(true)
                .batcherBuilder(BatcherBuilder.KEY_BASED)
                .batchingMaxMessages(1000)               // 默认 1000，可再涨到 5000
                .batchingMaxBytes(128 * 1024)            // 128 KB 一包，网卡 1 G 下最优
                // 10ms 内合并批量消息
                // 最大延迟 10 毫秒
                .batchingMaxPublishDelay(5, TimeUnit.MILLISECONDS)
                .blockIfQueueFull(true)                  // 防止本地队列打爆丢消息
                .maxPendingMessages(50000)
                .sendTimeout(5, TimeUnit.SECONDS) // 5 秒超时
                // 使用 LZ4 压缩，减少带宽占用
                .compressionType(CompressionType.LZ4)
                .autoUpdatePartitionsInterval(60, TimeUnit.SECONDS)

                .create();
    }

    @Bean(name = diwataMayariConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    public Consumer<byte[]> diwataMayariConsumer(@Autowired PulsarClient diwatamayariPulsarClient) throws PulsarClientException {
        log.debug(BUILD_BEAN_FORMAT, diwataMayariConsumer);
        String fullTopic = String.format(PULSAR_TOPIC_FORMAT, tenant, namespace, betTopic);
        String deadLetterTopic = String.format(PULSAR_TOPIC_FORMAT, tenant, namespace, betDeadLetterTopic);
        return diwatamayariPulsarClient.newConsumer()
                .topic(fullTopic)
                .subscriptionName(tenant)
                // 关键点：使用 Key_Shared 模式, 集群消费
                .subscriptionType(SubscriptionType.Key_Shared)
                // 增加内部缓存，提高吞吐量
                .receiverQueueSize(5000)
                .poolMessages(true)
                .ackTimeout(60, TimeUnit.SECONDS)
                .negativeAckRedeliveryDelay(600, TimeUnit.SECONDS)
                .batchReceivePolicy(BatchReceivePolicy.builder()
                        .maxNumMessages(1000)  // 批量最多100条
                        .timeout(2000, TimeUnit.MILLISECONDS)
                        .build())
                .deadLetterPolicy(DeadLetterPolicy.builder()
                        // **最多重试 10 次**
                        .maxRedeliverCount(15)
                        // **死信队列**
                        .deadLetterTopic(deadLetterTopic)
                        .build())
                // .keySharedPolicy(KeySharedPolicy.stickyHashRange())
                .subscribe();
    }

    @Bean(name = diwatamayariDeadLetterConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    public Consumer<byte[]> diwataMayariDeadLetterConsumer(@Autowired PulsarClient diwatamayariPulsarClient) throws PulsarClientException {
        log.debug(BUILD_BEAN_FORMAT, diwatamayariDeadLetterConsumer);
        String deadLetterTopic = String.format(PULSAR_TOPIC_FORMAT, tenant, namespace, betDeadLetterTopic);
        return diwatamayariPulsarClient.newConsumer(Schema.BYTES)
                .topic(deadLetterTopic)
                .subscriptionName(tenant + "_deadLetter")
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
