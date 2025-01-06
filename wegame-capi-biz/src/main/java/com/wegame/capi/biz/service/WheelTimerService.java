package com.wegame.capi.biz.service;

import com.wegame.capi.biz.domain.biz.reward.MariaNotifyResultDo;
import io.netty.util.HashedWheelTimer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WheelTimerService {
    private static final HashedWheelTimer PUSH_TIMER = new HashedWheelTimer(100, TimeUnit.MILLISECONDS);

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public void delayPushPrize(MariaNotifyResultDo prizeResultDo, String redisKey) {
        PUSH_TIMER.newTimeout(timeout ->
                        redisTemplate.opsForList().rightPush(redisKey, prizeResultDo),
                30,
                TimeUnit.SECONDS);
    }


}
