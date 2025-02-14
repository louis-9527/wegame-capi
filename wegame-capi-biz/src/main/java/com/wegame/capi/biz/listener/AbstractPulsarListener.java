package com.wegame.capi.biz.listener;

import cn.hutool.core.lang.UUID;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public abstract class AbstractPulsarListener {
    @Value("${spring.application.env}")
    String env;
    @Value("${spring.application.name}")
    String name;
    @Autowired
    PulsarClient pulsarClient;
    private Consumer<byte[]> consumer;
    private Consumer<byte[]> dlqConsumer;

    protected void initConsumer(GameTypeEnum gameType) {
        try {
            String fullTopic = String.format("persistent://%s/%s/%s", name + "_" + gameType.getName(), env, gameType.getName() + "_topic");
            String deadLetterTopic = String.format("persistent://%s/%s/%s", name + "_" + gameType.getName(), env, gameType.getName() + "_deadLetter" + "_topic");
            consumer = pulsarClient.newConsumer()
                    .topic(fullTopic)
                    .subscriptionName(name + "_" + gameType.getName())
                    .subscriptionType(SubscriptionType.Key_Shared) // 关键点：使用 Key_Shared 模式
                    .receiverQueueSize(2000) // 增加内部缓存，提高吞吐量
                    .ackTimeout(60, TimeUnit.SECONDS)
                    .batchReceivePolicy(BatchReceivePolicy.builder()
                            .maxNumMessages(200)  // 批量最多100条
                            .timeout(100, TimeUnit.MILLISECONDS)
                            .build())
                    .deadLetterPolicy(DeadLetterPolicy.builder()
                            .maxRedeliverCount(15)  // **最多重试 10 次**
                            .deadLetterTopic(deadLetterTopic) // **死信队列**
                            .build())
                    // .keySharedPolicy(KeySharedPolicy.stickyHashRange())
                    .subscribe();
            new Thread(this::consumeMessages).start();
            log.info("✅ Pulsar consumer started for topic: {}", fullTopic);

            // 监听死信队列
            dlqConsumer = pulsarClient.newConsumer(Schema.BYTES)
                    .topic(deadLetterTopic)
                    .subscriptionName(name + "_" + gameType.getName() + "_deadLetter")
                    .subscriptionType(SubscriptionType.Shared)
                    .messageListener((consumer, msg) -> {
                        String message = new String(msg.getData());
                        log.warn("Message moved to DLQ, consumerName: {}", consumer.getConsumerName());
                        try {
                            consumer.acknowledge(msg);
                        } catch (Exception e) {
                            log.error("Message moved acknowledge error:", e);
                        }
                    }).subscribe();
        } catch (PulsarClientException e) {
            log.error("Failed to initialize Pulsar consumer", e);
            throw new RuntimeException("Failed to initialize Pulsar consumer", e);
        }
    }

    private void consumeMessages() {
        while (true) {
            try {
                MDC.put(ThreadMdcUtil.TRACE_ID, UUID.randomUUID().toString(true));
                Messages<byte[]> messages = consumer.batchReceive();
                if (messages == null || messages.size() == 0) {
                    continue;
                }
                List<MessageId> successMsgIds = processMessages(messages);
                if (!successMsgIds.isEmpty()) {
                    consumer.acknowledge(successMsgIds);
                }
            } catch (Exception e) {
                log.error("Error consuming messages:", e);
            } finally {
                MDC.clear();
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
            if (dlqConsumer != null) {
                dlqConsumer.close();
            }
        } catch (PulsarClientException e) {
            log.error("Error closing Pulsar consumer", e);
        }
    }
}
