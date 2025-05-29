//package com.wegame.capi.scheduler.monitor;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
//import org.apache.http.pool.PoolStats;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
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
//    List<PoolingHttpClientConnectionManager> managerList;
//
//    /**
//     * 定时任务
//     */
//    @Scheduled(cron = "0 * * * * ?")
//    public void schedule() {
//        log.info("Scheduled execute start");
//        statisticPool();
//        log.info("Scheduled execute end");
//    }
//
//    private void statisticPool() {
//        Optional.ofNullable(managerList)
//                .orElse(Collections.emptyList())
//                .forEach(connectionManager -> {
//                    log.info("connection manager: {}", connectionManager);
//                    log.info("connection manager status: {}", connectionManager.getTotalStats());
//                });
//    }
//}
