package com.wegame.capi.infrastructure.config;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

/**
 * @author apple
 */
@Slf4j
@Configuration
public class IdConfig {

    private final static int MAX_RETRY = 100;
    private final static long MAX_MACHINE_ID = 1024L;
    @Value("${spring.application.name}")
    private String appName;
    @Autowired
    RedisTemplate redisTemplate;

    @Bean
    SnowflakeIdGenerator snowflakeIdGenerator() {
        log.info("start config snowflakeIdGenerator, ip: {}", IpUtil.getLocalIp());
        return new SnowflakeIdGenerator(getMachineId());
    }

    private Long getMachineId() {
        String machineIdKey = appName + "_" + SnowflakeIdGenerator.REDIS_MACHINE_ID_KEY;
        int tempRetry = 0;
        Long machineId = null;
        do {
            Long tempMachineId = redisTemplate.opsForValue().increment(machineIdKey, 1);
            log.info("IdConfig, get MachineId, ip: {}, machineId: {}", IpUtil.getLocalIp(), tempMachineId);
            if (tempMachineId != null) {
                machineId = tempMachineId;
            } else {
                redisTemplate.opsForValue().set(SnowflakeIdGenerator.REDIS_MACHINE_ID_KEY, 1);
            }
            tempRetry++;
        } while (Objects.isNull(machineId) && tempRetry < MAX_RETRY);
        log.info("IdConfig, ip: {}, machineId: {}", IpUtil.getLocalIp(), machineId);
        AssertUtil.assertNotNull(machineId, ErrorCode.ERR_INTER, "machineId is null");
        return Math.abs(machineId) % MAX_MACHINE_ID;
    }
}
