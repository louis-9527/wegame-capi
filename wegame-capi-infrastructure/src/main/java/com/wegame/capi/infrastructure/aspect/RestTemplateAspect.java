package com.wegame.capi.infrastructure.aspect;

import com.wegame.base.utils.FastJsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RestTemplateAspect {

    @Around("execution(* org.springframework.web.client.RestTemplate.*(..))")
    public Object profileAllRestTemplateMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Signature signature = joinPoint.getSignature();
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("调用第三方接口异常, 入参：{}, errMsg: {}",
                    FastJsonUtils.toJSONString(joinPoint.getArgs()),
                    throwable.getMessage(),
                    throwable);
            // todo 埋点上报
            throw throwable;
        } finally {
            // req req cost_time url suc_fail
            // todo 埋点上报
            log.info("调用第三方接口耗时，{}#{}，耗时：{} ms",
                    signature.getDeclaringTypeName(),
                    signature.getName(),
                    (System.currentTimeMillis() - startTime));
        }
    }

    @Around("execution(* org.springframework.web.client.RestTemplate.exchange(..))")
    public Object logRestCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String url = "?";
        String req = "?";
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            if (args[0] instanceof String) {
                url = (String) args[0];
            }
            if (args[0] instanceof RequestEntity) {
                RequestEntity<?> argEntity = (RequestEntity<?>) args[0];
                url = argEntity.getUrl().toString();
                req = FastJsonUtils.toJSONString(argEntity.getBody());
            }
        }

        long start = System.currentTimeMillis();
        Object response = joinPoint.proceed();
        long end = System.currentTimeMillis();

        log.info("URL: {}; Request: {}; Response: {}; Cost Time: {}ms", url, req, response, (end - start));
        return response;
    }
}

