//package com.wegame.capi.application.controller;
//
//import com.wegame.capi.BaseTest;
//import com.wegame.base.domain.CommonResponse;
//import com.wegame.base.utils.FastJsonUtils;
//import com.wegame.capi.common.context.UserInfoContext;
//import com.wegame.capi.common.context.UserInfo;
//import com.wegame.capi.common.domain.request.query.GetEventReq;
//import com.wegame.capi.common.domain.request.query.ListEventReq;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//@Slf4j
//public class EventControllerTest extends BaseTest {
//
//    @Autowired
//    EventController eventController;
//
//    @BeforeAll
//    public static void beforeTest() {
//        UserInfoVo infoVo = new UserInfoVo();
//        //        infoVo.setId(1L);
////        infoVo.setUserName("userName");
//        UserInfoContext.setUserInfo(infoVo);
//    }
//
//    @Test
//    public void test_getEvent() {
//        CommonResponse<Object> res = eventController.getEvent(GetEventReq.builder().build());
//        Assertions.assertNotNull(res.getData());
//        log.info("testAdd res: {}", FastJsonUtils.toJSONString(res));
//    }
//
//    @Test
//    public void test_listEvent() {
//        CommonResponse<Object> res = eventController.listEvent(ListEventReq.builder().build());
//        Assertions.assertNotNull(res.getData());
//        log.info("testAdd res: {}", FastJsonUtils.toJSONString(res));
//    }
//
//
//}
