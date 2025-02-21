package com.wegame.capi.infrastructure.config;

import com.wegame.base.executor.WsThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author apple
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {

    public static final String defaultThreadPoolExecutor = "defaultThreadPoolExecutor";

    public static final String betRecordThreadPoolExecutor = "betRecordThreadPoolExecutor";

    public static final String dynamicOperationThreadPoolExecutor = "dynamicOperationThreadPoolExecutor";

    public static final String sendMessageThreadPoolExecutor = "sendMessageThreadPoolExecutor";

    /**
     * 默认模块-线程池
     *
     * @return
     */
    @Bean(defaultThreadPoolExecutor)
    public ThreadPoolExecutor defaultThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(100,
                100,
                30L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue(1000),
                new CustomizableThreadFactory("default-pool-"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    /**
     * 投注模块-线程池
     *
     * @return
     */
    @Bean(betRecordThreadPoolExecutor)
    public ThreadPoolExecutor betRecordThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(20,
                5000,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue(10000),
                new CustomizableThreadFactory("bet-record-pool-"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }


    @Bean(sendMessageThreadPoolExecutor)
    public ThreadPoolExecutor sendMessageThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(10,
                50,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue(10000),
                new CustomizableThreadFactory("send-message-pool-"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }


    /**
     * 动态运营-线程池
     *
     * @return
     */
    @Bean(dynamicOperationThreadPoolExecutor)
    public ThreadPoolExecutor dynamicOperationThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(10,
                100,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue(10000),
                new CustomizableThreadFactory("bet-record-pool-"),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

}
