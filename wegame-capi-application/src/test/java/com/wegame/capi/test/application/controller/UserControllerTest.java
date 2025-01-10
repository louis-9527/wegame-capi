package com.wegame.capi.test.application.controller;

import cn.hutool.core.lang.Assert;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.client.domain.request.UserLoginRequest;
import com.wegame.capi.test.BaseTest;
import com.wegame.capi.application.controller.UserController;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class UserControllerTest extends BaseTest {

    @Autowired
    UserController userController;

    @BeforeAll
    public static void beforeTest() {
        UserInfoContext.setUserInfo(UserInfo.builder()
                .id("10000")
                .build());
    }

    @Test
    public void test_me() {
        CommonResponse<UserInfo> response = userController.me();
        log.info("me: {}", response);
        Assert.notNull(response);
        Assert.isNull(response.getData());
    }

    @Test
    public void test_login() {
        UserLoginRequest request = new UserLoginRequest();
        CommonResponse<UserInfo> response = userController.login(null, request);
        Assert.notNull(response);
        Assert.isNull(response.getData());
    }

    @Test
    public void test_getCredit() {

    }

}
