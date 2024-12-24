package com.wegame.capi.scheduler;

import cn.hutool.core.util.NumberUtil;
import com.wegame.base.utils.FastJsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author apple
 */
@Slf4j
@Service
public class ThreadPoolMonitorScheduler {

    @Autowired
    Map<String, java.util.concurrent.ThreadPoolExecutor>  threadPoolExecutorMap;
    @Autowired
    WebServerApplicationContext webServerApplicationContext;
    @Autowired
    TomcatServletWebServerFactory factory;

    /**
     * todo 指定一个单独的线程池
     * 定时任务
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void schedule() {
        log.info("Scheduled execute start");
        statisticTomcatThreadPool();
        statisticTomcatConnectPool();
        statisticBusinessThreadPool();
        statisticsRunningTimeInfo();
    }

    public void statisticsRunningTimeInfo() {
        Runtime runtime = Runtime.getRuntime();

        long totalMemory = runtime.totalMemory(); // 总内存
        long freeMemory = runtime.freeMemory(); // 可用内存
        long usedMemory = totalMemory - freeMemory; // 已使用内存
        long maxMemory = runtime.maxMemory(); // 最大"内存

        Map<String, Object> runingMap = new LinkedHashMap<>();
        runingMap.put("Total Memory", NumberUtil.div(totalMemory, 1000 * 1000, 0) + "MB");
        runingMap.put("Free Memory", NumberUtil.div(freeMemory, 1000 * 1000, 0) + "MB");
        runingMap.put("Used Memory", NumberUtil.div(usedMemory, 1000 * 1000, 0) + "MB");
        runingMap.put("Max Memory", NumberUtil.div(maxMemory, 1000 * 1000, 0) + "MB");

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        runingMap.put("heap Memory Committed: ", NumberUtil.div(memoryMXBean.getHeapMemoryUsage().getCommitted(), 1000 * 1000, 0) + "MB");
        runingMap.put("heap Memory Used: ", NumberUtil.div(memoryMXBean.getHeapMemoryUsage().getUsed(), 1000 * 1000, 0) + "MB");
        runingMap.put("heap Memory Max: ", NumberUtil.div(memoryMXBean.getHeapMemoryUsage().getMax(), 1000 * 1000, 0) + "MB");
        runingMap.put("non heap Committed: ", NumberUtil.div(memoryMXBean.getNonHeapMemoryUsage().getCommitted(), 1000 * 1000, 0) + "MB");
        runingMap.put("non heap Memory Used: ", NumberUtil.div(memoryMXBean.getNonHeapMemoryUsage().getUsed(), 1000 * 1000, 0) + "MB");
        runingMap.put("non heap Memory Max: ", NumberUtil.div(memoryMXBean.getNonHeapMemoryUsage().getMax(), 1000 * 1000, 0) + "MB");

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        runingMap.put("Thread count", threadMXBean.getThreadCount());

        log.info("running_info: {}", FastJsonUtils.toJSONString(runingMap));
    }

    public void statisticTomcatThreadPool() {
        //获取webServer线程池
        ThreadPoolExecutor executor = (ThreadPoolExecutor) ((TomcatWebServer) webServerApplicationContext.getWebServer())
                .getTomcat()
                .getConnector()
                .getProtocolHandler()
                .getExecutor();

        ((TomcatWebServer) webServerApplicationContext.getWebServer())
                .getTomcat()
                .getConnector()
                .getProtocolHandler();
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("核心线程数", String.valueOf(executor.getCorePoolSize()));
        map.put("最大线程数", String.valueOf(executor.getMaximumPoolSize()));
        map.put("活跃线程数", String.valueOf(executor.getActiveCount()));
        map.put("池中当前线程数", String.valueOf(executor.getPoolSize()));
        map.put("历史最大线程数", String.valueOf(executor.getLargestPoolSize()));
        map.put("最大线程数", String.valueOf(executor.getMaximumPoolSize()));
        map.put("线程允许空闲时间/s", String.valueOf(executor.getKeepAliveTime(TimeUnit.SECONDS)));
        map.put("核心线程数是否允许被回收", String.valueOf(executor.allowsCoreThreadTimeOut()));
        map.put("提交任务总数", String.valueOf(executor.getSubmittedCount()));
        //        map.put("历史执行任务的总数(近似值)", String.valueOf(executor.getTaskCount()));
        //        map.put("历史完成任务的总数(近似值)", String.valueOf(executor.getCompletedTaskCount()));
        //        map.put("工作队列任务数量", String.valueOf(executor.getQueue().size()));
        map.put("拒绝策略", executor.getRejectedExecutionHandler().getClass().getSimpleName());
        log.info("tomcat_thread_pool_info: {}", FastJsonUtils.toJSONString(map));
    }

    public void statisticTomcatConnectPool() {
        Connector connector = ((TomcatWebServer) webServerApplicationContext.getWebServer()).getTomcat().getConnector();
        AbstractProtocol protocolHandler = (AbstractProtocol) connector.getProtocolHandler();

        // 打印连接池信息（配置信息）
        Map<String, Object> configMap = new LinkedHashMap<>();
        configMap.put("最小线程数", protocolHandler.getMinSpareThreads());
        configMap.put("最大线程数", protocolHandler.getMaxThreads());
        configMap.put("连接超时时间", protocolHandler.getConnectionTimeout());
        configMap.put("最大连接数", protocolHandler.getMaxConnections());
        configMap.put("连接等待队列容量", protocolHandler.getAcceptCount());
        log.info("tomcat_connect_pool_config_info: {}", FastJsonUtils.toJSONString(configMap));
        // 打印连接池信息（运行时信息）
        Map<String, Object> runningMap = new LinkedHashMap<>();
//        runningMap.put("实时连接数", protocolHandler.getCurrentThreadCount());
//        runningMap.put("剩余连接数", protocolHandler.getMaxConnections() - protocolHandler.getCurrentThreadCount());
        log.info("tomcat_connect_pool_running_info: {}", FastJsonUtils.toJSONString(runningMap));
    }

    public void statisticBusinessThreadPool() {
        threadPoolExecutorMap.forEach((name, executor) -> {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("核心线程数", String.valueOf(executor.getCorePoolSize()));
            map.put("最大线程数", String.valueOf(executor.getMaximumPoolSize()));
            map.put("活跃线程数", String.valueOf(executor.getActiveCount()));
            map.put("池中当前线程数", String.valueOf(executor.getPoolSize()));
            map.put("历史最大线程数", String.valueOf(executor.getLargestPoolSize()));
            map.put("最大线程数", String.valueOf(executor.getMaximumPoolSize()));
            map.put("线程允许空闲时间/s", String.valueOf(executor.getKeepAliveTime(TimeUnit.SECONDS)));
            map.put("核心线程数是否允许被回收", String.valueOf(executor.allowsCoreThreadTimeOut()));
            //        map.put("提交任务总数", String.valueOf(executor.getSubmittedCount()));
            //        map.put("历史执行任务的总数(近似值)", String.valueOf(executor.getTaskCount()));
            //        map.put("历史完成任务的总数(近似值)", String.valueOf(executor.getCompletedTaskCount()));
            map.put("工作队列任务数量", String.valueOf(executor.getQueue().size()));
            map.put("拒绝策略", executor.getRejectedExecutionHandler().getClass().getSimpleName());
            log.info("{}: {}", name, FastJsonUtils.toJSONString(map));
        });
    }

}
