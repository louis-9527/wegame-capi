//package com.wegame.capi.infrastructure.config.nacos;
//
//import com.wegame.capi.common.domain.ServerRateLimitConfigDo;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//
///**
// * Nacos 服务限流配置
// */
//@Component
////@ConfigurationProperties(prefix = "server-rate-limit")
//public class NacosServerRateLimitConfig {
//    private final Map<String, ServerRateLimitConfigDo> configDoMap = new HashMap<>();
//    private ServerRateLimitConfigDo serverRateLimitConfig;
//
//    public void setServerRateLimitConfig(ServerRateLimitConfigDo serverRateLimitConfig) {
//        this.serverRateLimitConfig = serverRateLimitConfig;
//        this.initConfigDoMap();
//    }
//
//    private void initConfigDoMap() {
//        if (Objects.isNull(serverRateLimitConfig)) {
//            return;
//        }
//
//        if (Objects.nonNull(serverRateLimitConfig.getGlobal())) {
//
//
//        }
//
//
//    }
//
//    public ServerRateLimitConfigDo getServerRateLimit() {
////        return Optional.ofNullable(serverRateLimitConfig).orElse(defaultConfigDo);
//        return serverRateLimitConfig;
//    }
//
//}
