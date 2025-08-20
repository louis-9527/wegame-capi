package com.wegame.capi.test.biz.listener;

import com.wegame.capi.biz.domain.biz.operation.OperationEventMessageDo;
import com.wegame.capi.biz.listener.operation.DiwataMayariOperationEventPulsarListener;
import com.wegame.capi.biz.listener.operation.MariamakilingOperationEventPulsarListener;
import com.wegame.capi.biz.listener.operation.SirenaOperationEventPulsarListener;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.OperationEventKindEnum;
import com.wegame.capi.test.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class OperationEventPulsarListenerTest extends BaseTest {

    @Autowired
    SirenaOperationEventPulsarListener sirenaOperationEventPulsarListener;

    @Test
    public void test_handleMessage() {
        for (int i = 0; i < 1; i++) {
            boolean ret = sirenaOperationEventPulsarListener.handleMessage(buildMessageDo());
            Assert.assertTrue(ret);
        }
    }

    private OperationEventMessageDo buildMessageDo() {
        return OperationEventMessageDo.builder()
                .playerName("slottest01")
                .bonus(new BigDecimal(100))
                .eventId("2-0692-4dc5-9f08-4a383b5d311a2")
                .eventKind(OperationEventKindEnum.NORMAL_URGENT.getCode())
                .gameId(GameTypeEnum.SIRENA.getId())
                .operatorGameId("d596da1e-81a7-4010-9d22-749b9c30c69c")
                .channelId("1050")
                .strategyId("3")
                .orderNo("Sss123")
                .effectedTime("2025-07-07T07:31:13.357Z")
                .settlementTime("2025-07-27T07:31:10.000Z")
                .timesOrSessionDuration(new BigDecimal(10))
                .build();
    }
}
