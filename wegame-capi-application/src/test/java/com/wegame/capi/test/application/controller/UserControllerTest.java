package com.wegame.capi.test.application.controller;

import com.wegame.base.domain.CommonResponse;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.application.controller.UserController;
import com.wegame.capi.client.domain.request.UserLoginRequest;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class UserControllerTest extends BaseTest {

    @Autowired
    UserController userController;

    @Before
    public void beforeTest() {
        ThreadMdcUtil.setTraceIdIfAbsent();
    }

    @Test
    public void test_login() {
        // login success
        HttpServletResponse response = new MockHttpServletResponse();
        CommonResponse<UserInfo> commonResponse = userController.login(response, buildUserLoginRequestForSuccess());
        Assert.assertNotNull(commonResponse);
        Assert.assertTrue(StringUtils.equals(commonResponse.getCode(), CommonResponse.SUC_CODE));
        Assert.assertNotNull(commonResponse.getData());
        Assert.assertTrue(StringUtils.isNotEmpty(commonResponse.getData().getToken()));
        // login fail
        Exception exception = assertThrows(BusinessException.class, () -> {
            userController.login(response, buildUserLoginRequestForFail());
        });
        String errorMsg = "invalid login";
        assertEquals(errorMsg, exception.getMessage());
    }

    @Test
    public void test_logout() {
        HttpServletResponse response = new MockHttpServletResponse();
        CommonResponse<UserInfo> loginResponse = userController.login(response, buildUserLoginRequestForSuccess());
        Assert.assertNotNull(loginResponse);
        Assert.assertTrue(StringUtils.equals(loginResponse.getCode(), CommonResponse.SUC_CODE));
        Assert.assertNotNull(loginResponse.getData());
        Assert.assertTrue(StringUtils.isNotEmpty(loginResponse.getData().getToken()));

        // logout success
        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(Constants._TOKEN_, loginResponse.getData().getToken());
        CommonResponse<Boolean> result = userController.logout(request);
        Assert.assertNotNull(result.getData());
        Assert.assertTrue(result.getData());
    }

    @Test
    public void test_me() {
        // test me success
        HttpServletResponse loginHttpServletResponse = new MockHttpServletResponse();
        CommonResponse<UserInfo> loginResponse = userController.login(loginHttpServletResponse, buildUserLoginRequestForSuccess());
        Assert.assertNotNull(loginResponse);
        Assert.assertTrue(StringUtils.equals(loginResponse.getCode(), CommonResponse.SUC_CODE));
        Assert.assertNotNull(loginResponse.getData());
        Assert.assertTrue(StringUtils.isNotEmpty(loginResponse.getData().getToken()));
        HttpServletResponse successMeHttpServletResponse = new MockHttpServletResponse();
        CommonResponse<UserInfo> successMeResponse = userController.me(successMeHttpServletResponse);
        Assert.assertNotNull(successMeResponse);
        Assert.assertNull(successMeResponse.getData());

        // test me fail
        HttpServletRequest logoutHttpServletRequest = new MockHttpServletRequest();
        logoutHttpServletRequest.setAttribute(Constants._TOKEN_, loginResponse.getData().getToken());
        CommonResponse<Boolean> logoutResponse = userController.logout(logoutHttpServletRequest);
        Assert.assertNotNull(logoutResponse.getData());
        Assert.assertTrue(logoutResponse.getData());
        HttpServletResponse failMeHttpServletResponse = new MockHttpServletResponse();
        CommonResponse<UserInfo> failMeResponse = userController.me(failMeHttpServletResponse);
        Assert.assertNotNull(failMeResponse);
        Assert.assertNull(failMeResponse.getData());
    }


    @Test
    public void test_getCredit() {
        // test getCredit success
        HttpServletResponse loginHttpServletResponse = new MockHttpServletResponse();
        CommonResponse<UserInfo> loginResponse = userController.login(loginHttpServletResponse, buildUserLoginRequestForSuccess());
        Assert.assertNotNull(loginResponse);
        Assert.assertTrue(StringUtils.equals(loginResponse.getCode(), CommonResponse.SUC_CODE));
        Assert.assertNotNull(loginResponse.getData());
        Assert.assertTrue(StringUtils.isNotEmpty(loginResponse.getData().getToken()));
        UserInfoContext.setUserInfo(loginResponse.getData());
        HttpServletResponse successGetCreditHttpServletResponse = new MockHttpServletResponse();
        CommonResponse<Long> successGetCreditResponse = userController.getCredit(successGetCreditHttpServletResponse);
        Assert.assertNotNull(successGetCreditResponse);
        Assert.assertNotNull(successGetCreditResponse.getData());

        // test getCredit fail
        Exception exception = assertThrows(BusinessException.class, () -> {
            UserInfoContext.setUserInfo(UserInfo.builder()
                    .id("10000")
                    .playerId("123456")
                    .playerName("123456")
                    .userName("system")
                    .build());
            HttpServletResponse failMeHttpServletResponse = new MockHttpServletResponse();
            userController.getCredit(failMeHttpServletResponse);
        });
        String errorMsg = "Parameter error";
        assertEquals(errorMsg, exception.getMessage());
    }

    private static UserLoginRequest buildUserLoginRequestForSuccess() {
        UserLoginRequest request = new UserLoginRequest();
        request.setPId("55");
        request.setChannelId("1051");
        request.setGameId(GameTypeEnum.MARIA_MAKILING.getId());
        request.setPlayerId(TEST_PLAYER_ID);
        request.setPlayerName(TEST_PLAYER_NAME);
        request.setUserName(TEST_USER_NAME);
        request.setNickName(TEST_NICK_NAME);
        request.setUserLevel(TEST_USER_LEVEL);
        request.setToken(TEST_TOKEN);
        return request;
    }

    private static UserLoginRequest buildUserLoginRequestForFail() {
        UserLoginRequest request = new UserLoginRequest();
        request.setPId("55");
        request.setChannelId("1051");
        request.setGameId(GameTypeEnum.MARIA_MAKILING.getId());
        request.setPlayerId(TEST_PLAYER_ID + "_invalid");
        request.setPlayerName(TEST_PLAYER_NAME + "_invalid");
        request.setUserName(TEST_USER_NAME + "_invalid");
        request.setNickName(TEST_NICK_NAME + "_invalid");
        request.setUserLevel(TEST_USER_LEVEL + "_invalid");
        request.setToken(TEST_TOKEN + "_invalid");
        return request;
    }
}
