package com.wegame.capi.biz.config.pulsar;

import com.wegame.base.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@ConditionalOnProperty(name = MANGKUKULAM_PROP, havingValue = TRUE_VAL)
public class MangkukulamPulsarConfig {

    public static final String mangkukulamPulsarClient = "mangkukulamPulsarClient";
    public static final String mangkukulamProducer = "mangkukulamProducer";
    public static final String mangkukulamConsumer = "mangkukulamConsumer";
    public static final String mangkukulamDeadLetterConsumer = "mangkukulamDeadLetterConsumer";

    @Value("${pulsar.dynamic.mangkukulam.url}")
    String mangkukulamUrl;
    @Value("${pulsar.dynamic.mangkukulam.tenant}")
    String mangkukulamTenant;
    @Value("${pulsar.dynamic.mangkukulam.namespace}")
    String mangkukulamNamespace;
    @Value("${pulsar.dynamic.mangkukulam.bet_topic}")
    String mangkukulamBetTopic;
    @Value("${pulsar.dynamic.mangkukulam.bet_deadLetter_topic}")
    String mangkukulamBetDeadLetterTopic;

    @Bean(name = mangkukulamPulsarClient, destroyMethod = COMMON_CLOSE_METHOD)
    public PulsarClient mangkukulamPulsarClient() throws Exception {
        log.debug(BUILD_BEAN_FORMAT, mangkukulamPulsarClient);
        PulsarClient pulsarClient = PulsarClient.builder()
                .serviceUrl(mangkukulamUrl)
                .keepAliveInterval(30, TimeUnit.SECONDS)
                //                .operationTimeout(100, TimeUnit.MILLISECONDS)
                .connectionTimeout(2000, TimeUnit.MILLISECONDS)
                .ioThreads(Runtime.getRuntime().availableProcessors() * 2)
                .listenerThreads(Runtime.getRuntime().availableProcessors())
                .connectionsPerBroker(60)
                .connectionMaxIdleSeconds(3000)
                .enableTcpNoDelay(true)
                // 10分钟打印一次统计信息
                .statsInterval(600, TimeUnit.SECONDS)
                .build();
        return pulsarClient;
    }

    @Bean(name = mangkukulamProducer, destroyMethod = COMMON_CLOSE_METHOD)
    public Producer<byte[]> mangkukulamProducer(@Autowired @Qualifier("mangkukulamPulsarClient") PulsarClient mangkukulamPulsarClient) throws PulsarClientException {
        log.debug(BUILD_BEAN_FORMAT, mangkukulamProducer);
        return mangkukulamPulsarClient.newProducer(Schema.BYTES)
                .topic(String.format(PULSAR_TOPIC_FORMAT, mangkukulamTenant, mangkukulamNamespace, mangkukulamBetTopic))
                .producerName(mangkukulamTenant + "_" + UUID.randomUUID() + "_" + IpUtil.getLocalIp())
                // 开启批量发送
                .enableBatching(true)
                .batcherBuilder(BatcherBuilder.KEY_BASED)
                .batchingMaxMessages(100) // 每批最大 200 条消息
                // .batchingMaxBytes(4 * 1024) // 每个批次最大 4 KB
                // 10ms 内合并批量消息
                // 最大延迟 10 毫秒
                .batchingMaxPublishDelay(10, TimeUnit.MILLISECONDS)
                .sendTimeout(150, TimeUnit.MILLISECONDS)
                // 使用 LZ4 压缩，减少带宽占用
                .compressionType(CompressionType.LZ4)
                .autoUpdatePartitions(true)
                .autoUpdatePartitionsInterval(600, TimeUnit.SECONDS)
                .create();
    }


    @Bean(name = mangkukulamConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    public Consumer<byte[]> mangkukulamConsumer(@Autowired @Qualifier("mangkukulamPulsarClient") PulsarClient mangkukulamPulsarClient) throws PulsarClientException {
        log.debug(BUILD_BEAN_FORMAT, mangkukulamConsumer);
        String fullTopic = String.format(PULSAR_TOPIC_FORMAT, mangkukulamTenant, mangkukulamNamespace, mangkukulamBetTopic);
        String deadLetterTopic = String.format(PULSAR_TOPIC_FORMAT, mangkukulamTenant, mangkukulamNamespace, mangkukulamBetDeadLetterTopic);
        return mangkukulamPulsarClient.newConsumer()
                .topic(fullTopic)
                .subscriptionName(mangkukulamTenant)
                // 关键点：使用 Key_Shared 模式, 集群消费
                .subscriptionType(SubscriptionType.Key_Shared)
                // 增加内部缓存，提高吞吐量
                .receiverQueueSize(2000)
                .ackTimeout(60, TimeUnit.SECONDS)
                .batchReceivePolicy(BatchReceivePolicy.builder()
                        // 批量最多100条
                        .maxNumMessages(500)
                        .timeout(100, TimeUnit.MILLISECONDS)
                        .build())
                .deadLetterPolicy(DeadLetterPolicy.builder()
                        // **最多重试 10 次**
                        .maxRedeliverCount(10)
                        // **死信队列**
                        .deadLetterTopic(deadLetterTopic)
                        .build())
                // .keySharedPolicy(KeySharedPolicy.stickyHashRange())
                .autoUpdatePartitions(true)
                .autoUpdatePartitionsInterval(600, TimeUnit.SECONDS)
                .subscribe();
    }

    @Bean(name = mangkukulamDeadLetterConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    public Consumer<byte[]> mangkukulamDeadLetterConsumer(@Autowired @Qualifier("mangkukulamPulsarClient") PulsarClient mangkukulamPulsarClient) throws PulsarClientException {
        log.debug(BUILD_BEAN_FORMAT, mangkukulamDeadLetterConsumer);
        String deadLetterTopic = String.format(PULSAR_TOPIC_FORMAT, mangkukulamTenant, mangkukulamNamespace, mangkukulamBetDeadLetterTopic);
        return mangkukulamPulsarClient.newConsumer(Schema.BYTES)
                .topic(deadLetterTopic)
                .subscriptionName(mangkukulamTenant + "_deadLetter")
                .subscriptionType(SubscriptionType.Shared)
                .messageListener((consumer, msg) -> {
                    String message = new String(msg.getData());
                    log.error("Message moved to DLQ, consumerName: {}, msgContent:{}", consumer.getConsumerName(), message);
                    try {
                        consumer.acknowledge(msg);
                    } catch (Exception e) {
                        log.error("Message moved acknowledge error:", e);
                    }
                })
                .autoUpdatePartitions(true)
                .autoUpdatePartitionsInterval(600, TimeUnit.SECONDS)
                .subscribe();
    }
}
