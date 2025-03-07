package com.wegame.capi.application.controller;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.converter.UserLoginConvert;
import com.wegame.capi.biz.service.LoginService;
import com.wegame.capi.biz.service.UserService;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户模块
 * @author generate
 */
@Slf4j
@RestController
@RequestMapping("user")
@Api(value = "用户模块", tags = {"用户模块"})
public class UserController extends AbstractController {

    @Autowired
    LoginService loginService;
    @Autowired
    UserService userService;

    @ApiOperation(value = "登录", httpMethod = "POST")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
    @PostMapping("/login")
    public CommonResponse<UserInfo> login(HttpServletResponse response, @RequestBody UserLoginRequest request) {
        request.validateParam();
        return CommonResponse.build(loginService.login(response, UserLoginConvert.requestToDo(request)));
    }

    @LoginRequired
    @ApiOperation(value = "登出", httpMethod = "GET")
    @GetMapping("/logout")
    public CommonResponse<Boolean> logout(HttpServletRequest request) {
        String token = request.getHeader(Constants._TOKEN_);
        return CommonResponse.build(loginService.logout(token));
    }

    @LoginRequired
    @ApiOperation(value = "个人信息", httpMethod = "GET")
    @GetMapping("/me")
    public CommonResponse<UserInfo> me(HttpServletResponse response) {
        noCacheControl(response);
        return CommonResponse.build(UserInfoContext.getUserInfo());
    }

    @LoginRequired
    @ApiOperation(value = "查询余额", httpMethod = "GET")
    @GetMapping("/getCredit")
    public CommonResponse<Long> getCredit(HttpServletResponse response) {
        noCacheControl(response);
        return CommonResponse.build(userService.getCredit());
    }
}