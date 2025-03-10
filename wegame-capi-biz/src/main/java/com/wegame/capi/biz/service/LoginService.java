package com.wegame.capi.biz.service;

import cn.hutool.crypto.SecureUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.domain.res.singlewallet.AuthLoginTokenRes;
import com.wegame.capi.biz.builder.UserInfoBuilder;
import com.wegame.capi.biz.builder.req.GetAuthLoginTokenReqBuilder;
import com.wegame.capi.biz.domain.user.UserLoginDo;
import com.wegame.capi.biz.domain.user.UserDo;
import com.wegame.capi.common.Constants;
import com.wegame.capi.client.context.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LoginService {
    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    SingleWalletApi singleWalletApi;

    public UserInfo login(HttpServletResponse response, UserLoginDo userLoginDo) {
        AuthLoginTokenRes authLoginTokenRes = singleWalletApi.authLoginToken(GetAuthLoginTokenReqBuilder.build(userLoginDo));
        AssertUtil.assertNotNull(authLoginTokenRes, ErrorCode.ERR_MIDDLE_PLATFORM);
        String token = SecureUtil.md5(userLoginDo.getToken() + userLoginDo.getGameId());
        response.addHeader(Constants._TOKEN_, token);
        UserDo userDo = UserInfoBuilder.buildUserDo(userLoginDo);
        Long userId = userService.insert(userDo);
        UserInfo userInfo = UserInfoBuilder.buildUserInfo(userLoginDo, userId, token);
        String loginTokenKey = String.format(Constants.CUSER_LOGIN_TOKEN, token);
        redisTemplate.opsForValue().set(loginTokenKey, userInfo, Constants.TOKEN_EXPIRED, TimeUnit.HOURS);
        return userInfo;
    }

    public Boolean logout(String token) {
        try {
            redisTemplate.delete(String.format(Constants.CUSER_LOGIN_TOKEN, token));
        } catch (Exception e) {
            log.error("logout error, errMsg = {}", e.getMessage(), e);
            return false;
        }
        return true;
    }
}
