package com.wegame.capi.application.controller;

import com.wegame.base.domain.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("common")
@Api(value = "common模块", tags = {"common模块"})
public class CommonController {
    @ApiOperation(value = "ping", httpMethod = "GET")
    @GetMapping("/ping")
    public CommonResponse<String> ping() {
        log.info("CommonController#ping");
        return CommonResponse.build("pong");
    }
}