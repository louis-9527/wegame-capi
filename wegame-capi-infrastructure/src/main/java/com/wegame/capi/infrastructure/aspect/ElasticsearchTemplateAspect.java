package com.wegame.capi.infrastructure.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ElasticsearchTemplateAspect {

    @Around("execution(* org.springframework.data.elasticsearch.core.AbstractElasticsearchTemplate.*(..)))  || execution(* org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate.*(..)) ")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Signature signature = joinPoint.getSignature();
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            throw throwable;
        } finally {
            long endTime = System.currentTimeMillis();
            long costTime = endTime - startTime;
            if ( costTime > 20) {
                log.info("{}#{}，elasticsearch call cost time more than 20：{} ms",
                        signature.getDeclaringTypeName(),
                        signature.getName(),
                        costTime);
            } else {
                log.info("{}#{}，elasticsearch call cost time：{} ms",
                        signature.getDeclaringTypeName(),
                        signature.getName(),
                        costTime);
            }
        }
    }
}
