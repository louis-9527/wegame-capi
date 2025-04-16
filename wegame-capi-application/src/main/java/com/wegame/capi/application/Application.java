package com.wegame.capi.application;

import com.alibaba.druid.util.StringUtils;
import com.wegame.base.domain.ServicePublishMessage;
import com.wegame.base.enums.EnvEnum;
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
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PreDestroy;
import java.util.List;
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
    @Value("${lark.publishRobot}")
    private String publishRobot;

    @Autowired
    RedisWarmUp redisWarmUp;
    @Autowired
    List<ExecutorService> executorList;

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
            // 预热
            // 1、redis预热，2分钟内做redis预热
            redisWarmUp.warmUp();
//            if (StringUtils.equals(env, EnvEnum.LOCAL.getValue())) {
//                return;
//            }
            LarkUtil.sendTextMsg(publishRobot, ServicePublishMessage.builder()
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
        executorList.forEach(executor -> {
            try {
                executor.shutdown();
                while (!executor.isTerminated()) {
                    // 等待线程池中所有任务完成
                }
                log.info("executor {} graceful stop success", executor);
            } catch (Exception e) {
                log.error("executor {} graceful stop fail:{}", e.getMessage(), e);
            }
            //            try {
            //                executor.shutdown();
            //                boolean stopFlag = executor.awaitTermination(20, TimeUnit.SECONDS);
            //                if (stopFlag) {
            //                    log.info("executorService graceful stop success ,{},{}", executor, stopFlag);
            //                } else {
            //                    log.info("executorService graceful stop fail after 20 seconds,force stop it,{},{}", executor, stopFlag);
            //                    executor.shutdownNow();
            //                }
            //            } catch (InterruptedException e) {
            //                log.info("executorService graceful stop exception,force stop it,{}", executor);
            //                executor.shutdownNow();
            //            }
        });
    }
}
