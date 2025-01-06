package com.wegame.capi.application.controller;

import cn.hutool.core.lang.Assert;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.application.BaseTest;
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

    // 补充单测 todo @maidong


}
