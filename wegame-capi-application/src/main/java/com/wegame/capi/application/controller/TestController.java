//package com.wegame.capi.application.controller;
//
//import com.alibaba.fastjson.JSON;
//import com.wegame.base.domain.CommonResponse;
//import com.wegame.base.utils.LarkUtil;
//import com.wegame.capi.api.SingleWalletApi;
//import com.wegame.capi.biz.config.domain.MalakasConfig;
//import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
//import com.wegame.capi.biz.domain.biz.operation.OperationEventMessageDo;
//import com.wegame.capi.biz.listener.operation.MariamakilingOperationEventPulsarListener;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletResponse;
//
//
//@Slf4j
//@RestController
//@RequestMapping("test")
//@Api(value = "测试模块", tags = {"测试模块"})
//public class TestController {
//
////    @Autowired
////    MariamakilingOperationEventPulsarListener mariamakilingOperationEventPulsarListener;
////
////    @ApiOperation(value = "eventMsg", httpMethod = "POST")
////    @PostMapping("/event/msg")
////    public CommonResponse<Boolean> handleEventMessage(@RequestBody String event) {
////
////        String msgDoStr = "{\n" +
////                "    \"playerName\": \"slottest01\",\n" +
////                "    \"bonus\": 100,\n" +
////                "    \"eventId\": \"2-0692-4dc5-9f08-4a383b5d311a\",\n" +
////                "    \"eventKind\": 1,\n" +
////                "    \"gameId\": \"zhumeng001\",\n" +
////                "    \"operatorGameId\": \"d596da1e-81a7-4010-9d22-749b9c30c69c\",\n" +
////                "    \"channelId\": \"1050\",\n" +
////                "    \"strategyId\": 3,\n" +
////                "    \"orderNo\": \"Sss123\",\n" +
////                "    \"effectedTime\": \"2025-02-27T07:31:13.357Z\",\n" +
////                "    \"settlementTime\": \"2025-02-27T07:31:10.000Z\",\n" +
////                "    \"timesOrSessionDuration\": 10\n" +
////                "}";
////        OperationEventMessageDo msgDo = JSON.parseObject(event, OperationEventMessageDo.class);
////        mariamakilingOperationEventPulsarListener.handleMessage(msgDo);
////
////
////        //            for (int i = 0; i <= 210; i++) {
////        //                msgDo.setEventId(msgDo.getEventId() + "_" + i);
////        //                mariamakilingOperationEventPulsarListener.handleMessage(msgDo);
////        //            }
////        return CommonResponse.build(true);
////    }
////    @ApiOperation(value = "test/api/ping", httpMethod = "GET")
////    @GetMapping("/test/api/ping")
////    public CommonResponse<Boolean> testApiPing() {
////        //testApi.ping();
////        return CommonResponse.build(true);
////    }
////
////    @Autowired
////    SingleWalletApi singleWalletApi;
////
////    @ApiOperation(value = "test/api/ping", httpMethod = "GET")
////    @GetMapping("/test/singleWalletApi/healthCheck")
////    public CommonResponse<Boolean> testSingleWalletApiHealthCheck() {
////        singleWalletApi.healthCheck();
////        return CommonResponse.build(true);
////    }
//
//    @Autowired
//    MariaMakilingConfig mariaMakilingConfig;
//    @Autowired
//    MalakasConfig malakasConfig;
//
//    @ApiOperation(value = "config", httpMethod = "GET")
//    @GetMapping("/config")
//    public CommonResponse<String> config(HttpServletResponse response) {
//        new Thread(() -> {
//            try {
//                while(true) {
//                    System.out.println("malakasConfig: " + malakasConfig.getRewardMaxMultiple());
//                }
//            } catch (Exception e) {
//                String bootId = "b7a6477c-b829-41d4-acac-599c15aee2dc";
//                LarkUtil.sendTextMsg(bootId, String.format("config test error"));
//                log.error("config test error: " + e.getMessage());
//            }
//        }).start();
//        return CommonResponse.build("pong");
//    }
//
//}