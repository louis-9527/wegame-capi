package com.wegame.capi.application.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("test")
@Api(value = "测试模块", tags = {"测试模块"})
public class TestController {

    //    @Autowired
    //    NacosMariaMakilingConfig nacosMariaMakilingConfig;
    //
    //    @ApiOperation(value = "commonProbabilisticConfig", httpMethod = "GET")
    //    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
    //    @GetMapping("/commonProbabilisticConfig")
    //    public CommonResponse<Map<String, List<BigDecimal>>> commonProbabilisticConfig() {
    //        return CommonResponse.build(nacosMariaMakilingConfig.getCommonProbabilisticConfig());
    //    }
    //
    //    @ApiOperation(value = "freeProbabilisticConfig", httpMethod = "GET")
    //    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
    //    @GetMapping("/freeProbabilisticConfig")
    //    public CommonResponse<Map<String, List<BigDecimal>>> freeProbabilisticConfig() {
    //        return CommonResponse.build(nacosMariaMakilingConfig.getFreeProbabilisticConfig());
    //    }
    //
    //    @ApiOperation(value = "nacosMysteriousOddsConfig", httpMethod = "GET")
    //    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
    //    @GetMapping("/oddsConfig")
    //    public CommonResponse<Map<String, Map<String, BigDecimal>>> oddsConfig() {
    //        return CommonResponse.build(nacosMariaMakilingConfig.getOddsConfig());
    //    }

}