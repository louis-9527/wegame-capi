package com.wegame.capi.application;

import com.wegame.base.executor.WsThreadPoolExecutor;
import com.wegame.base.utils.ThreadMdcUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class RedisWarmUp {

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    public void warmUp() {
        try {
            ThreadMdcUtil.setTraceIdIfAbsent();
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            WsThreadPoolExecutor executor = new WsThreadPoolExecutor(1000,
                    1000,
                    30L,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue(1000),
                    new CustomizableThreadFactory("WarmUp-pool-"),
                    new ThreadPoolExecutor.CallerRunsPolicy());
            // 当前请求数量，从 10 开始
            AtomicInteger currentRequestCount = new AtomicInteger(10);
            // 记录开始时间
            final long startTimeMillis = System.currentTimeMillis();
            // 总执行时间 200 秒
            final long totalDurationMillis = 200 * 1000;
            log.info("start redis warm up，请求数量将从 5 逐步增加到 1000，然后保持 1000 请求量执行，总时长 200 秒...");
            // 每秒执行一次任务
            scheduler.scheduleAtFixedRate(() -> {
                // 检查是否已经执行了 200 秒
                long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
                if (elapsedTimeMillis >= totalDurationMillis) {
                    log.info("end redis warm up");
                     closeResource(scheduler, executor);
                    return;
                }
                int requestCount = currentRequestCount.get();
                // 执行批量请求
                executeBatchRequests(requestCount, executor);
                // 如果请求数量小于 1000，则增加下一批次的请求数量
                if (requestCount < 2000) {
                    currentRequestCount.addAndGet(10);
                    log.info("next batch request num: {}", currentRequestCount.get());
                } else {
                    log.info("keep batch request num: {}", currentRequestCount.get());
                }
            }, 0, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("redis warm up error", e);
        }
    }

    /**
     * 执行批量请求
     *
     * @param requestCount 请求数量
     */
    private void executeBatchRequests(int requestCount, ExecutorService executor) {
        try {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            // 发送请求
            for (int i = 0; i < requestCount; i++) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        sendRequest();
                    } catch (Exception e) {
                        log.error("send request fail:{}", e.getMessage(), e);
                    }
                }, executor);
                futures.add(future);
            }
            // 等待所有请求完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            log.error("executeBatchRequests error fail", e);
        }
    }

    private void sendRequest() {
        try {
            RedisClusterConnection connection = redisTemplate.getConnectionFactory().getClusterConnection();
            connection.ping();
        } catch (Exception e) {
            log.error("sendRequest error", e);
        }
    }

    private static void closeResource(ScheduledExecutorService scheduler, WsThreadPoolExecutor executor) {
        // 添加关闭钩子，确保程序退出时能够正确关闭线程池
        // close scheduler
        try {
            scheduler.shutdown();
            // 等待任务完成
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        // close executor
        try {
            // 等待任务完成
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        log.info("stop redis warm up，resource closed");
    }
}
