package com.wegame.capi.biz.listener.operation;

import com.alibaba.fastjson.JSON;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventMessageDo;
import com.wegame.capi.biz.service.operation.OperationEventService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.infrastructure.config.properties.GameApiProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.wegame.capi.biz.converter.operation.OperationEventConvert.messageToDo;
import static com.wegame.capi.common.Constants.BUILD_BEAN_FORMAT;

/**
 * 干预事件消息监听
 *
 * 核销逻辑：监听干预消息，然后落干预事件到mongodb
 * db中 eventId 为消息id，防止重复消费
 */
@Slf4j
@Service
public abstract class AbstractOperationEventPulsarListener implements MessageListener<byte[]>, InitializingBean {

    @Autowired
    OperationEventService operationEventService;
    @Autowired
    SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private GameApiProperty gameApiProperty;

    @Override
    public void afterPropertiesSet() {
        log.info(BUILD_BEAN_FORMAT, getClass().getSimpleName());
    }

    @Override
    public void received(Consumer<byte[]> consumer, Message<byte[]> msg) {
        String msgStr = new String(msg.getData());
        log.info("received operation event msg: {}", msgStr);
        OperationEventMessageDo messageDo = JSON.parseObject(msgStr, OperationEventMessageDo.class);
        boolean result = handleMessage(messageDo);
        if (result) {
            try {
                consumer.acknowledge(msg);
            } catch (Exception e) {
                log.error("operation event msg ack error, errMsg: {}", e.getMessage(), e);
            }
        }
    }

    public boolean handleMessage(OperationEventMessageDo messageDo) {
        try {
            // 只处理本渠道的消息
            if (!gameApiProperty.getChannelIdAll().contains(messageDo.getChannelId())) {
                return true;
            }
            GameTypeEnum gameType = GameTypeEnum.valueOfId(messageDo.getGameId());
            if (Objects.isNull(gameType) || gameType != this.gameType()) {
                log.warn("handleMessage err, un support gameType: {}", messageDo);
                return true;
            }
            OperationEventDo eventDo = messageToDo(snowflakeIdGenerator.nextId(), messageDo);
            if (Objects.isNull(eventDo)) {
                log.warn("handleMessage err, convert err: {}", messageDo);
                return true;
            }
            OperationEventTypeEnum operationEventType = OperationEventTypeEnum.valueOfGameType(gameType);
            if (Objects.isNull(operationEventType)) {
                log.warn("handleMessage err, un support operationEventType, errMsg: {}", messageDo);
                return true;
            }
            operationEventService.add(eventDo, operationEventType);
            return true;
        } catch (Exception e) {
            log.error("handleMessage error, errMsg: {}", e.getMessage(), e);
            return false;
        }
    }

    public abstract GameTypeEnum gameType();
}
