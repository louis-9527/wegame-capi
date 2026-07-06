package com.wegame.capi.application;

import cn.hutool.core.lang.UUID;
import com.alibaba.druid.util.StringUtils;
import com.wegame.base.domain.ServicePublishMessage;
import com.wegame.base.domain.ServiceShutdownMessage;
import com.wegame.base.enums.EnvEnum;
import com.wegame.base.utils.IpUtil;
import com.wegame.base.utils.LarkUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.biz.listener.AppLaunchPostProcessor;
import com.wegame.capi.infrastructure.config.properties.GameProperty;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.mybatis.spring.annotation.MapperScan;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * 启动类
 *
 * @author apple
 */
@Slf4j
@EnableScheduling
@EnableAsync
@EnableRetry
@EnableCaching
@EnableTransactionManagement
@MapperScan(value = {"com.wegame.capi.dao.*"})
@SpringBootApplication(scanBasePackages = {"com.wegame.*"}, exclude = {
        RedissonAutoConfiguration.class,
        SessionAutoConfiguration.class,
        MongoAutoConfiguration.class})
public class Application implements CommandLineRunner {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${spring.application.env}")
    private String env;
    @Value("${spring.application.version}")
    private String version;
    @Value("${lark.publishRobot}")
    private String publishRobot;
    @Value("${lark.shutDownRobot}")
    private String shutDownRobot;
    @Autowired
    List<Executor> executorList;
    @Autowired
    AppLaunchPostProcessor appLaunchPostProcessor;
    @Autowired
    GameProperty gameProperty;
    @Value("${game.enableRedis}")
    private String enableRedis;

    public static void main(String[] args) {
        MDC.put(ThreadMdcUtil.TRACE_ID, UUID.randomUUID().toString(true));
        log.info("begin launch application ... ...");
        SpringApplication.run(Application.class, args);
        log.info("success launch application 🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉");
        log.info("see doc: http://localhost:9500/capi/game/doc.html#/home");
    }

    @Override
    public void run(String... args) {
        log.info("wegame-capi after launch application, game: {}, env: {}, enableRedis, version: {}",
                gameProperty.enabledGameNames(),
                env,
                enableRedis,
                version);
        sendPublishMessage();
        appLaunchPostProcessor.postProcess();
    }

    @PreDestroy
    public void destroy() {
        log.info("start destroy executor before destroy");
        executorList.forEach(item -> {
            if (item instanceof ExecutorService) {
                gracefulShutdown((ExecutorService) item);
            } else if (item instanceof ThreadPoolTaskScheduler) {
                gracefulShutdown((ThreadPoolTaskScheduler) item);
            } else {
                log.error("unknown executor: {}", item);
            }
        });
        sendShutdownMessage();
    }

    private void gracefulShutdown(ExecutorService executorService) {
        try {
            log.info("before executor gracefulShutdown: {}", executorService);
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                // 等待线程池中所有任务完成
            }
            log.info("after executor graceful: {}", executorService);
        } catch (Exception e) {
            log.error("executor gracefulShutdown error:{}", executorService, e);
        }
    }

    private void gracefulShutdown(ThreadPoolTaskScheduler taskScheduler) {
        try {
            log.info("before executor gracefulShutdown: {}", taskScheduler.getScheduledExecutor());
            taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
            taskScheduler.shutdown();
            gracefulShutdown(taskScheduler.getScheduledExecutor());
            gracefulShutdown(taskScheduler.getScheduledThreadPoolExecutor());
            log.info("after executor graceful: {}", taskScheduler);
        } catch (Exception e) {
            log.error("executor gracefulShutdown error:{}", taskScheduler, e);
        }
    }

    private void sendPublishMessage() {
        try {
            if (StringUtils.equals(env, EnvEnum.LOCAL.getValue())) {
                return;
            }
            LarkUtil.sendTextMsg(publishRobot, ServicePublishMessage.builder()
                    .applicationName(appName)
                    .env(env)
                    .version(version)
                    .gameNames(gameProperty.enabledGameNames())
                    .ip(IpUtil.getLocalIp())
                    .build().buildMessage());
        } catch (Exception e) {
            log.warn("send publish message fail:{}", e.getMessage(), e);
        }
    }

    private void sendShutdownMessage() {
        try {
            if (StringUtils.equals(env, EnvEnum.LOCAL.getValue())) {
                return;
            }
            LarkUtil.sendTextMsg(shutDownRobot, ServiceShutdownMessage.builder()
                    .applicationName(appName)
                    .env(env)
                    .version(version)
                    .gameNames(gameProperty.enabledGameNames())
                    .ip(IpUtil.getLocalIp())
                    .build().buildMessage());
        } catch (Throwable te) {
            log.warn("send shutdown message fail:{}", te.getMessage(), te);
        }
    }
}
