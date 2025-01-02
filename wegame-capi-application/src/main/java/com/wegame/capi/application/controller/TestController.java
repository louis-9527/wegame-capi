package com.wegame.capi.application.controller;

import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.infrastructure.config.nacos.NacosMariaMakilingConfig;
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
    NacosMariaMakilingConfig nacosMariaMakilingConfig;

    @ApiOperation(value = "commonProbabilisticConfig", httpMethod = "GET")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
    @GetMapping("/commonProbabilisticConfig")
    public CommonResponse<Map<String, List<BigDecimal>>> commonProbabilisticConfig() {
        return CommonResponse.build(nacosMariaMakilingConfig.getCommonProbabilisticConfig());
    }

    @ApiOperation(value = "freeProbabilisticConfig", httpMethod = "GET")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
    @GetMapping("/freeProbabilisticConfig")
    public CommonResponse<Map<String, List<BigDecimal>>> freeProbabilisticConfig() {
        return CommonResponse.build(nacosMariaMakilingConfig.getFreeProbabilisticConfig());
    }

    @ApiOperation(value = "nacosMysteriousOddsConfig", httpMethod = "GET")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
    @GetMapping("/oddsConfig")
    public CommonResponse<Map<String, Map<String, BigDecimal>>> oddsConfig() {
        return CommonResponse.build(nacosMariaMakilingConfig.getOddsConfig());
    }

}