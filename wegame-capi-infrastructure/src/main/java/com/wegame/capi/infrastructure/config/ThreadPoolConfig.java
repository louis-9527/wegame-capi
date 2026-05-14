package com.wegame.capi.infrastructure.config;

import com.wegame.base.executor.WsThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.*;

/**
 * @author apple
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {

    public static final String defaultThreadPoolExecutor = "defaultThreadPoolExecutor";
    public static final String betRecordThreadPoolExecutor = "betRecordThreadPoolExecutor";
    public static final String betRecordStatusThreadPoolExecutor = "betRecordStatusThreadPoolExecutor";
    public static final String sendMessageThreadPoolExecutor = "sendMessageThreadPoolExecutor";
    public static final String gameRecordPutCacheThreadPoolExecutor = "gameRecordPutCacheThreadPoolExecutor";
    public static final String operationEventThreadPoolExecutor = "operationEventThreadPoolExecutor";
    public static final String operationEventReportThreadPoolExecutor = "operationEventReportThreadPoolExecutor";
    public static final String starCardUseRecordThreadPoolExecutor = "starCardUseRecordThreadPoolExecutor";

    public static final String autoContinuePlayGameThreadPoolExecutor = "autoContinuePlayGameThreadPoolExecutor";
    public static final String warmUpConnectionThreadPoolExecutor = "warmUpConnectionThreadPoolExecutor";

    public static final String scheduledThreadPoolExecutor = "scheduledThreadPoolExecutor";
    public static final String inventoryThreadPoolExecutor = "inventoryThreadPoolExecutor";

    @Bean(defaultThreadPoolExecutor)
    public ThreadPoolExecutor defaultThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(1,
                100,
                100L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue(100000),
                new CustomizableThreadFactory("default-pool-"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        // executor.allowCoreThreadTimeOut(true);
        executor.prestartAllCoreThreads();
        return executor;
    }

    @Bean(betRecordThreadPoolExecutor)
    public ThreadPoolExecutor betRecordThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(10,
                1000,
                100L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue(100000),
                new CustomizableThreadFactory("bet-record-pool-"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        // executor.allowCoreThreadTimeOut(true);
        executor.prestartAllCoreThreads();
        return executor;
    }

    @Bean(betRecordStatusThreadPoolExecutor)
    public ThreadPoolExecutor betRecordStatusThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(5,
                100,
                100L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100000),
                new CustomizableThreadFactory("bet-record-status-pool-"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        // executor.allowCoreThreadTimeOut(true);
        executor.prestartAllCoreThreads();
        return executor;
    }

    @Bean(sendMessageThreadPoolExecutor)
    public ThreadPoolExecutor sendMessageThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(20,
                50,
                100L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue(1000000),
                new CustomizableThreadFactory("send-message-pool-"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        // executor.allowCoreThreadTimeOut(true);
        executor.prestartAllCoreThreads();
        return executor;
    }

    @Bean(gameRecordPutCacheThreadPoolExecutor)
    public ThreadPoolExecutor gameRecordPutCacheThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(20,
                100,
                100L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue(100000),
                new CustomizableThreadFactory("game-record-cache-put-thread-pool-"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.allowCoreThreadTimeOut(true);
        executor.prestartAllCoreThreads();
        return executor;
    }

    @Bean(operationEventThreadPoolExecutor)
    public ThreadPoolExecutor operationEventThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(10,
                100,
                100L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue(10000),
                new CustomizableThreadFactory("operation-event-pool-"),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.allowCoreThreadTimeOut(true);
        executor.prestartAllCoreThreads();
        return executor;
    }

    @Bean(operationEventReportThreadPoolExecutor)
    public ThreadPoolExecutor operationEventReportThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(10,
                100,
                100L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue(10000),
                new CustomizableThreadFactory("operation-event-report-pool-"),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.allowCoreThreadTimeOut(true);
        executor.prestartAllCoreThreads();
        return executor;
    }

    @Bean(starCardUseRecordThreadPoolExecutor)
    public ThreadPoolExecutor starCardUseRecordThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(2,
                100,
                100L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10000),
                new CustomizableThreadFactory("star-card-use-record-pool-"),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.allowCoreThreadTimeOut(true);
        executor.prestartAllCoreThreads();
        return executor;
    }

    @Bean(autoContinuePlayGameThreadPoolExecutor)
    public ThreadPoolExecutor autoContinuePlayGameThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(2,
                8,
                100L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10000),
                new CustomizableThreadFactory("auto-continue-play-game-pool-"),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.allowCoreThreadTimeOut(true);
        executor.prestartAllCoreThreads();
        return executor;
    }

    @Bean(warmUpConnectionThreadPoolExecutor)
    public ThreadPoolExecutor warmUpConnectionThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(120,
                120,
                300L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(300),
                new CustomizableThreadFactory("warm-up-connection-thread-pool-"),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.allowCoreThreadTimeOut(true);
        executor.prestartAllCoreThreads();
        return executor;
    }

    @Bean(scheduledThreadPoolExecutor)
    public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1,
                new CustomizableThreadFactory("scheduled-pool-"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        // executor.allowCoreThreadTimeOut(true);
        executor.prestartAllCoreThreads();
        return executor;
    }

    @Bean(inventoryThreadPoolExecutor)
    public ThreadPoolExecutor inventoryThreadPoolExecutor() {
        WsThreadPoolExecutor executor = new WsThreadPoolExecutor(1,
                10,
                100L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue(1000000),
                new CustomizableThreadFactory("inventory-pool-"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        // executor.allowCoreThreadTimeOut(true);
        executor.prestartAllCoreThreads();
        return executor;
    }
}
