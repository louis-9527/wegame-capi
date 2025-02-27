package com.wegame.capi.test.biz.service;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.service.DynamicOperationEventService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationReportStatusEnum;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DynamicOperationEventServiceTest extends BaseTest {
    @Autowired
    private DynamicOperationEventService dynamicOperationEventService;


    @Test
    public void add(){
        DynamicOperationEventDo eventDo = new DynamicOperationEventDo();
        eventDo.setEventId("1000");
        eventDo.setEventStatus(DynamicOperationEventStatusEnum.NONE);
        eventDo.setGameId(GameTypeEnum.MARIA_MAKILING.getId());
        eventDo.setEventKind(1);
        eventDo.setBonus(1000L);
        eventDo.setChannelId("100");
        eventDo.setCreateTime(DateUtil.getCurrentMilli());
        eventDo.setUpdateTime(DateUtil.getCurrentMilli());
        eventDo.setEffectedTime(DateUtil.getCurrentMilli());
        eventDo.setExpiredTime(1742537902000L);
        eventDo.setReportStatus(DynamicOperationReportStatusEnum.NONE);
        eventDo.setPlayerName("slottest01");
        dynamicOperationEventService.add(eventDo,GameTypeEnum.MARIA_MAKILING);
    }
}
