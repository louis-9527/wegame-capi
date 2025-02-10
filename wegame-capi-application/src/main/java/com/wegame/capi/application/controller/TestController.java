package com.wegame.capi.application.controller;

import com.alibaba.fastjson.JSON;
import com.wegame.base.domain.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("test")
@Api(value = "测试模块", tags = {"测试模块"})
public class TestController {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @ApiOperation(value = "redisQuery", httpMethod = "GET")
    @GetMapping("/redis/query")
    public CommonResponse<String> redis(@RequestParam(value = "key") String key) {
        Object data = redisTemplate.opsForValue().get(key);
        return CommonResponse.build(data == null ? null : JSON.toJSONString(data));
    }

    @ApiOperation(value = "redisClean", httpMethod = "GET")
    @GetMapping("/redis/clean")
    public CommonResponse<Boolean> redisClean(@RequestParam(value = "key") String key) {
        redisTemplate.delete("key");
        return CommonResponse.build(true);
    }

}