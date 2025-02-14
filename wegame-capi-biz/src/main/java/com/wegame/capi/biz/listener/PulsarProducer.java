package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.GameTypeEnum;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class PulsarProducer {

    @Autowired
    Map<String, Producer<byte[]>> producerCache;

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

            return this.getProducer(gameType)
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
        return getProducer(gameType)
                .newMessage()
                .key(mqMessage.getMsgId())
                .value(JSON.toJSONBytes(mqMessage))
                .sendAsync();
    }

    private Producer<byte[]> getProducer(GameTypeEnum gameType) {
        Producer<byte[]> producer = producerCache.get(StringUtils.lowerCase(gameType.getName()) + "Producer");
        AssertUtil.assertNotNull(producer, ErrorCode.ERR_INTER);
        return producer;
    }
}
