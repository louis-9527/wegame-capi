package com.wegame.capi.application.controller;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.service.UserLoginService;
import com.wegame.capi.client.domain.request.UserLoginRequest;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("user")
@Api(value = "用户模块", tags = {"用户模块"})
public class UserController {

    @Autowired
    UserLoginService userLoginService;
    @Autowired
    RedisTemplate redisTemplate;

    @ApiOperation(value = "登录", httpMethod = "POST")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
    @PostMapping("/login")
    public CommonResponse<UserInfo> login(HttpServletResponse response, @Valid @RequestBody UserLoginRequest request) {
        request.validateParam();
        return CommonResponse.build(userLoginService.login(response, request));
    }

    @LoginRequired
    @ApiOperation(value = "登出", httpMethod = "GET")
    @GetMapping("/logout")
    public CommonResponse<Boolean> logout(HttpServletRequest request) {
        String token = request.getHeader(Constants._TOKEN_);
        return CommonResponse.build(userLoginService.logout(token));
    }

    @LoginRequired
    @ApiOperation(value = "个人信息", httpMethod = "GET")
    @GetMapping("/me")
    public CommonResponse<UserInfo> me() {
        return CommonResponse.build(UserInfoContext.getUserInfo());
    }
}