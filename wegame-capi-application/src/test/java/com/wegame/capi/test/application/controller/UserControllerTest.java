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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserControllerTest extends BaseTest {

    @Autowired
    UserController userController;

    @Test
    public void test_login() {
        CommonResponse<UserInfo> result = userController.login(null, buildUserLoginRequest());
        Assert.notNull(result.getData());
        Assert.notBlank(result.getData().getToken());
    }

    @Test
    public void test_loginOut() {
        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("token", ""); // todo
        CommonResponse<Boolean> result = userController.logout(request);
        Assert.notNull(result.getData());
        Assert.isTrue(result.getData());
    }

    @Test
    public void test_me() {
        HttpServletResponse httpServletResponse = new MockHttpServletResponse();
        CommonResponse<UserInfo> response = userController.me(httpServletResponse);
        log.info("me: {}", response);
        Assert.notNull(response);
        Assert.isNull(response.getData());
    }


    @Test
    public void test_getCredit() {

    }

    private static UserLoginRequest buildUserLoginRequest() {
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
        return request;
    }

    //    curl 'https://capi.makillingslot.net/capi/game/user/login' \
    //            -H 'accept: */*' \
    //            -H 'accept-language: zh-CN,zh;q=0.9' \
    //            -H 'content-type: application/json' \
    //            -H 'gameid: zhumeng002' \
    //            -H 'noncestr: boo06wgzbl' \
    //            -H 'origin: https://game2.makillingslot.net' \
    //            -H 'playerid: IgoTest10020' \
    //            -H 'priority: u=1, i' \
    //            -H 'sec-fetch-dest: empty' \
    //            -H 'sec-fetch-mode: cors' \
    //            -H 'sec-fetch-site: same-site' \
    //            -H 'signature: b76f0f468c4560a136cabe9411e3303c' \
    //            -H 'timestamp: 1740473523940' \
    //            -H 'token-game: 143a5c0ea9f3021065f0e37383a14a71' \
    //            -H 'user-agent: Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1' \
    //            --data-raw '{"birthday":"01-01-1990","channelId":"1051","nickName":"IgoTest10020","gameId":"zhumeng002","pId":"55","playerId":"IgoTest10020","playerName":"IgoTest10020","token":"13820fff6c479bfb53107bc8c43b81c6be7881b21dca68e8db6b9f2265599f96364f19e939f86dfeda3dd7e4d115050939a8d1e83b6a5c6a76ae9d1a610f9300054725e423edbe9b3c8d7bfc5d815b2435df548520f3f1750356e22a5f6f8065","userLevel":"1","userName":"IgoTest10020"}'

}
