package com.wegame.capi.biz.service;

import cn.hutool.crypto.SecureUtil;
import com.wegame.capi.api.CommonApi;
import com.wegame.capi.api.domain.req.GetAuthLoginTokenReq;
import com.wegame.capi.biz.builder.UserInfoBuilder;
import com.wegame.capi.biz.domain.user.UserDo;
import com.wegame.capi.client.domain.request.UserLoginRequest;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.context.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserLoginService {
    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    CommonApi commonApi;

    public UserInfo login(HttpServletResponse response, UserLoginRequest request) {
        commonApi.authLoginToken(GetAuthLoginTokenReq.buildClientReq(request));

        String token = SecureUtil.md5(request.getToken());
        response.addHeader(Constants._TOKEN_, token);
        UserDo userDo = UserInfoBuilder.buildUserDo(request);
        Long userId = userService.insertOrUpdate(userDo);
        UserInfo userInfo = UserInfoBuilder.buildUserInfo(request, userId, token);
        userInfo.setId(String.valueOf(userId));

        redisTemplate.opsForValue().set(String.format(Constants.CUSER_LOGIN_TOKEN, token), userInfo, Constants.TOKEN_EXPIRED, TimeUnit.HOURS);
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
