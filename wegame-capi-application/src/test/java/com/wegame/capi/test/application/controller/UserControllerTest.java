package com.wegame.capi.test.application.controller;

import cn.hutool.core.lang.Assert;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.application.controller.UserController;
import com.wegame.capi.client.domain.request.UserLoginRequest;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

@Slf4j
public class UserControllerTest extends BaseTest {

    @Autowired
    UserController userController;

    @BeforeAll
    public static void beforeTest() {
        UserInfoContext.setUserInfo(UserInfo.builder()
                .id("10000")
                .playerId("123456")
                .playerName("123456")
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
        request.setPId("55");
        request.setChannelId("1051");
        request.setGameId("zhumeng001");
        request.setPlayerId("igoslot01");
        request.setPlayerName("igoslot01");
        request.setUserName("1204352556");
        request.setNickName("YUAN6401");
        request.setUserLevel("1");
        request.setToken("13820fff6c479bfb53107bc8c43b81c6be7881b21dca68e8db6b9f2265599f968b5c8509eb065535ae1395483a741b96658fc228a1da41d4620d1765daa2588c59b86d0310e3e434ab252a7b4a58e265b61913194d845f17a6afec2867b8e964");
        CommonResponse<UserInfo> result = userController.login(null, request);
        Assert.isNull(result.getData());
    }

    @Test
    public void test_getCredit() {

    }

}
