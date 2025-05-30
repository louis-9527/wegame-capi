package com.wegame.capi.api.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
public class SingleWalletBaseApi extends CommonBaseApi {

    @Value("${api.game.singleDomain}")
    public String singleDomain;

    public MultiValueMap<String, String> buildHeaders(String uid) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Connection", "Keep-Alive");
        headers.add("Keep-Alive", "timeout=1800");
        headers.add("uid", uid);
        return headers;
    }

    public void healthCheck() {
        try {
            ResponseEntity<Object> response = walletRestTemplate.getForEntity(String.format(HEALTH_CHECK_FORMAT, singleDomain), Object.class);
            log.info("singleWallet health check: {}", response);
        } catch (Exception e) {
            log.warn("singleWallet health check error", e);
        }
    }
}
