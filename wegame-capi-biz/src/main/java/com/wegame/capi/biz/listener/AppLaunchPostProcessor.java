package com.wegame.capi.biz.listener;

import com.wegame.capi.biz.listener.handler.PostHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.defaultThreadPoolExecutor;

/**
 * app启动后处理器
 */
@Slf4j
@Component
public class AppLaunchPostProcessor {

    @Autowired
    List<PostHandler> postHandlers;
    //    @Autowired
    //    RedisTemplate<String, Object> redisTemplate;

    @Async(defaultThreadPoolExecutor)
    public void postProcess() {
        //        try {
        //            redisTemplate.execute((RedisCallback<Void>) connection -> {
        //                connection.flushAll();
        //                log.info("delete all redis data");
        //                return null;
        //            });
        //        } catch (Exception e) {
        //
        //        }
        // 业务后处理runner
        postHandlers.forEach(PostHandler::handle);
    }
}
