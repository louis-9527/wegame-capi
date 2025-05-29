//package com.wegame.capi.api.base;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//
//import java.util.stream.IntStream;
//
//@Slf4j
//@Service
//public class TestApi extends CommonBaseApi {
//
//    public void initConnections() {
//        try {
//            String url = "http://server1.makillingslot.net/capi/game/common/ping";
//            IntStream.range(0, 200).forEach(i -> {
//                try {
////                    domainWarmUpThreadPoolExecutor.submit(() -> {
//                        try {
//                            walletRestTemplate.getForEntity(url, Object.class);
//                            log.info("singleWallet health check response suc");
//                        } catch (Exception e) {
//                            log.warn("singleWallet health check error", e);
//                        }
////                    });
//                } catch (Exception ignored) {
//                    // 忽略所有异常
//                }
//            });
//        } catch (Exception e) {
//            // 忽略所有异常
//        }
//    }
//}
