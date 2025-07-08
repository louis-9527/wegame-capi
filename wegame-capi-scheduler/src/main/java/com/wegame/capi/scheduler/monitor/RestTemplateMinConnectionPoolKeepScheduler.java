package com.wegame.capi.scheduler.monitor;

import com.wegame.capi.biz.listener.RestTemplateWarmUpHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


/**
 * @author apple
 */
@Slf4j
@Service
public class RestTemplateMinConnectionPoolKeepScheduler {

    @Autowired
    RestTemplateWarmUpHelper restTemplateWarmUpHelper;

    //    @Scheduled(fixedRate = 30_000) // 每30秒执行
    @Scheduled(cron = "0/15 * * * * ?")
    public void keepMinConnections() {
        restTemplateWarmUpHelper.keepAllConnectionPool();
    }
}