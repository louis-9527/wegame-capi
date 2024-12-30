package com.wegame.capi.application.controller;

import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.service.ServerRateLimitTestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("serverRateLimit")
@Api(value = "限流测试模块", tags = {"限流测试模块"})
public class ServerRateLimitTestController {
    @Autowired
    ServerRateLimitTestService serverRateLimitTestService;

    @ApiOperation(value = "test", httpMethod = "GET")
    @GetMapping("/test")
    public CommonResponse<String> test() {
        return CommonResponse.build(serverRateLimitTestService.rateLimit());
    }
}
