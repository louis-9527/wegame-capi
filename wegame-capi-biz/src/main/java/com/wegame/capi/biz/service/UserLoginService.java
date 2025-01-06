package com.wegame.capi.biz.service;

import cn.hutool.crypto.SecureUtil;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.biz.builder.UserInfoBuilder;
import com.wegame.capi.biz.builder.req.GetAuthLoginTokenReqBuilder;
import com.wegame.capi.biz.domain.UserLoginDo;
import com.wegame.capi.biz.domain.user.UserDo;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserLoginService {
    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    SingleWalletApi singleWalletApi;

    public UserInfo login(HttpServletResponse response, UserLoginDo userLoginDo) {
        // todo fix token认证 结果判断  @maidong
        singleWalletApi.authLoginToken(GetAuthLoginTokenReqBuilder.build(userLoginDo));

        String token = SecureUtil.md5(userLoginDo.getToken());
        response.addHeader(Constants._TOKEN_, token);
        UserDo userDo = UserInfoBuilder.buildUserDo(userLoginDo);
        Long userId = userService.insertOrUpdate(userDo);
        UserInfo userInfo = UserInfoBuilder.buildUserInfo(userLoginDo, userId, token);

        String loginTokenKey = String.format(Constants.CUSER_LOGIN_TOKEN, token);
        redisTemplate.opsForValue().set(loginTokenKey, userInfo, Constants.TOKEN_EXPIRED, TimeUnit.HOURS);
        String loginIdentityKey = String.format(Constants.CUSER_LOGIN_IDENTITY, userDo.getPlayerId());
        redisTemplate.opsForValue().set(loginIdentityKey, token, Constants.TOKEN_EXPIRED, TimeUnit.HOURS);
        return userInfo;
    }

    public Boolean logout(String token) {
        try {
            redisTemplate.delete(Arrays.asList(String.format(Constants.CUSER_LOGIN_TOKEN, token),
                    String.format(Constants.CUSER_LOGIN_IDENTITY, UserInfoContext.getUserInfo().getPlayerId())));
        } catch (Exception e) {
            log.error("logout error, errMsg = {}", e.getMessage(), e);
            return false;
        }
        return true;
    }

    private Boolean innerLogout(String playerId) {
        try {
            String token = (String) redisTemplate.opsForValue().get(String.format(Constants.CUSER_LOGIN_IDENTITY, playerId));
            redisTemplate.delete(Arrays.asList(String.format(Constants.CUSER_LOGIN_TOKEN, token), String.format(Constants.CUSER_LOGIN_IDENTITY, playerId)));
        } catch (Exception e) {
            log.error("logout error, errMsg = {}", e.getMessage(), e);
            return false;
        }
        return true;
    }
}
