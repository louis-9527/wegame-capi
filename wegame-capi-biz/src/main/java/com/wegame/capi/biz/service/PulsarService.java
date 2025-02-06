package com.wegame.capi.biz.service;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.rocketmq.MQMessage;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.client.enums.BetOperationTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PulsarService {

    private  Producer<byte[]> producer;

    @Autowired
    public PulsarService(Producer<byte[]> producer) {
        this.producer = producer;
    }
    /**
     * 按Key发送,保证同一个key局部有序
     *
     * @param message
     * @throws PulsarClientException
     */
    public <T> void sendMessage(String betId, BetOperationTypeEnum betOperationType, T message) {
        MQMessage<T> mqMessage = MQMessage.buildMsg(betId, betOperationType.getCode(), message);
        try {
            producer.newMessage()
                    .key(betId) // 关键点：设置 Key
                    .value(JSON.toJSONBytes(mqMessage))
                    .send();
        } catch (PulsarClientException e) {
            log.error("mariamakilingProducer send fail, msg={}", FastJsonUtils.toJSONString(mqMessage));
            throw new BusinessException(ErrorCode.ERR_SINGLE_WALLET_PLATFORM, e);
        }
    }
}
