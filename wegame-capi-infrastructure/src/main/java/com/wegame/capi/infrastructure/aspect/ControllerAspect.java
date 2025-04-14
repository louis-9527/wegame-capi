package com.wegame.capi.infrastructure.aspect;

import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class ControllerAspect {

    @Around("execution(* com.wegame.capi..*Controller.*(..))")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = proceedingJoinPoint.proceed();
        } finally {
            statistic(proceedingJoinPoint, result, startTime);
        }
        return result;
    }

    private void statistic(ProceedingJoinPoint proceedingJoinPoint, Object result, long startTime) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
            Object req = null;
            if (AnnotationUtils.findAnnotation(signature.getMethod(), GetMapping.class) != null) {
                req = request.getParameterMap();
            } else if (AnnotationUtils.findAnnotation(signature.getMethod(), PostMapping.class) != null) {
                Object[] args = proceedingJoinPoint.getArgs();
                req = Arrays.stream(args)
                        .filter(arg -> (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)))
                        .collect(Collectors.toList());
            }
            UserInfo userInfo =UserInfoContext.getUserInfo();

            long costTime = System.currentTimeMillis() - startTime;
            if (costTime > 150L) {
                log.info("{}#{}, cost times more than 150: {} ms, req={}, res={},playerId={}",
                        signature.getDeclaringTypeName(),
                        signature.getName(),
                        costTime,
                        req,
                        result,
                        userInfo.getPlayerId());
            } else {
                log.info("{}#{}, cost times: {} ms, req={}, res={},playerId={}",
                        signature.getDeclaringTypeName(),
                        signature.getName(),
                        costTime,
                        req,
                        result,
                        userInfo.getPlayerId());
            }
        } catch (Throwable e) {
            log.error("ControllerAspect.statistic error: {}", e.getMessage(), e);
        }
    }
}

