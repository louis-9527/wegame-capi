package com.wegame.capi.api.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.warmUpConnectionThreadPoolExecutor;

@Slf4j
public class FreeSpinBaseApi extends CommonBaseApi {

    @Value("${api.game.freeSpinDomain}")
    public String freeSpinDomain;

    public MultiValueMap<String, String> buildHeaders(String uid) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Connection", "Keep-Alive");
        headers.add("Keep-Alive", "timeout=59");
        headers.add("uid", uid);
        return headers;
    }

    @Async(warmUpConnectionThreadPoolExecutor)
    @Override
    public void healthCheck() {
        try {
            String url = String.format(HEALTH_CHECK_FORMAT, freeSpinDomain, System.currentTimeMillis());
            ResponseEntity<Object> response = freeSpinRestTemplate.getForEntity(url, Object.class);
            log.info("freeSpin health check: {}", response);
        } catch (Exception e) {
            log.warn("freeSpin health check error", e);
        }
    }
}
