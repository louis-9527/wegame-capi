package com.wegame.capi.biz.config.pulsar.jackpot;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

import static com.wegame.base.PulsarConstants.PULSAR_TOPIC_FORMAT;
import static com.wegame.capi.common.Constants.*;

@Slf4j
@Configuration
@ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
public class LamangJackPotPulsarConfig {

    @Value("${pulsar.dynamic.lamang.url}")
    String lamangUrl;
    @Value("${pulsar.dynamic.lamang.tenant}")
    String lamangTenant;
    @Value("${pulsar.dynamic.lamang.namespace}")
    String lamangNamespace;
    @Value("${pulsar.dynamic.lamang.bet_topic}")
    String lamangBetTopic;

    @Bean(name = "lamangJackPotPulsarClient", destroyMethod = "close")
    public PulsarClient lamangJackPotPulsarClient() throws Exception {
        //                .operationTimeout(100, TimeUnit.MILLISECONDS)
        // 10分钟打印一次统计信息
        return PulsarClient.builder()
                .serviceUrl(lamangUrl)
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
    }


    @Bean(name = "lamangJackPotPulsarConsumer", destroyMethod = "close")
    public Consumer<byte[]> lamangJackPotPulsarConsumer(@Autowired PulsarClient lamangJackPotPulsarClient) throws PulsarClientException {
        String fullTopic = String.format(PULSAR_TOPIC_FORMAT, lamangTenant, lamangNamespace, lamangBetTopic);
        return lamangJackPotPulsarClient.newConsumer()
                .topic(fullTopic)
                .subscriptionName(lamangTenant+"_jackpot")
                // 关键点：使用 Key_Shared 模式, 集群消费
                .subscriptionType(SubscriptionType.Shared)
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
                        .build())
                // .keySharedPolicy(KeySharedPolicy.stickyHashRange())
                .autoUpdatePartitions(true)
                .autoUpdatePartitionsInterval(600, TimeUnit.SECONDS)
                .subscribe();
    }
}
