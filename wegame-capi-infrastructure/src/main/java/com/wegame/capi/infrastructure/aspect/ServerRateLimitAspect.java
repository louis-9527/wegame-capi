//package com.wegame.capi.infrastructure.aspect;
//
//import com.google.common.util.concurrent.RateLimiter;
//import com.wegame.base.annotation.ServerRateLimit;
//import com.wegame.capi.infrastructure.config.nacos.NacosServerRateLimitConfig;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.annotation.AnnotationUtils;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Method;
//import java.util.Map;
//import java.util.Objects;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * 限流切面
// */
//@Slf4j
//@Aspect
//@Component
//public class ServerRateLimitAspect {
//    private final static Map<ServerRateLimit, RateLimiter> RATE_LIMITER_MAP = new ConcurrentHashMap<>();
//    @Autowired
//    NacosServerRateLimitConfig nacosServerRateLimitConfig;
//
//    @Around("@annotation(serverRateLimit) || @within(serverRateLimit)")
//    public Object aroundAdvice(ProceedingJoinPoint joinPoint, ServerRateLimit serverRateLimit) throws Throwable {
//        // 限流处理
//        this.serverRateLimitHandle(joinPoint);
//        // 继续执行目标方法
//        return joinPoint.proceed();
//    }
//
//    private void serverRateLimitHandle(ProceedingJoinPoint joinPoint) {
//        try {
//            //全局开关是否打开
//            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
//            ServerRateLimit rateLimit = AnnotationUtils.findAnnotation(method, ServerRateLimit.class);
//            if (Objects.isNull(rateLimit)) {
//                rateLimit = AnnotationUtils.findAnnotation(method.getDeclaringClass(), ServerRateLimit.class);
//            }
//
//            // 开关校验
//            if (this.rateLimitSwitchOpen(rateLimit)) {
//                RateLimiter rateLimiter = this.getRateLimiter(rateLimit);
//                // 尝试获取一个许可
//                double waitTime = rateLimiter.acquire();
//                log.info("Request to {} processed after waiting for {} seconds", method.getName(), waitTime);
//            } else {
//                // 清空limiter
//                RATE_LIMITER_MAP.clear();
//            }
//        } catch (Exception e) {
//            log.error("serverRateLimitHandle error", e);
//        }
//    }
//
//    private boolean rateLimitSwitchOpen(ServerRateLimit rateLimit) {
//
//
//        return false;
//    }
//
//    private RateLimiter getRateLimiter(ServerRateLimit rateLimit) {
//        RateLimiter rateLimiter = RATE_LIMITER_MAP.get(rateLimit);
//        if (Objects.isNull(rateLimiter)) {
//            rateLimiter = RateLimiter.create(this.getPermitsPerSecond(rateLimit));
//            RATE_LIMITER_MAP.put(rateLimit, rateLimiter);
//        }
//        return rateLimiter;
//    }
//
//    /**
//     * 获取限流限制 每秒流量
//     *
//     * @param serverRateLimit serverRateLimit
//     * @return 每秒限制流量
//     */
//    private double getPermitsPerSecond(ServerRateLimit serverRateLimit) {
//        // 优先走nacos配置
//        return 5.0;
//    }
//}
