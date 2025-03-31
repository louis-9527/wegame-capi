package com.wegame.capi.test.biz.listener;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.capi.biz.domain.biz.operation.OperationEventMessageDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.OperationEventKindEnum;

import java.math.BigDecimal;

public class OperationEventPulsarListenerTestMocker {
    public static OperationEventMessageDo mockOperationEventMessageDo(GameTypeEnum gametype, String eventId) {
        switch (gametype) {
            case MARIA_MAKILING:
                return buildMessageDoForMariaMakiLing(eventId);
            case IBONG_ADARNA:
                return buildMessageDoForIbong(eventId);
            default:
                throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT");
        }
    }

    private static OperationEventMessageDo buildMessageDoForMariaMakiLing(String eventId) {
        return OperationEventMessageDo.builder()
                .playerName("slottest01")
                .bonus(new BigDecimal(100))
                .eventId(eventId)
                .eventKind(OperationEventKindEnum.NORMAL_URGENT.getCode())
                .gameId(GameTypeEnum.MARIA_MAKILING.getId())
                .operatorGameId("d596da1e-81a7-4010-9d22-749b9c30c69c")
                .channelId("1050")
                .strategyId("3")
                .orderNo("Sss123")
                .effectedTime("2025-02-27T07:31:13.357Z")
                .settlementTime("2025-02-27T07:31:10.000Z")
                .timesOrSessionDuration(new BigDecimal(10))
                .build();
    }

    private static OperationEventMessageDo buildMessageDoForIbong(String eventId) {
        return OperationEventMessageDo.builder()
                .playerName("slottest01")
                .bonus(new BigDecimal(1000))
                .eventId(eventId)
                .eventKind(OperationEventKindEnum.NORMAL_URGENT.getCode())
                .gameId(GameTypeEnum.IBONG_ADARNA.getId())
                .operatorGameId("d596da1e-81a7-4010-9d22-749b9c30c69c")
                .channelId("1050")
                .strategyId("3")
                .orderNo("Sss123")
                .effectedTime("2025-04-05T07:31:13.357Z")
                .settlementTime("2025-04-05T07:31:10.000Z")
                .timesOrSessionDuration(new BigDecimal(10))
                .build();
    }


}
