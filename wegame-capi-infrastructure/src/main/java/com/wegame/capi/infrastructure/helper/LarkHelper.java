package com.wegame.capi.infrastructure.helper;

import com.google.common.util.concurrent.RateLimiter;
import com.wegame.base.utils.LarkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.defaultThreadPoolExecutor;

/**
 * po服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class LarkHelper {

    private final RateLimiter rateLimiter = RateLimiter.create(3);

    @Value("${lark.warningRobot}")
    public String warningRobot;

    @Async(defaultThreadPoolExecutor)
    public void sendWarningMsg(String msg) {
        try {
            rateLimiter.acquire();
            LarkUtil.sendTextMsg(warningRobot, msg);
        } catch (Exception e) {
            log.error("sendWarningMsg error, msg: {}", msg, e);
        }
    }
}