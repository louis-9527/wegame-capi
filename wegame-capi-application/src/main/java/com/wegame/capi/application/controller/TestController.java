package com.wegame.capi.application.controller;

import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.infrastructure.config.nacos.NacosMysteriousOddsConfig;
import com.wegame.capi.infrastructure.config.nacos.NacosMysteriousProbabilisticConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("test")
@Api(value = "测试模块", tags = {"测试模块"})
public class TestController {


    @Autowired
    NacosMysteriousProbabilisticConfig nacosMysteriousProbabilisticConfig;
    @Autowired
    NacosMysteriousOddsConfig nacosMysteriousOddsConfig;

    @ApiOperation(value = "ProbabilisticConfig", httpMethod = "GET")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
    @GetMapping("/probabilisticConfig")
    public CommonResponse<Map<String, List<BigDecimal>>> testMysql() {
        return CommonResponse.build(nacosMysteriousProbabilisticConfig.getProbabilisticConfigMap());
    }

    @ApiOperation(value = "nacosMysteriousOddsConfig", httpMethod = "GET")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
    @GetMapping("/nacosMysteriousOddsConfig")
    public CommonResponse<Map<String, Map<String, Double>>> getOddsMap() {
        return CommonResponse.build(nacosMysteriousOddsConfig.getOddsConfigMap());
    }

}