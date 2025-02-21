package com.wegame.capi.biz.listener;

import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.service.DynamicOperationEventService;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 动态运营消息监听
 */
@Slf4j
@Service
public class DynamicOperationPulsarHandler {
    @Autowired
    private DynamicOperationEventService dynamicOperationEventService;

    public Boolean handleMessage(DynamicOperationEventDo eventDo) {
        try {
            //todo 过滤只属于自己游戏的
            if (eventDo.getOperatorGameId().equals(GameTypeEnum.MARIA_MAKILING.getId())) {
                dynamicOperationEventService.add(eventDo, GameTypeEnum.MARIA_MAKILING);
            }
            return true;
        } catch (Exception e) {
            log.error("Message moved acknowledge error:", e);
            return false;
        }
    }


}
