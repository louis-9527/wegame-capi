package com.wegame.capi.application.controller;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.builder.BetBuilder;
import com.wegame.capi.biz.service.BetService;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.domain.request.UserLoginRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("投注")
@Api(value = "投注模块", tags = {"投注模块"})
public class BetController {

    @Autowired
    BetService betService;

    @LoginRequired
    @ApiOperation(value = "投注", httpMethod = "POST")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
    @PostMapping("/bet")
    public CommonResponse<Object> bet(@Valid @RequestBody BetRequest request) {
        request.validate();
        return CommonResponse.build(betService.bet(BetBuilder.buildBetDo(request)));
    }

}