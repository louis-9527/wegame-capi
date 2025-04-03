package com.wegame.capi.biz.config.pulsar;

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
public class IbongAdarnaPulsarConfig {

    @Value("${pulsar.dynamic.ibong_adarna.url}")
    String ibongAdarnaUrl;
    @Value("${pulsar.dynamic.ibong_adarna.tenant}")
    String ibongAdarnaTenant;
    @Value("${pulsar.dynamic.ibong_adarna.namespace}")
    String ibongAdarnaNamespace;
    @Value("${pulsar.dynamic.ibong_adarna.bet_topic}")
    String ibongAdarnaBetTopic;
    @Value("${pulsar.dynamic.ibong_adarna.bet_deadLetter_topic}")
    String ibongAdarnaBetDeadLetterTopic;

    @Bean(name = "ibongadarnaPulsarClient", destroyMethod = "close")
    public PulsarClient ibongadarnaPulsarClient() throws Exception {
        return PulsarClient.builder()
                .serviceUrl(ibongAdarnaUrl)
                .keepAliveInterval(30, TimeUnit.SECONDS)
                .build();
    }

    @Bean(name = "ibongadarnaProducer", destroyMethod = "close")
    public Producer<byte[]> ibongadarnaProducer(@Autowired PulsarClient ibongadarnaPulsarClient) throws PulsarClientException {
        return ibongadarnaPulsarClient.newProducer(Schema.BYTES)
                .topic(String.format(PULSAR_TOPIC_FORMAT, ibongAdarnaTenant, ibongAdarnaNamespace, ibongAdarnaBetTopic))
                .producerName(ibongAdarnaTenant + "_" + UUID.randomUUID() + "_" + IpUtil.getLocalIp())
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


    @Bean(name = "ibongadarnaConsumer", destroyMethod = "close")
    public Consumer<byte[]> ibongadarnaConsumer(@Autowired PulsarClient ibongadarnaPulsarClient) throws PulsarClientException {
        String fullTopic = String.format(PULSAR_TOPIC_FORMAT, ibongAdarnaTenant, ibongAdarnaNamespace, ibongAdarnaBetTopic);
        String deadLetterTopic = String.format(PULSAR_TOPIC_FORMAT, ibongAdarnaTenant, ibongAdarnaNamespace, ibongAdarnaBetDeadLetterTopic);
        return ibongadarnaPulsarClient.newConsumer()
                .topic(fullTopic)
                .subscriptionName(ibongAdarnaTenant)
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
                .subscribe();
    }

    @Bean(name = "ibongadarnaDeadLetterConsumer", destroyMethod = "close")
    public Consumer<byte[]> ibongadarnaDeadLetterConsumer(@Autowired PulsarClient ibongadarnaPulsarClient) throws PulsarClientException {
        String deadLetterTopic = String.format(PULSAR_TOPIC_FORMAT, ibongAdarnaTenant, ibongAdarnaNamespace, ibongAdarnaBetDeadLetterTopic);
        return ibongadarnaPulsarClient.newConsumer(Schema.BYTES)
                .topic(deadLetterTopic)
                .subscriptionName(ibongAdarnaTenant + "_deadLetter")
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
