//package com.wegame.capi.scheduler.monitor;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//
///**
// * @author apple
// */
//@Slf4j
//@Service
//public class RestTemplateConnectionPoolMonitorScheduler {
//
//    @Autowired
//    PoolingHttpClientConnectionManager walletConnectionManager;
//    @Autowired
//    PoolingHttpClientConnectionManager freeSpinConnectionManager;
//    @Autowired
//    PoolingHttpClientConnectionManager promotionConnectionManager;
//
//    /**
//     * 定时任务
//     */
//    @Scheduled(cron = "0 * * * * ?")
//    public void schedule() {
//        log.info("Scheduled execute start");
//        log.info("walletConnectionManager status: {}", walletConnectionManager.getTotalStats());
//        log.info("freeSpinConnectionManager status: {}", freeSpinConnectionManager.getTotalStats());
//        log.info("promotionConnectionManager status: {}", promotionConnectionManager.getTotalStats());
//        log.info("Scheduled execute end");
//    }
//}