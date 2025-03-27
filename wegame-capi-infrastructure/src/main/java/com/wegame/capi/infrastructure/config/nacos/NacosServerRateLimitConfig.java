//package com.wegame.capi.infrastructure.config.nacos;
//
//import com.wegame.base.utils.FastJsonUtils;
//import com.wegame.capi.common.domain.ServerRateLimitConfigDo;
//import com.wegame.capi.common.domain.ServerRateLimitConfigMapDo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//import java.util.Objects;
//
///**
// * Nacos 服务限流配置
// */
//@Slf4j
//@Component
//@ConfigurationProperties(prefix = "server-rate-limit")
//public class NacosServerRateLimitConfig {
//    /**
//     * 配置map
//     */
//    private ServerRateLimitConfigMapDo configMapDo;
//
//    public void setServerRateLimitConfig(ServerRateLimitConfigDo serverRateLimitConfig) {
//        log.info("[NacosServerRateLimitConfig] 接收到新的限流配置:{}", FastJsonUtils.toJSONString(serverRateLimitConfig));
//        configMapDo = ServerRateLimitConfigMapDo.build(serverRateLimitConfig);
//    }
//
//    /**
//     * 初始化配置map
//     *
//     * @param serverRateLimitConfig nacos配置中心配置
//     */
//    private void initConfigMapDo(ServerRateLimitConfigDo serverRateLimitConfig) {
//        configMapDo.clear();
//
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
//}
