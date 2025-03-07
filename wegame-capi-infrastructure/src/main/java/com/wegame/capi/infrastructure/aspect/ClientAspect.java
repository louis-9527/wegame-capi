//package com.wegame.capi.aspect;
//
//import com.wegame.base.utils.FastJsonUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.Signature;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Aspect
//@Component
//public class ClientAspect {
//
//    @Around("execution(* com.wegame.capi..*Client.*(..))")
//    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        long startTime = System.currentTimeMillis();
//        Object result = proceedingJoinPoint.proceed();
//        Signature signature = proceedingJoinPoint.getSignature();
//        log.info("第三方接口调用耗时，{}#{}, req={}, res={}, cost times: {} ms",
//                signature.getDeclaringTypeName(),
//                signature.getName(),
//                FastJsonUtils.toJSONString(proceedingJoinPoint.getArgs()),
//                FastJsonUtils.toJSONString(result),
//                System.currentTimeMillis() - startTime);
//        return result;
//    }
//}
//
