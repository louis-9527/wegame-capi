package com.wegame.capi.test.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationMsgDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.listener.DynamicOperationPulsarHandler;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DynamicOperationPulsarHandlerTest extends BaseTest {
    @Autowired
    private DynamicOperationPulsarHandler dynamicOperationPulsarHandler;

    @Test
    public void handleMessage(){

        String msgDoStr = "{\n" +
                "    \"playerName\": \"slottest01\",\n" +
                "    \"bonus\": 100.001,\n" +
                "    \"eventId\": \"jsbs323\",\n" +
                "    \"eventKind\": 1,\n" +
                "    \"gameId\": \"ssssjsbs323\",\n" +
                "    \"operatorGameId\": \"zhumeng001\",\n" +
                "    \"channelId\": \"1050\",\n" +
                "    \"strategyId\": 1,\n" +
                "    \"orderNo\": \"Sss123\",\n" +
                "    \"effectedTime\": \"2024-12-01T15:00:00.000Z\",\n" +
                "    \"timesOrSessionDuration\": 10\n" +
                "}";
        DynamicOperationMsgDo msgDo = JSON.parseObject(msgDoStr, DynamicOperationMsgDo.class);
        dynamicOperationPulsarHandler.handleMessage(msgDo);
    }


    public static void main(String[] args) {
        String dd ="[{\"index\":0,\"symbolArr\":[[{\"_id\":\"3001\",\"name\":\"Scatter\",\"symbolType\":\"SCATTER\"},{\"_id\":\"1002\",\"name\":\"Symbol1002\",\"symbolType\":\"COMMON\"},{\"_id\":\"3001\",\"name\":\"Scatter\",\"symbolType\":\"SCATTER\"},{\"_id\":\"1002\",\"name\":\"Symbol1002\",\"symbolType\":\"COMMON\"},{\"_id\":\"1004\",\"name\":\"Symbol1004\",\"symbolType\":\"COMMON\"}]]}]";


        List<WheelDo<SymbolDo>> wheelDos = JSON.parseObject(dd, new TypeReference<List<WheelDo<SymbolDo>>>() {
        });
        System.out.println(wheelDos);
    }
}
