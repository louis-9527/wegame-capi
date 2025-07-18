package com.wegame.capi.api.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.warmUpConnectionThreadPoolExecutor;

@Slf4j
public class PromotionBaseApi extends CommonBaseApi {

    @Value("${api.game.promotionDomain}")
    public String promotionDomain;

    public MultiValueMap<String, String> buildHeaders(String uid) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Connection", "Keep-Alive");
        headers.add("Keep-Alive", "timeout=550");
        headers.add("uid", uid);
        return headers;
    }

    @Async(warmUpConnectionThreadPoolExecutor)
    @Override
    public void healthCheck() {
        try {
            String url = String.format(HEALTH_CHECK_FORMAT, promotionDomain, System.currentTimeMillis());
            ResponseEntity<Object> response = promotionRestTemplate.getForEntity(url, Object.class);
            log.info("promotion health check: {}", response);
        } catch (Exception e) {
            log.warn("promotion health check error", e);
        }
    }
}
