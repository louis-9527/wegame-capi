package com.wegame.capi.infrastructure.aspect;

import com.wegame.base.utils.FastJsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RedisTemplateAspect {

    @Around("execution(* org.springframework.data.redis.core.RedisTemplate.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Signature signature = joinPoint.getSignature();
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("调用redis接口异常, 入参：{}, errMsg: {}",
                    FastJsonUtils.toJSONString(joinPoint.getArgs()),
                    throwable.getMessage(),
                    throwable);
            throw throwable;
        } finally {
            log.info("调用redis接口耗时，{}#{}，耗时：{} ms",
                    signature.getDeclaringTypeName(),
                    signature.getName(),
                    (System.currentTimeMillis() - startTime));
        }
    }
}
