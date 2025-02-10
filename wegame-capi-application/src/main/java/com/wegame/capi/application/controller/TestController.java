//package com.wegame.capi.application.controller;
//
//import com.wegame.capi.biz.listener.PulsarProducer;
//import io.swagger.annotations.Api;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//
//@Slf4j
//@RestController
//@RequestMapping("test")
//@Api(value = "测试模块", tags = {"测试模块"})
//public class TestController {
////    @Autowired
////    PulsarProducer pulscarProducer;
//
//    //    @Autowired
//    //    NacosMariaMakilingConfig nacosMariaMakilingConfig;
//    //
//    //    @ApiOperation(value = "commonProbabilisticConfig", httpMethod = "GET")
//    //    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
//    //    @GetMapping("/commonProbabilisticConfig")
//    //    public CommonResponse<Map<String, List<BigDecimal>>> commonProbabilisticConfig() {
//    //        return CommonResponse.build(nacosMariaMakilingConfig.getCommonProbabilisticConfig());
//    //    }
//    //
//    //    @ApiOperation(value = "freeProbabilisticConfig", httpMethod = "GET")
//    //    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
//    //    @GetMapping("/freeProbabilisticConfig")
//    //    public CommonResponse<Map<String, List<BigDecimal>>> freeProbabilisticConfig() {
//    //        return CommonResponse.build(nacosMariaMakilingConfig.getFreeProbabilisticConfig());
//    //    }
//    //
//    //    @ApiOperation(value = "nacosMysteriousOddsConfig", httpMethod = "GET")
//    //    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
//    //    @GetMapping("/oddsConfig")
//    //    public CommonResponse<Map<String, Map<String, BigDecimal>>> oddsConfig() {
//    //        return CommonResponse.build(nacosMariaMakilingConfig.getOddsConfig());
//    //    }
//
////    @ApiOperation(value = "pulsar发送", httpMethod = "POST")
////    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
////    @PostMapping("/pulsar/send")
////    public CommonResponse<Boolean> send(Object content) throws PulsarClientException {
////        return CommonResponse.build(pulscarProducer.send(GameTypeEnum.MARIA_MAKILING, content));
////    }
//}