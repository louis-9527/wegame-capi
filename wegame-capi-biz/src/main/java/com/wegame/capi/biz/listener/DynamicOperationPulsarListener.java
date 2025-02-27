package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.JSON;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationMsgDo;
import com.wegame.capi.biz.service.DynamicOperationEventService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 动态运营消息监听
 */
@Slf4j
@Service
public class DynamicOperationPulsarListener implements MessageListener {

    @Autowired
    DynamicOperationEventService dynamicOperationEventService;

    @Override
    public void received(Consumer consumer, Message msg) {
        String msgData = new String(msg.getData());
        log.info("accept dynamicOperation msg:{}", msgData);
        DynamicOperationMsgDo dynamicOperationMsgDo = JSON.parseObject(msgData, DynamicOperationMsgDo.class);
        Boolean result = handleMessage(dynamicOperationMsgDo);
        if (result) {
            try {
                consumer.acknowledge(msg);
            } catch (Exception e) {
                log.error("dynamicOperation msg ack error:", e);
            }
        }
    }

    public Boolean handleMessage(DynamicOperationMsgDo msgDo) {
        try {
            if (msgDo.getGameId().equals(GameTypeEnum.MARIA_MAKILING.getId())) {
                Long add = dynamicOperationEventService.add(this.convertEventDo(msgDo), GameTypeEnum.MARIA_MAKILING);
                if (add == null) {
                    return true;
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Message moved acknowledge error:", e);
            return false;
        }
    }

    private DynamicOperationEventDo convertEventDo(DynamicOperationMsgDo msgDo) {
        long currentMilli = DateUtil.getCurrentMilli();
        DynamicOperationEventDo eventDo = new DynamicOperationEventDo();
        eventDo.setPlayerName(msgDo.getPlayerName());
        eventDo.setBonus(AmountUtil.toLong(msgDo.getBonus()));
        eventDo.setEventId(msgDo.getEventId());
        eventDo.setEventKind(msgDo.getEventKind());
        eventDo.setGameId(msgDo.getGameId());
        eventDo.setChannelId(msgDo.getChannelId());
        eventDo.setEffectedTime(DateUtil.utcStringToEpochMilli(msgDo.getEffectedTime()));
        eventDo.setExpiredTime(msgDo.getExpiredTime() == null ? DateUtil.getMaxMilli() : DateUtil.utcStringToEpochMilli(msgDo.getExpiredTime()));
        eventDo.setEventMsgDetail(JSON.toJSONString(msgDo));
        eventDo.setCreateTime(currentMilli);
        eventDo.setUpdateTime(currentMilli);
        return eventDo;
    }
}
