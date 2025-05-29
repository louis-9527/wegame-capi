package com.wegame.capi.api.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import java.util.stream.IntStream;

@Slf4j
public class SingleWalletBaseApi extends CommonBaseApi {

    private static final String HEALTH_CHECK_FORMAT = "%s/v1/health-check";

    @Value("${api.game.singleDomain}")
    public String singleDomain;


    public MultiValueMap<String, String> buildHeaders(String uid) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Connection", "Keep-Alive");
        headers.add("Keep-Alive", "timeout=180");
        headers.add("uid", uid);
        return headers;
    }

    public void initConnections() {
        try {
            String url = String.format(HEALTH_CHECK_FORMAT, singleDomain);
            // String url = "http://server1.makillingslot.net/capi/game/common/ping";
            IntStream.range(0, 200).forEach(i -> {
                try {
                    domainWarmUpThreadPoolExecutor.submit(() -> {
                        try {
                            walletRestTemplate.getForEntity(url, Object.class);
                            log.info("singleWallet health check response suc");
                        } catch (Exception e) {
                            log.warn("singleWallet health check error", e);
                        }
                    });
                } catch (Exception ignored) {
                    // 忽略所有异常
                }
            });
        } catch (Exception e) {
            // 忽略所有异常
        }
    }
}
