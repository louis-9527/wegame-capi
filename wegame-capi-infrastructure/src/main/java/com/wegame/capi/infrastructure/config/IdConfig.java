package com.wegame.capi.infrastructure.config;

import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

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
        return new SnowflakeIdGenerator(getMachineId());
    }

    private Long getMachineId() {
        Long machineId = null;
        do {
            Long tempMachineId = redisTemplate.opsForValue().increment(SnowflakeIdGenerator.REDIS_MACHINE_ID_KEY, 1);
            if (tempMachineId != null && tempMachineId > 0 && tempMachineId < SnowflakeIdGenerator.MAX_MACHINE_ID) {
                machineId = tempMachineId % 1024;
            }
        } while (machineId == null);
        log.info("IdConfig, ip: {}, machineId: {}", IpUtil.getLocalIp(), machineId);
        return machineId;
    }
}
