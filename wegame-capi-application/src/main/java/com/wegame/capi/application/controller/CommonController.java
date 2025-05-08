package com.wegame.capi.application.controller;

import com.wegame.base.domain.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("common")
@Api(value = "common模块", tags = {"common模块"})
public class CommonController extends AbstractController {

    @ApiOperation(value = "ping", httpMethod = "GET")
    @GetMapping("/ping")
    public CommonResponse<String> ping(HttpServletResponse response) {
        log.info("CommonController#ping");
        noCacheControl(response);
        return CommonResponse.build("pong");
    }
}