//package com.wegame.capi.api.base;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.warmUpConnectionThreadPoolExecutor;
//
//@Slf4j
//@Service
//public class TestApi extends CommonBaseApi {
//
//    @Async(warmUpConnectionThreadPoolExecutor)
//    public void ping() {
//        try {
//            String url = "http://server1.makillingslot.net/capi/game/common/ping";
//            ResponseEntity<Object> response = walletRestTemplate.getForEntity(url, Object.class);
//            log.error("ping response: {}", response);
//        } catch (Exception e) {
//            // 忽略所有异常
//        }
//    }
//
//    @Async(warmUpConnectionThreadPoolExecutor)
//    @Override
//    public void healthCheck() {
//        try {
//            String url = "http://server1.makillingslot.net/capi/game/common/ping";
//            ResponseEntity<Object> response = walletRestTemplate.getForEntity(url, Object.class);
//            log.info("singleWallet health check: {}", response);
//        } catch (Exception e) {
//            log.warn("singleWallet health check error", e);
//        }
//    }
//}
