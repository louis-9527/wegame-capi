package com.wegame.capi.infrastructure.config.pulsar;

import com.wegame.base.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.wegame.base.PulsarConstants.PULSAR_TOPIC_FORMAT;

/**
 * @author apple
 */
@Slf4j
@Configuration
public class MalakasPulsarConfig {

    @Value("${pulsar.dynamic.malakas.url}")
    String url;
    @Value("${pulsar.dynamic.malakas.tenant}")
    String tenant;
    @Value("${pulsar.dynamic.malakas.namespace}")
    String namespace;
    @Value("${pulsar.dynamic.malakas.bet_topic}")
    String betTopic;
    @Value("${pulsar.dynamic.malakas.bet_deadLetter_topic}")
    String betDeadLetterTopic;

    @Bean(name = "malakasPulsarClient", destroyMethod = "close")
    public PulsarClient malakasPulsarClient() throws Exception {
        return PulsarClient.builder()
                .serviceUrl(url)
                .build();
    }

    @Bean(name = "malakasProducer", destroyMethod = "close")
    public Producer<byte[]> malakasProducer(@Autowired PulsarClient malakasPulsarClient) throws PulsarClientException {
        return malakasPulsarClient.newProducer(Schema.BYTES)
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
                .create();
    }


    @Bean(name = "malakasConsumer", destroyMethod = "close")
    public Consumer<byte[]> malakasConsumer(@Autowired PulsarClient malakasPulsarClient) throws PulsarClientException {
        String fullTopic = String.format(PULSAR_TOPIC_FORMAT, tenant, namespace, betTopic);
        String deadLetterTopic = String.format(PULSAR_TOPIC_FORMAT, tenant, namespace, betDeadLetterTopic);
        return malakasPulsarClient.newConsumer()
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

    @Bean(name = "malakasDeadLetterConsumer", destroyMethod = "close")
    public Consumer<byte[]> malakasDeadLetterConsumer(@Autowired PulsarClient malakasPulsarClient) throws PulsarClientException {
        String deadLetterTopic = String.format(PULSAR_TOPIC_FORMAT, tenant, namespace, betDeadLetterTopic);
        return malakasPulsarClient.newConsumer(Schema.BYTES)
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
