package com.wegame.capi.biz.config.pulsar;

import com.wegame.base.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.impl.auth.oauth2.AuthenticationFactoryOAuth2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.wegame.base.PulsarConstants.PULSAR_TOPIC_FORMAT;
import static com.wegame.capi.common.Constants.*;

/**
 * @author apple
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = KAPRE_PROP, havingValue = TRUE_VAL)
public class KaprePulsarConfig {

    public static final String kaprePulsarClient = "kaprePulsarClient";
    public static final String kapreProducer = "kapreProducer";
    public static final String kapreConsumer = "kapreConsumer";
    public static final String kapreDeadLetterConsumer = "kapreDeadLetterConsumer";

    @Value("${pulsar.dynamic.kapre.url}")
    String url;
    @Value("${pulsar.dynamic.kapre.tenant}")
    String tenant;
    @Value("${pulsar.dynamic.kapre.namespace}")
    String namespace;
    @Value("${pulsar.dynamic.kapre.bet_topic}")
    String betTopic;
    @Value("${pulsar.dynamic.kapre.bet_deadLetter_topic}")
    String betDeadLetterTopic;


    @Value("${pulsar.dynamic.kapre.auth.issuer_url}")
    String authIssuerUrl;
    @Value("${pulsar.dynamic.kapre.auth.credentials_file_path}")
    String authCredentialsFilePath;
    @Value("${pulsar.dynamic.kapre.auth.audience}")
    String authAudience;


    @Bean(name = kaprePulsarClient, destroyMethod = COMMON_CLOSE_METHOD)
    public PulsarClient kaprePulsarClient() throws Exception {
        log.info(BUILD_BEAN_FORMAT, kaprePulsarClient);

        Authentication authentication = AuthenticationFactoryOAuth2.clientCredentials(
                new URL(authIssuerUrl),
                Objects.requireNonNull(getClass().getResource(authCredentialsFilePath)),
                authAudience
        );
        return  PulsarClient.builder()
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
                .authentication(authentication)
                .build();


    }

    @Bean(name = kapreProducer, destroyMethod = COMMON_CLOSE_METHOD)
    public Producer<byte[]> kapreProducer(@Autowired PulsarClient kaprePulsarClient) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, kapreProducer);
        return kaprePulsarClient.newProducer(Schema.BYTES)
                .topic(String.format(PULSAR_TOPIC_FORMAT, tenant, namespace, betTopic))
                .producerName(tenant + "_" + UUID.randomUUID() + "_" + IpUtil.getLocalIp())
                // 开启批量发送
                .enableBatching(true)
                .batcherBuilder(BatcherBuilder.KEY_BASED)
                .batchingMaxMessages(200) // 每批最大 200 条消息
                // .batchingMaxBytes(4 * 1024) // 每个批次最大 4 KB
                // 10ms 内合并批量消息
                // 最大延迟 10 毫秒
                .batchingMaxPublishDelay(10, TimeUnit.MILLISECONDS)
                .sendTimeout(5, TimeUnit.SECONDS) // 5 秒超时
                // 使用 LZ4 压缩，减少带宽占用
                .compressionType(CompressionType.LZ4)
                .autoUpdatePartitionsInterval(60, TimeUnit.SECONDS)
                .create();
    }

    @Bean(name = kapreConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    public Consumer<byte[]> kapreConsumer(@Autowired PulsarClient kaprePulsarClient) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, kapreConsumer);
        String fullTopic = String.format(PULSAR_TOPIC_FORMAT, tenant, namespace, betTopic);
        String deadLetterTopic = String.format(PULSAR_TOPIC_FORMAT, tenant, namespace, betDeadLetterTopic);
        return kaprePulsarClient.newConsumer()
                .topic(fullTopic)
                .subscriptionName(tenant)
                // 关键点：使用 Key_Shared 模式, 集群消费
                .subscriptionType(SubscriptionType.Key_Shared)
                // 增加内部缓存，提高吞吐量
                .receiverQueueSize(2000)
                .ackTimeout(60, TimeUnit.SECONDS)
                .negativeAckRedeliveryDelay(60, TimeUnit.SECONDS)
                .batchReceivePolicy(BatchReceivePolicy.builder()
                        .maxNumMessages(200)  // 批量最多100条
                        .timeout(100, TimeUnit.MILLISECONDS)
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

    @Bean(name = kapreDeadLetterConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    public Consumer<byte[]> kapreDeadLetterConsumer(@Autowired PulsarClient kaprePulsarClient) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, kapreDeadLetterConsumer);
        String deadLetterTopic = String.format(PULSAR_TOPIC_FORMAT, tenant, namespace, betDeadLetterTopic);
        return kaprePulsarClient.newConsumer(Schema.BYTES)
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
