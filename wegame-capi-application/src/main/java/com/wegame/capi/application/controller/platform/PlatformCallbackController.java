package com.wegame.capi.application.controller.platform;

import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.converter.PlatformCallbackConverter;
import com.wegame.capi.biz.service.UserService;
import com.wegame.capi.client.domain.request.CreditChangeNotifyRequest;
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

    @ApiOperation(value = "余额变动通知", httpMethod = "POST")
    @PostMapping("/promonotifyCreditChange")
    public CommonResponse<Void> notifyCreditChange(@RequestBody CreditChangeNotifyRequest request) {
        request.validateParam();
        userService.notifyCreditChange(PlatformCallbackConverter.requestToDto(request));
        return CommonResponse.build(null);
    }

}
