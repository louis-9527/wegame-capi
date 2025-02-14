package com.wegame.capi.infrastructure.config;

import com.wegame.base.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author apple
 */
@Slf4j
@Configuration
public class PulsarConfig {

    @Value("${pulsar.dynamic.maria_makiling.url}")
    private String mariaMakilingUrl;
    @Value("${pulsar.dynamic.maria_makiling.tenant}")
    private String mariaMakilingTenant;

    @Value("${pulsar.dynamic.maria_makiling.namespace}")
    private String mariaMakilingNamespace;

    @Value("${pulsar.dynamic.maria_makiling.bet_topic}")
    private String mariaMakilingBetTopic;


    @Bean(name = "mariaMakilingPulsarClient", destroyMethod = "close")
    public PulsarClient mariaMakilingPulsarClient() throws Exception {
        return PulsarClient.builder()
                .serviceUrl(mariaMakilingUrl)
                .build();
    }

    @Bean(name = "mariamakilingProducer", destroyMethod = "close")
    public Producer<byte[]> mariaMakilingProducer(PulsarClient mariaMakilingPulsarClient) throws PulsarClientException {
        String topicName = String.format("persistent://%s/%s/%s", mariaMakilingTenant, mariaMakilingNamespace, mariaMakilingBetTopic);
        String producerName = mariaMakilingTenant + "_" + UUID.randomUUID() + "_" + IpUtil.getLocalIp();
        return mariaMakilingPulsarClient.newProducer(Schema.BYTES)
                .topic(topicName)
                .producerName(producerName)
                .enableBatching(true) // 开启批量发送
                .batchingMaxMessages(200) // 每批最大 200 条消息
                .batchingMaxPublishDelay(10, TimeUnit.MILLISECONDS) // 10ms 内合并批量消息
                .sendTimeout(5, TimeUnit.SECONDS) // 5 秒超时
                .compressionType(CompressionType.LZ4) // 使用 LZ4 压缩，减少带宽占用
                .create();
    }


}
