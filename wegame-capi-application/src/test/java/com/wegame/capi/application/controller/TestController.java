//package com.wegame.capi.application.controller;
//
//import com.wegame.base.domain.CommonResponse;
//import com.wegame.base.enums.DistributeIdSceneEnum;
//import com.wegame.base.id.DistributeId;
//import com.wegame.base.id.IdGeneratorHelper;
//import com.wegame.base.lock.DistributeLock;
//import com.wegame.base.utils.FastJsonUtils;
//import com.wegame.capi.common.context.UserInfoContext;
//import com.wegame.capi.common.domain.vo.ReportVo;
//import com.wegame.capi.common.domain.vo.UserVo;
//import com.wegame.capi.common.utils.LockNameUtil;
//import com.wegame.capi.config.rocketmq.RocketMqProducer;
//import com.wegame.capi.service.ReportService;
//import com.wegame.capi.service.UserService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiResponse;
//import io.swagger.annotations.ApiResponses;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//
//@Slf4j
//@RestController
//@RequestMapping("test")
//@Api(value = "测试模块", tags = {"测试模块"})
//public class TestController {
//
//    @Autowired
//    UserService userService;
//    @Autowired
//    ReportService reportService;
//    @Autowired
//    RedisTemplate redisTemplate;
//    @Autowired
//    IdGeneratorHelper idGeneratorHelper;
//    @Autowired(required = false)
//    DistributeLock distributeLock;
//    @Autowired
//    DistributeId distributeId;
//    @Autowired
//    RocketMqProducer rocketMqProducer;
//
//    @ApiOperation(value = "mysql", httpMethod = "GET")
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
//    @GetMapping("/mysql")
//    public CommonResponse<UserVo> testMysql() {
//        return CommonResponse.build(userService.get(1L));
//    }
//
//    @ApiOperation(value = "redis", httpMethod = "GET")
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
//    @GetMapping("/redis")
//    public CommonResponse<Object> testRedis() {
//        redisTemplate.opsForValue().set("ping", "pong");
//        Object val = redisTemplate.opsForValue().get("ping");
//        return CommonResponse.build(val);
//    }
//
////    @ApiOperation(value = "mongodb", httpMethod = "GET")
////    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
////    @GetMapping("/mongodb")
////    public CommonResponse<Object> testMongoDb() {
////        ReportVo reportVo = ReportVo.builder()
////                .playerId(1L)
////                .build();
////        reportService.save(reportVo, UserInfoContext.getUserInfo());
////        ReportVo reportVo1 = reportService.findById(0L);
////        log.info("testMongoDb reportVo1 = {}", FastJsonUtils.toJSONString(reportVo1));
////        return CommonResponse.build(reportVo1);
////    }
//
//    @ApiOperation(value = "distributeLock", httpMethod = "GET")
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
//    @GetMapping("/distributeLock")
//    public CommonResponse<Boolean> distributeLock() {
//        distributeLock.lockThenRun(LockNameUtil.getTestLockName(1),
//                () -> log.info("111, threadId={}", Thread.currentThread().getId()),
//                "distributeLock error111");
//        new Thread(() -> distributeLock.lockThenRun(LockNameUtil.getTestLockName(1),
//                () -> log.info("222, threadId={}", Thread.currentThread().getId()),
//                "distributeLock error222")).start();
//        return CommonResponse.build(true);
//    }
//
//    @ApiOperation(value = "distributeId", httpMethod = "GET")
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
//    @GetMapping("/distributeId")
//    public CommonResponse<Long> distributeId() {
//        return CommonResponse.build(idGeneratorHelper.nextId());
//    }
//
//    @ApiOperation(value = "distributeId2", httpMethod = "GET")
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
//    @GetMapping("/distributeId2")
//    public CommonResponse<Long> distributeId2() {
//        return CommonResponse.build(distributeId.nextId(DistributeIdSceneEnum.TEST));
//    }
//
//    @ApiOperation(value = "graceful/shutdown", httpMethod = "GET")
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
//    @GetMapping(value = "/graceful/shutdown")
//    public CommonResponse<String> gracefulShutdown() throws InterruptedException {
//        // 模拟业务耗时处理流程
//        Thread.sleep(20 * 1000L);
//        return CommonResponse.build("业务处理完毕");
//    }
//
//    @ApiOperation(value = "manual/gc", httpMethod = "GET")
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "success")})
//    @GetMapping(value = "manual/gc")
//    public CommonResponse<Boolean> manualGc() {
//        System.gc();
//        return CommonResponse.build(true);
//    }
//
//    @GetMapping("/msg/send")
//    public CommonResponse<String> sendMessage() {
//        rocketMqProducer.syncSend("mytest-topic", "jayni_test_tag", "this is my sync test message");
//        return CommonResponse.build("ok");
//    }
//
//
//}