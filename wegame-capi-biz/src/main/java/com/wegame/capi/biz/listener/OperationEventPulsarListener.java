package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.JSON;
import com.wegame.capi.biz.converter.DynamicOperationConvert;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventMessageDo;
import com.wegame.capi.biz.service.OperationEventService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 干预事件消息监听
 */
@Slf4j
@Service
public class OperationEventPulsarListener implements MessageListener {

    @Autowired
    OperationEventService operationEventService;

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
            GameTypeEnum gameTypeEnum = GameTypeEnum.valueOfId(messageDo.getGameId());
            if( gameTypeEnum ==null){
                log.warn("dynamicOperation msg content error:{}", JSON.toJSONString(messageDo));
                return true;
            }
            if (messageDo.getGameId().equals(GameTypeEnum.MARIA_MAKILING.getId())) {
                DynamicOperationEventDo eventDo = DynamicOperationConvert.messageToDo(messageDo);
                if (eventDo == null) {
                    log.warn("dynamicOperation msg content error:{}", JSON.toJSONString(messageDo));
                    return true;
                }
                operationEventService.add(eventDo, OperationEventTypeEnum.MARIA_MAKILING);
            }
            return true;
        } catch (Exception e) {
            log.error("Message moved acknowledge error:", e);
            return false;
        }
    }
}
