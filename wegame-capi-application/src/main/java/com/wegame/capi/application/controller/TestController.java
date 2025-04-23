package com.wegame.capi.application.controller;

import com.alibaba.fastjson.JSON;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.domain.biz.operation.OperationEventMessageDo;
import com.wegame.capi.biz.listener.IbongOperationEventPulsarListener;
import com.wegame.capi.biz.listener.MariamakilingOperationEventPulsarListener;
import com.wegame.capi.biz.service.promotion.FreeSpinService;
import com.wegame.capi.client.domain.request.promotion.CreateFreeSpinRequest;
import com.wegame.capi.client.domain.vo.promotion.CreateFreeSpinVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.wegame.base.domain.CommonResponse.build;
import static com.wegame.capi.biz.converter.promotion.CreateFreeSpinConverter.toVo;


@Slf4j
@RestController
@RequestMapping("test")
@Api(value = "测试模块", tags = {"测试模块"})
public class TestController {

    @Autowired
    MariamakilingOperationEventPulsarListener mariamakilingOperationEventPulsarListener;
    @Autowired
    IbongOperationEventPulsarListener ibongOperationEventPulsarListener;
    @Autowired
    FreeSpinService freeSpinService;

    @ApiOperation(value = "eventMsg", httpMethod = "POST")
    @PostMapping("/event/msg")
    public CommonResponse<Boolean> handleEventMessage(@RequestBody String event) {

        String msgDoStr = "{\n" +
                "    \"playerName\": \"slottest01\",\n" +
                "    \"bonus\": 100,\n" +
                "    \"eventId\": \"2-0692-4dc5-9f08-4a383b5d311a\",\n" +
                "    \"eventKind\": 1,\n" +
                "    \"gameId\": \"zhumeng001\",\n" +
                "    \"operatorGameId\": \"d596da1e-81a7-4010-9d22-749b9c30c69c\",\n" +
                "    \"channelId\": \"1050\",\n" +
                "    \"strategyId\": 3,\n" +
                "    \"orderNo\": \"Sss123\",\n" +
                "    \"effectedTime\": \"2025-02-27T07:31:13.357Z\",\n" +
                "    \"settlementTime\": \"2025-02-27T07:31:10.000Z\",\n" +
                "    \"timesOrSessionDuration\": 10\n" +
                "}";
        OperationEventMessageDo msgDo = JSON.parseObject(event, OperationEventMessageDo.class);
        if (msgDo.getGameId().equals(GameTypeEnum.MARIA_MAKILING.getId())) {
            mariamakilingOperationEventPulsarListener.handleMessage(msgDo);
        } else if (msgDo.getGameId().equals(GameTypeEnum.IBONG_ADARNA.getId())) {
            ibongOperationEventPulsarListener.handleMessage(msgDo);
        }

//        for (int i = 0; i <= 210; i++) {
//            msgDo.setEventId(msgDo.getEventId() + "_" + i);
//            mariamakilingOperationEventPulsarListener.handleMessage(msgDo);
//        }
        return CommonResponse.build(true);
    }

    @ApiOperation(value = "createFreeSpin", httpMethod = "POST")
    @PostMapping("/createFreeSpin")
    public CommonResponse<CreateFreeSpinVo> createFreeSpin(@RequestBody CreateFreeSpinRequest request) {
        request.validate();
        return build(toVo(freeSpinService.createFreeSpin(request)));
    }
}