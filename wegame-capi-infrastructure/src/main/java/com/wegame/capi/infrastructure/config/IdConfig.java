package com.wegame.capi.infrastructure.config;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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

    @Autowired
    RedisTemplate redisTemplate;

    @Bean
    SnowflakeIdGenerator snowflakeIdGenerator() {
        log.info("start config snowflakeIdGenerator, ip: {}", IpUtil.getLocalIp());
        return new SnowflakeIdGenerator(getMachineId());
    }

    private Long getMachineId() {
        int maxRetry = 100;
        int tempRetry = 0;
        Long machineId = null;
        do {
            Long tempMachineId = redisTemplate.opsForValue().increment(SnowflakeIdGenerator.REDIS_MACHINE_ID_KEY, 1);
            log.info("IdConfig, get MachineId, ip: {}, machineId: {}", IpUtil.getLocalIp(), machineId);
            if (tempMachineId != null) {
                machineId = tempMachineId % 1024;
            } else {
                redisTemplate.opsForValue().set(SnowflakeIdGenerator.REDIS_MACHINE_ID_KEY, 1);
            }
            tempRetry ++;
        } while (Objects.isNull(machineId) && tempRetry < maxRetry);
        log.info("IdConfig, ip: {}, machineId: {}", IpUtil.getLocalIp(), machineId);
        AssertUtil.assertNotNull(machineId, ErrorCode.ERR_INTER);
        return Math.abs(machineId);
    }
}
