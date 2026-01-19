package com.wegame.capi.api.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
public class FreeSpinBaseApi extends CommonBaseApi {

    //    @Value("${api.game.freeSpinDomain}")
    //    public String freeSpinDomain;

    public MultiValueMap<String, String> buildHeaders(String uid) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Connection", "Keep-Alive");
        headers.add("Keep-Alive", "timeout=59");
        headers.add("uid", uid);
        return headers;
    }

    //    public void healthCheck() {
    //        try {
    //            ResponseEntity<Object> response = freeSpinRestTemplate.getForEntity(String.format(HEALTH_CHECK_FORMAT, freeSpinDomain), Object.class);
    //            log.info("freeSpin health check: {}", response);
    //        } catch (Exception e) {
    //            log.warn("freeSpin health check error", e);
    //        }
    //    }
}
