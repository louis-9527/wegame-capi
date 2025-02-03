package com.wegame.capi.biz.listerner;

import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.client.enums.GameTypeEnum;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PulscarProducer {


    @Autowired
    Producer<String> mariamakilingProducer;

    public boolean send(GameTypeEnum gameType, Object message) throws PulsarClientException {
        // 根据gameType选择producer
        // 发送消息
        MessageId messageId = mariamakilingProducer.send(FastJsonUtils.toJSONString(message));
        return Objects.nonNull(messageId);
    }

}
