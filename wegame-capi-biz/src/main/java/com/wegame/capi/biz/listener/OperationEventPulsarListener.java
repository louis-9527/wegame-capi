package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.JSON;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.capi.biz.converter.operation.OperationEventConvert;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventMessageDo;
import com.wegame.capi.biz.service.operation.OperationEventService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 干预事件消息监听
 */
@Slf4j
@Service
public class OperationEventPulsarListener implements MessageListener {

    @Value("${api.game.cp.channelId}")
    String cpChannelId;
    @Autowired
    OperationEventService operationEventService;
    @Autowired
    SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public void received(Consumer consumer, Message msg) {
        String msgStr = new String(msg.getData());
        log.info("accept operation event msg:{}", msgStr);
        OperationEventMessageDo messageDo = JSON.parseObject(msgStr, OperationEventMessageDo.class);
        boolean result = handleMessage(messageDo);
        if (result) {
            try {
                consumer.acknowledge(msg);
            } catch (Exception e) {
                log.error("operation event msg ack error:", e);
            }
        }
    }

    public boolean handleMessage(OperationEventMessageDo messageDo) {
        try {
            if (!Objects.equals(cpChannelId, messageDo.getChannelId())) {
                return true;
            }
            GameTypeEnum gameTypeEnum = GameTypeEnum.valueOfId(messageDo.getGameId());
            if (gameTypeEnum == null) {
                log.warn("dynamicOperation msg content error:{}", JSON.toJSONString(messageDo));
                return true;
            }
            OperationEventDo eventDo = OperationEventConvert.messageToDo(snowflakeIdGenerator.nextId(), messageDo);
            if (eventDo == null) {
                log.warn("dynamicOperation msg content error:{}", JSON.toJSONString(messageDo));
                return true;
            }
            OperationEventTypeEnum operationEventType = OperationEventTypeEnum.valueOfGameType(gameTypeEnum);
            if(operationEventType ==null){
                log.warn("dynamicOperation operationEventType error error:{}", JSON.toJSONString(messageDo));
                return true;
            }
            operationEventService.add(eventDo, operationEventType);
            return true;
        } catch (Exception e) {
            log.error("Message moved acknowledge error:", e);
            return false;
        }
    }
}
