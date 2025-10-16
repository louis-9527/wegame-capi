package com.wegame.capi.api.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * see doc:
 * https://casinoplus.sg.larksuite.com/docx/N3Yvdnhewo6s4ox10cqlklOlgKI?source_type=message&from=message&disposable_login_token=eyJ1c2VyX2lkIjoiNzQ1NzQ0Mjc0NDc0Nzc2OTg4NyIsImRldmljZV9sb2dpbl9pZCI6Ijc0NTc0ODA4NzE1NDk4NjE5MjAiLCJ0aW1lc3RhbXAiOjE3NjA2MDAwMjQsInVuaXQiOiJsYXJrc2dhd3MiLCJwd2RfbGVzc19sb2dpbl9hdXRoIjoiMSIsInZlcnNpb24iOiJ2MyIsInRlbmFudF9icmFuZCI6ImxhcmsiLCJwa2dfYnJhbmQiOiJMYXJrIn0%3D.83e359bddd32aa9864c1205d4db45289571c9123bfb90fcae22b918629786679
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
        headers.add("Keep-Alive", "timeout=58");
        headers.add("uid", uid);
        return headers;
    }

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
