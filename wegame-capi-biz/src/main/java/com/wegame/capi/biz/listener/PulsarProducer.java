package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.betRecordThreadPoolExecutor;
import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.sendMessageThreadPoolExecutor;

@Slf4j
@Component
public class PulsarProducer implements InitializingBean {

    private static final String PRODUCER = "Producer";
    @Autowired
    Map<String, Producer<byte[]>> producerCache;

    @Override
    public void afterPropertiesSet() throws Exception {
        Arrays.stream(GameTypeEnum.values()).forEach(gameType -> {
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
        if (duration <= 10) {
            log.info("sendMessage cost time less 10, {} ms", endTime - startTime);
        } else if (duration > 10 && duration <= 20) {
            log.info("sendMessage cost time(10-20], {} ms", endTime - startTime);
        } else if (duration > 20) {
            log.info("sendMessage cost time more than 20, {} ms", endTime - startTime);
        }
        return messageId;
    }

    public <T> MessageId syncSendMessage(GameTypeEnum gameType, MQMessage<T> mqMessage) {
        try {
            log.info("{}Producer syncSendMessage type:{},msgId:{}", gameType.getName(), mqMessage.getType(), mqMessage.getMsgId());
            return getProducer(gameType)
                    .newMessage()
                    .key(mqMessage.getMsgId())
                    .value(JSON.toJSONBytes(mqMessage))
                    .send();
        } catch (PulsarClientException e) {
            log.error("{}Producer send fail, msgId={}", gameType.getName(), mqMessage.getMsgId(), e);
            throw new BusinessException(ErrorCode.ERR_SINGLE_WALLET_PLATFORM, e);
        }
    }

    public <T> CompletableFuture<MessageId> asyncSendMessage(GameTypeEnum gameType, MQMessage<T> mqMessage) {
        log.info("{}Producer asyncSendMessage msgType:{}, msgId:{}", gameType.getName(), mqMessage.getType(), mqMessage.getMsgId());
        long startTime = System.currentTimeMillis();
        CompletableFuture<MessageId> future = getProducer(gameType)
                .newMessage()
                .key(mqMessage.getMsgId())
                .value(JSON.toJSONBytes(mqMessage))
                .sendAsync();
        long endTime = System.currentTimeMillis();
        log.info("asyncSendMessage cost time, {} ms", endTime - startTime);
        return future;
    }

    @Async(sendMessageThreadPoolExecutor)
    public <T> void asyncSendMessageThenRetry(GameTypeEnum gameType, MQMessage<T> mqMessage) {
        log.info("{}Producer asyncSendMessageThenRetry msgType:{}, msgId:{}", gameType.getName(), mqMessage.getType(), mqMessage.getMsgId());
        CompletableFuture<MessageId> future = getProducer(gameType)
                .newMessage()
                .key(mqMessage.getMsgId())
                .value(JSON.toJSONBytes(mqMessage))
                .sendAsync();
        //通过异步回调得知消息发送成功与否
        future.whenComplete(((messageId, throwable) -> {
            if( null != throwable ) {
                log.warn("asyncSendMessageThenRetry msgId:{} fail, then retry", mqMessage.getMsgId(), throwable);
                MessageId id = syncSendMessage(gameType, mqMessage, 100);
                if (Objects.isNull(id)) {
                    log.error("asyncSendMessageThenRetry then syncSendMessage send fail, msgId: {}", mqMessage.getMsgId());
                }
            }
        }));
    }

    private Producer<byte[]> getProducer(GameTypeEnum gameType) {
        Producer<byte[]> producer = producerCache.get(StringUtils.lowerCase(gameType.getName()) + "Producer");
        AssertUtil.assertNotNull(producer, ErrorCode.ERR_INTER);
        return producer;
    }
}
