//package com.wegame.capi.infrastructure.aspect;
//
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
//public class MalakasGameModuleAspect {
//
//    @Around("execution(* com.wegame.capi..module.malakas.game.MalakasGameNormalPlay.play(..))")
//    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
//        long startTime = System.currentTimeMillis();
//        Signature signature = joinPoint.getSignature();
//        try {
//            return joinPoint.proceed();
//        } catch (Throwable throwable) {
//            log.error("MalakasGame#play, param：{}, errMsg: {}",
//                    joinPoint.getArgs(),
//                    throwable.getMessage(),
//                    throwable);
//            throw throwable;
//        } finally {
//            long costTime = System.currentTimeMillis() - startTime;
//            if (costTime > 10L) {
//                log.info("MalakasGame#play，{}#{}，COST TIME MORE THAN 10：{} ms",
//                        signature.getDeclaringTypeName(),
//                        signature.getName(),
//                        (System.currentTimeMillis() - startTime));
//            } else {
//                log.info("MalakasGame#play，{}#{}，COST TIME：{} ms",
//                        signature.getDeclaringTypeName(),
//                        signature.getName(),
//                        (System.currentTimeMillis() - startTime));
//            }
//        }
//    }
//}
//
