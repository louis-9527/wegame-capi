package com.wegame.capi.biz.listener;

import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public abstract class AbstractPulsarListener {
    @Autowired
    PulsarClient pulsarClient;
    @Value("${pulsar.tenant}")
    String tenant;
    @Value("${pulsar.namespaces}")
    String namespace;

    private Consumer<byte[]> consumer;

    protected void initConsumer(GameTypeEnum gameType) {
        try {
            String fullTopic = String.format("persistent://%s/%s/%s", tenant, namespace, gameType.getName() + "_topic");
            String deadLetterTopic = String.format("persistent://%s/%s/%s", tenant, namespace, gameType.getName() + "deadLetter" + "_topic");
            consumer = pulsarClient.newConsumer()
                    .topic(fullTopic)
                    .subscriptionName(gameType.getName())
                    .subscriptionType(SubscriptionType.Key_Shared) // 关键点：使用 Key_Shared 模式
                    .receiverQueueSize(1000) // 增加内部缓存，提高吞吐量
                    .ackTimeout(60, TimeUnit.SECONDS)
                    .batchReceivePolicy(BatchReceivePolicy.builder()
                            .maxNumMessages(100)  // 批量最多100条
                            .timeout(5000, TimeUnit.MILLISECONDS)
                            .build())
                    .deadLetterPolicy(DeadLetterPolicy.builder()
                            .maxRedeliverCount(20)  // **最多重试 3 次**
                            .deadLetterTopic(deadLetterTopic) // **死信队列**
                            .build())
                    .subscribe();
            new Thread(this::consumeMessages).start();
            log.info("✅ Pulsar consumer started for topic: {}", fullTopic);
        } catch (PulsarClientException e) {
            log.error("Failed to initialize Pulsar consumer", e);
            throw new RuntimeException("Failed to initialize Pulsar consumer", e);
        }
    }

    private void consumeMessages() {
        while (true) {
            try {
                Messages<byte[]> messages = consumer.batchReceive();
                if (messages == null || messages.size() == 0) {
                    continue;
                }
                List<MessageId> msgIds = processMessages(messages);
                consumer.acknowledge(msgIds);
            } catch (Exception e) {
                log.error("Error consuming messages:", e);
            }
        }
    }

    /**
     * 处理接收到的消息，子类必须实现
     *
     * @param messages 批量消息
     * @return 处理成功的消息ID列表
     */
    protected abstract List<MessageId> processMessages(Messages<byte[]> messages);


    @PreDestroy
    public void close() {
        try {
            if (consumer != null) {
                consumer.close();
            }
        } catch (PulsarClientException e) {
            log.error("Error closing Pulsar consumer", e);
        }
    }
}
