package com.wegame.capi.infrastructure.config;

import com.wegame.base.executor.WsThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author apple
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {

    public static final String defaultThreadPoolExecutor = "defaultThreadPoolExecutor";
    public static final String userThreadPoolExecutor = "userThreadPoolExecutor";
    public static final String reportThreadPoolExecutor = "reportThreadPoolExecutor";
    public static final String betThreadPoolExecutor = "betThreadPoolExecutor";
    public static final String payOutThreadPoolExecutor = "payOutThreadPoolExecutor";

    public static final String boxingMatchChangeNotifyExecutor = "boxingMatchChangeNotifyExecutor";
    public static final String mqThreadPoolExecutor = "mqThreadPoolExecutor";

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
     * 用户模块-线程池
     *
     * @return
     */
    @Bean(userThreadPoolExecutor)
    public ThreadPoolExecutor userThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(100,
                100,
                30L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue(100000),
                new CustomizableThreadFactory("user-pool-"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    /**
     * 投注模块-线程池
     *
     * @return
     */
    @Bean(betThreadPoolExecutor)
    public ThreadPoolExecutor betThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(100,
                100,
                30L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue(5000),
                new CustomizableThreadFactory("bet-pool-"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    /**
     * 投注模块-线程池
     *
     * @return
     */
    @Bean(payOutThreadPoolExecutor)
    public ThreadPoolExecutor payOutThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(100,
                100,
                30L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue(10000),
                new CustomizableThreadFactory("payOut-pool-"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    /**
     * 埋点模块-线程池
     *
     * @return
     */
    @Bean(reportThreadPoolExecutor)
    public ThreadPoolExecutor reportThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(100,
                100,
                30L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue(1000000),
                new CustomizableThreadFactory("report-pool-"));
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    @Bean(boxingMatchChangeNotifyExecutor)
    public ThreadPoolExecutor boxingMatchChangeNotifyExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(1,
                1,
                30L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1),
                new CustomizableThreadFactory("boxingMatchChange-pool-"),
                new ThreadPoolExecutor.DiscardPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    /**
     * 消息-线程池
     * @return
     */
    @Bean(mqThreadPoolExecutor)
    public ThreadPoolExecutor mqThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(0,
                10,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue(5000),
                new CustomizableThreadFactory("mq-pool-"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }
}
