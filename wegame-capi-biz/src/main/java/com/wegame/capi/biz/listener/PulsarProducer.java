package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.infrastructure.config.properties.GameProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.sendMessageThreadPoolExecutor;

@Slf4j
@Component
public class PulsarProducer implements InitializingBean {

    private static final String PRODUCER = "Producer";
    @Autowired
    GameProperty gameProperty;
    @Autowired
    Map<String, Producer<byte[]>> producerCache;

    @Override
    public void afterPropertiesSet() {
        gameProperty.enabledGameTypes().forEach(gameType -> {
            Producer<byte[]> producer = producerCache.get(StringUtils.lowerCase(gameType.getName()) + PRODUCER);
            AssertUtil.assertNotNull(producer, ErrorCode.ERR_INTER);
        });
    }

    public <T> MessageId syncSendMessage(GameTypeEnum gameTypeEnum, MQMessage<T> mqMessage, int tryTimes) {
        long startTime = System.currentTimeMillis();
        MessageId messageId;
        int times = 0;
        do {
            messageId = syncSendMessage(gameTypeEnum, mqMessage);
            times++;
        } while (Objects.isNull(messageId) && times < tryTimes);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        if (duration > 20L) {
            log.warn("sendMessage cost time more than 20, {} ms", endTime - startTime);
        }
        return messageId;
    }

    public <T> MessageId syncSendMessage(GameTypeEnum gameType, MQMessage<T> mqMessage) {
        try {
            // log.info("{}Producer syncSendMessage type:{},msgId:{}", gameType.getName(), mqMessage.getType(), mqMessage.getMsgId());
            return getProducer(gameType)
                    .newMessage()
                    .key(mqMessage.getMsgId())
                    .value(JSON.toJSONBytes(mqMessage, SerializerFeature.DisableCircularReferenceDetect))
                    .send();
        } catch (PulsarClientException e) {
            log.error("{}Producer send fail, msgId={}", gameType.getName(), mqMessage.getMsgId(), e);
            throw new BusinessException(ErrorCode.ERR_SINGLE_WALLET_PLATFORM, e);
        }
    }

    public <T> CompletableFuture<MessageId> asyncSendMessage(GameTypeEnum gameType, MQMessage<T> mqMessage) {
        // log.info("{}Producer asyncSendMessage msgType:{}, msgId:{}", gameType.getName(), mqMessage.getType(), mqMessage.getMsgId());
        long startTime = System.currentTimeMillis();
        CompletableFuture<MessageId> future = getProducer(gameType)
                .newMessage()
                .key(mqMessage.getMsgId())
                .value(JSON.toJSONBytes(mqMessage, SerializerFeature.DisableCircularReferenceDetect))
                .sendAsync();
        long endTime = System.currentTimeMillis();
        long cost = endTime - startTime;
        if (cost >= 50) {
            log.info("asyncSendMessage cost time: more than 50, {} ms", endTime - startTime);
        }
        return future;
    }

    @Async(sendMessageThreadPoolExecutor)
    public <T> void asyncSendMessageThenRetry(GameTypeEnum gameType, MQMessage<T> mqMessage) {
        // log.info("{}Producer asyncSendMessageThenRetry msgType:{}, msgId:{}", gameType.getName(), mqMessage.getType(), mqMessage.getMsgId());
        CompletableFuture<MessageId> future = getProducer(gameType)
                .newMessage()
                .key(mqMessage.getMsgId())
                .value(JSON.toJSONBytes(mqMessage, SerializerFeature.DisableCircularReferenceDetect))
                .sendAsync();
        // 通过异步回调得知消息发送成功与否
        future.whenComplete(((messageId, throwable) -> {
            if (Objects.nonNull(throwable)) {
                log.warn("asyncSendMessageThenRetry msgId:{} fail, then retry", mqMessage.getMsgId(), throwable);
                MessageId id = syncSendMessage(gameType, mqMessage, 100);
                if (Objects.isNull(id)) {
                    log.error("asyncSendMessageThenRetry then syncSendMessage send fail, msgId: {}", mqMessage.getMsgId());
                }
            }
        }));
    }

    private Producer<byte[]> getProducer(GameTypeEnum gameType) {
        return producerCache.get(StringUtils.lowerCase(gameType.getName()) + PRODUCER);
    }
}
