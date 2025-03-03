package com.wegame.capi.application;

import com.alibaba.druid.util.StringUtils;
import com.wegame.base.domain.ServicePublishMessage;
import com.wegame.base.enums.EnvEnum;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.base.utils.IpUtil;
import com.wegame.base.utils.LarkUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.boot.web.server.GracefulShutdownCallback;
import org.springframework.boot.web.server.GracefulShutdownResult;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 启动类
 *
 * @author apple
 */
@Slf4j
@EnableScheduling
@EnableAsync
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
    @Value("${lark.robot}")
    private String robot;

    @Autowired
    List<ExecutorService> allExecutors;

    public static void main(String[] args) {
        log.info("Begin Start Application 😄😄😄😄😄😄");
        SpringApplication.run(Application.class, args);
        log.info("Started Application 🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉");
        log.info("see doc: http://localhost:9500/capi/game/doc.html#/home");
    }

    @Override
    public void run(String... args) {
        try {
            log.info("start Application after env: {}", env);
            if (StringUtils.equals(env, EnvEnum.LOCAL.getValue())) {
                return;
            }
            LarkUtil.sendTextMsg(robot, ServicePublishMessage.builder()
                    .applicationName(appName)
                    .env(env)
                    .ip(IpUtil.getLocalIp())
                    .build().buildMessage());
        } catch (Throwable te) {
            log.warn("send publish message fail:{}", te.getMessage(), te);
        }

    }

    @PreDestroy
    public void destroy() {
        log.info("start destroy executor before destroy");
        allExecutors.forEach(executorService -> {
            try {
                executorService.shutdown();
                boolean stopFlag = executorService.awaitTermination(20, TimeUnit.SECONDS);
                if (stopFlag) {
                    log.info("executorService graceful stop success ,{},{}", executorService, stopFlag);
                } else {
                    log.info("executorService graceful stop fail after 20 seconds,force stop it,{},{}", executorService, stopFlag);
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                log.info("executorService graceful stop exception,force stop it,{}", executorService);
                executorService.shutdownNow();
            }
        });
    }
}
