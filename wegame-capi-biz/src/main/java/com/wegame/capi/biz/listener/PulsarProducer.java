package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.JSON;
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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

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
            if (times > 0) {
                log.warn("syncSendMessage retry times:{}, messageId: {}", times, mqMessage.getMsgId());
            }
            times++;
        } while (Objects.isNull(messageId) && times < tryTimes);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        if (duration > 20L) {
            log.warn("sendMessage cost time more than 20, {} ms, messageId: {}", endTime - startTime, mqMessage.getMsgId());
        }
        return messageId;
    }

    public <T> MessageId syncSendMessage(GameTypeEnum gameType, MQMessage<T> mqMessage) {
        try {
            // todo 去掉日志
            log.info("syncSendMessage messageId: {}", mqMessage.getMsgId());
            return getProducer(gameType)
                    .newMessage()
                    .key(mqMessage.getMsgId())
                    .value(JSON.toJSONBytes(mqMessage))
                    .send();
        } catch (Exception e) {
            log.error("{}Producer syncSendMessage fail, msgId={}", gameType.getName(), mqMessage.getMsgId(), e);
            throw new BusinessException(ErrorCode.ERR_SINGLE_WALLET_PLATFORM, e);
        }
    }

    @Async(sendMessageThreadPoolExecutor)
    public <T> void asyncSendMessage(GameTypeEnum gameType, MQMessage<T> mqMessage) {
        // todo 去掉日志
        log.info("{}Producer asyncSendMessageThenRetry msgType:{}, msgId:{}", gameType.getName(), mqMessage.getType(), mqMessage.getMsgId());
        MessageId id = syncSendMessage(gameType, mqMessage, 100);
        if (Objects.isNull(id)) {
            log.error("asyncSendMessage send fail, mqMessage: {}", mqMessage);
        }
    }

    private Producer<byte[]> getProducer(GameTypeEnum gameType) {
        return producerCache.get(StringUtils.lowerCase(gameType.getName()) + PRODUCER);
    }
}
