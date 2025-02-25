//package com.wegame.capi.infrastructure.aspect;
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
//public class GameAspect {
//
//    @Around("execution(* com.wegame.capi..module.base.game.Game.play(..)))")
//    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
//        long startTime = System.currentTimeMillis();
//        Signature signature = joinPoint.getSignature();
//        try {
//            return joinPoint.proceed();
//        } catch (Throwable throwable) {
//            log.error("GameAspect#play, param：{}, errMsg: {}",
//                    FastJsonUtils.toJSONString(joinPoint.getArgs()),
//                    throwable.getMessage(),
//                    throwable);
//            throw throwable;
//        } finally {
//            long costTime = System.currentTimeMillis() - startTime;
//            if (costTime > 10L) {
//                log.info("GameAspect#play，{}#{}，COST TIME MORE THAN 10：{} ms",
//                        signature.getDeclaringTypeName(),
//                        signature.getName(),
//                        (System.currentTimeMillis() - startTime));
//            } else {
//                log.info("GameAspect#play，{}#{}，COST TIME：{} ms",
//                        signature.getDeclaringTypeName(),
//                        signature.getName(),
//                        (System.currentTimeMillis() - startTime));
//            }
//        }
//    }
//}
