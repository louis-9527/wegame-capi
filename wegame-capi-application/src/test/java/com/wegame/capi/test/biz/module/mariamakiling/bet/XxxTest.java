package com.wegame.capi.test.biz.module.mariamakiling.bet;

import com.wegame.capi.test.BaseTest;
import com.wegame.capi.application.controller.UserController;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class XxxTest extends BaseTest {

    @Autowired
    UserController userController;

    @BeforeAll
    public static void beforeTest() {
        UserInfoContext.setUserInfo(UserInfo.builder()
                .id("10000")
                .build());
    }

    @Test
    public void test_XxxMethod() {

    }
}
