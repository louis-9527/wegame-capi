//package com.wegame.capi.test.application.controller;
//
//import com.alibaba.fastjson.JSON;
//import com.wegame.base.utils.DateUtil;
//import com.wegame.capi.application.controller.platform.DynamicOperationCallbackController;
//import com.wegame.capi.client.domain.request.query.GetStatusOfEventEncryptRequest;
//import com.wegame.capi.client.domain.request.query.GetStatusOfEventRequest;
//import com.wegame.capi.client.domain.response.DynamicOperationResponse;
//import com.wegame.capi.client.domain.vo.StatusOfEventsResultVo;
//import com.wegame.capi.test.BaseTest;
//import org.assertj.core.util.Lists;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.text.ParseException;
//import java.util.List;
//
//public class DynamicOperationCallbackControllerTest extends BaseTest {
//    @Autowired
//    private DynamicOperationCallbackController dynamicOperationCallbackController;
//
//    @Autowired
//    DynamicOperationCallBackRsaHelper dynamicOperationCallBackRsaHelper;
//
//    @Test
//    public void getStatusOfEvents() throws ParseException {
//        GetStatusOfEventRequest request = GetStatusOfEventRequest.builder().gameId("zhumeng001")
//                .eventIds(Lists.newArrayList("2-0692-4dc5-9f08-4a383b5d311a"))
//                .startTime(DateUtil.parseDateNoTime("2024-12-21 08:09:10","yyyy-MM-dd HH:mm:ss"))
//                .endTime(DateUtil.parseDateNoTime("2025-12-21 08:09:10","yyyy-MM-dd HH:mm:ss"))
//                .build();
//
//        GetStatusOfEventEncryptRequest  message = GetStatusOfEventEncryptRequest.builder()
//                .agentCode("12")
//                .message(dynamicOperationCallBackRsaHelper.encrypt(JSON.toJSONString(request)))
//                .build();
//
//        DynamicOperationResponse<List<StatusOfEventsResultVo>> statusOfEvents = dynamicOperationCallbackController.getStatusOfEvents(message);
//
//        System.out.println("result:"+ JSON.toJSONString(statusOfEvents));
//    }
//}
