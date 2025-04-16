package com.wegame.capi.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * @author apple
 */
@Slf4j
@Configuration
public class LettuceConnectionValidConfig implements InitializingBean {

    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (redisConnectionFactory instanceof LettuceConnectionFactory) {
            ((LettuceConnectionFactory) redisConnectionFactory).setValidateConnection(true);
            ((LettuceConnectionFactory) redisConnectionFactory).setShareNativeConnection(true);
            ((LettuceConnectionFactory) redisConnectionFactory).setEagerInitialization(true);
        }
    }
}
