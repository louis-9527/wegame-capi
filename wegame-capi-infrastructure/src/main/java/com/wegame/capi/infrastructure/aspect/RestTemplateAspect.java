package com.wegame.capi.infrastructure.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Aspect
@Component
public class RestTemplateAspect {

    //    @Around("execution(* org.springframework.web.client.RestTemplate.*(..))")
    //    public Object profileAllRestTemplateMethods(ProceedingJoinPoint joinPoint) throws Throwable {
    //        long startTime = System.currentTimeMillis();
    //        Signature signature = joinPoint.getSignature();
    //        try {
    //            return joinPoint.proceed();
    //        } catch (Throwable throwable) {
    //            log.error("调用第三方接口异常, 入参：{}, errMsg: {}",
    //                    FastJsonUtils.toJSONString(joinPoint.getArgs()),
    //                    throwable.getMessage(),
    //                    throwable);
    //            throw throwable;
    //        } finally {
    //            // req req cost_time url suc_fail
    //            log.info("调用第三方接口耗时，{}#{}，耗时：{} ms",
    //                    signature.getDeclaringTypeName(),
    //                    signature.getName(),
    //                    (System.currentTimeMillis() - startTime));
    //        }
    //    }

    @Around("execution(* org.springframework.web.client.RestTemplate.exchange(..))")
    public Object logRestCall(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestEntity<?> argEntity = null;
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            if (args[0] instanceof RequestEntity) {
                argEntity = (RequestEntity<?>) args[0];
            }
        }
        long start = System.currentTimeMillis();
        log.info("before invoke third, time: {}", new Date());
        Object response = null;
        try {
            response = joinPoint.proceed();
            return response;
        } finally {
            long costTime = System.currentTimeMillis() - start;
            if (costTime > 50L) {
                log.info("invoke third, cost time more than 50: {}ms, Request: {}; Response: {}", costTime, argEntity, response);
            } else {
                log.info("invoke third, cost time: {}ms, Request: {}; Response: {}", costTime, argEntity, response);
            }
        }
    }
}

