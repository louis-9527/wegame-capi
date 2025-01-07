package com.wegame.capi.application.controller;

import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.biz.service.UserService;
import com.wegame.capi.client.domain.request.BetSummaryRequest;
import com.wegame.capi.client.domain.request.BetSummaryVo;
import com.wegame.capi.client.domain.request.NotifyCreditChangeRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Api(value = "回调模块", tags = {"回调模块"})
public class CallbackController {

    @Autowired
    UserService userService;
    @Autowired
    GameRecordService gameRecordService;

    @ApiOperation(value = "余额变动通知", httpMethod = "POST")
    @PostMapping("/notifyCreditChange")
    public CommonResponse<Void> notifyCreditChange(@RequestBody NotifyCreditChangeRequest request) {
        request.validateParam();
        return CommonResponse.build(userService.notifyCreditChange(request));
    }

    @ApiOperation(value = "对账", httpMethod = "POST")
    @PostMapping("/betSummary")
    public CommonResponse<BetSummaryVo> betSummary(@RequestBody BetSummaryRequest request) {
        request.validateParam();
        return CommonResponse.build(gameRecordService.betSummary(request));
    }
}
