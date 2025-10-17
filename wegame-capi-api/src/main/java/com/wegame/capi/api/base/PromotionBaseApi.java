package com.wegame.capi.api.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * see doc:
 * https://casinoplus.sg.larksuite.com/docx/O5d4dhv05ouRRTxdLA9lPNKzgxb
 *
 */
@Slf4j
public class PromotionBaseApi extends CommonBaseApi {

    public static final String PROMOTION_PREFIX_ERR_CODE = "2";

    @Value("${api.game.promotionDomain}")
    public String promotionDomain;

    public MultiValueMap<String, String> buildHeaders(String uid) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Connection", "Keep-Alive");
        headers.add("Keep-Alive", "timeout=59");
        headers.add("uid", uid);
        return headers;
    }

    public void healthCheck() {
        try {
            ResponseEntity<Object> response = promotionRestTemplate.getForEntity(String.format(HEALTH_CHECK_FORMAT, promotionDomain), Object.class);
            log.info("promotion health check: {}", response);
        } catch (Exception e) {
            log.warn("promotion health check error", e);
        }
    }
}
