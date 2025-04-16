package com.wegame.capi.scheduler.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class RedisScheduleTask {

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    // 30s
    @Scheduled(fixedRate = 30000)
    public void configureTasks() {
        log.info("ping redis start");
        redisTemplate.execute((RedisCallback<String>) connection -> {
                    String pong = connection.ping();
                    log.info("pong: {}", pong);
                    return "pong: " + pong;
                }
        );
        log.info("ping redis end");
    }
}