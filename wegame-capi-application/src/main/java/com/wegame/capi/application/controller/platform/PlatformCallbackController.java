package com.wegame.capi.application.controller.platform;

import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.converter.PlatformCallbackConverter;
import com.wegame.capi.biz.service.BetSummaryService;
import com.wegame.capi.biz.service.UserService;
import com.wegame.capi.client.domain.request.BetReconciliationRequest;
import com.wegame.capi.client.domain.request.CreditChangeNotifyRequest;
import com.wegame.capi.client.domain.vo.BetSummaryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Api(value = "中台回调模块", tags = {"中台回调模块"})
public class PlatformCallbackController {

    @Autowired
    UserService userService;
    @Autowired
    BetSummaryService betSummaryService;

    @ApiOperation(value = "余额变动通知", httpMethod = "POST")
    @PostMapping("/notifyCreditChange")
    public CommonResponse<Void> notifyCreditChange(@RequestBody CreditChangeNotifyRequest request) {
        request.validateParam();
        return CommonResponse.build(userService.notifyCreditChange(PlatformCallbackConverter.requestToDto(request)));
    }

    @ApiOperation(value = "对账", httpMethod = "POST")
    @PostMapping("/betSummary")
    public CommonResponse<BetSummaryVo> betSummary(@RequestBody BetReconciliationRequest request) {
        request.validateParam();
        return CommonResponse.build(betSummaryService.betSummary(PlatformCallbackConverter.requestToDto(request)));
    }
}
