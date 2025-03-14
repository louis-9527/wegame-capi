package com.wegame.capi.biz.listener;

import com.alibaba.fastjson.JSON;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationMsgDo;
import com.wegame.capi.biz.service.DynamicOperationEventService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.DynamicOperationGameEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.DynamicOperationEventKindEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationReportStatusEnum;
import com.wegame.capi.common.utils.AmountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
                DynamicOperationEventDo eventDo = this.convertEventDo(msgDo);
                if (eventDo == null) {
                    log.warn("dynamicOperation msg content error:{}", JSON.toJSONString(msgDo));
                    return true;
                }
                dynamicOperationEventService.add(eventDo, DynamicOperationGameEnum.MARIA_MAKILING_DYNAMIC);
            }
            return true;
        } catch (Exception e) {
            log.error("Message moved acknowledge error:", e);
            return false;
        }
    }

    private DynamicOperationEventDo convertEventDo(DynamicOperationMsgDo msgDo) {
        long currentMilli = DateUtil.getCurrentMilli();
        DynamicOperationEventKindEnum eventKindEnum = DynamicOperationEventKindEnum.valueOfCode(msgDo.getEventKind());
        if (eventKindEnum == null) {
            return null;
        }
        BigDecimal bonus = msgDo.getBonus();
        BigDecimal minBonus = bonus.multiply(new BigDecimal("0.9"));
        DynamicOperationEventDo eventDo = new DynamicOperationEventDo();
        eventDo.setPlayerName(msgDo.getPlayerName());
        eventDo.setBonus(AmountUtil.toLong(bonus));
        eventDo.setMinBonus(AmountUtil.toLong(minBonus));
        eventDo.setMaxBonus(AmountUtil.toLong(bonus));
        eventDo.setFinishBonus(0L);
        eventDo.setEventStatus(DynamicOperationEventStatusEnum.NONE);
        eventDo.setReportStatus(DynamicOperationReportStatusEnum.NONE);
        eventDo.setEventId(msgDo.getEventId());
        eventDo.setEventKind(eventKindEnum);
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
