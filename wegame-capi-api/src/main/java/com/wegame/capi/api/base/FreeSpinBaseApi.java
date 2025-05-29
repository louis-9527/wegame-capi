package com.wegame.capi.api.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
public class FreeSpinBaseApi extends CommonBaseApi {

    @Value("${api.game.freeSpinDomain}")
    public String freeSpinDomain;

    public MultiValueMap<String, String> buildHeaders(String uid) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Connection", "Keep-Alive");
        headers.add("Keep-Alive", "timeout=180");
        headers.add("uid", uid);
        return headers;
    }

    public void healthCheck() {
        try {
            ResponseEntity<Object> response = walletRestTemplate.getForEntity(String.format(HEALTH_CHECK_FORMAT, freeSpinDomain), Object.class);
            log.info("freeSpin health check: {}", response);
        } catch (Exception e) {
            log.warn("freeSpin health check error", e);
        }
    }

    //    public void initConnections() {
    //        try {
    //            String url = String.format(HEALTH_CHECK_FORMAT, freeSpinDomain);
    //            IntStream.range(0, 200).forEach(i -> {
    //                try {
    //                    domainWarmUpThreadPoolExecutor.submit(() -> {
    //                        try {
    //                            walletRestTemplate.getForEntity(url, Object.class);
    //                            log.info("promotion health check suc");
    //                        } catch (Exception e) {
    //                            log.warn("promotion health check error", e);
    //                        }
    //                    });
    //                } catch (Exception ignored) {
    //                    // 忽略所有异常
    //                }
    //            });
    //        } catch (Exception e) {
    //            // 忽略所有异常
    //        }
    //    }
}
