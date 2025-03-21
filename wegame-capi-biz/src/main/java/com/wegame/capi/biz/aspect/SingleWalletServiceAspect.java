package com.wegame.capi.biz.aspect;

import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.api.domain.res.singlewallet.BetStatusRes;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@Aspect
@Component
public class SingleWalletServiceAspect {

    @Around("execution(* com.wegame.capi..service.SingleWalletService.authLoginToken(..))")
    public Object authLoginTokenAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            UserInfo userInfo = UserInfoContext.getUserInfo();
            if (Objects.isNull(userInfo) || !StringUtils.equals(userInfo.getGameId(), GameTypeEnum.MARIA_MAKILING.getId())) {
                return joinPoint.proceed();
            } else {
                return true;
            }
        } catch (Throwable throwable) {
            log.error("SingleWalletService#authLoginToken, param：{}, errMsg: {}",
                    FastJsonUtils.toJSONString(joinPoint.getArgs()),
                    throwable.getMessage(),
                    throwable);
            throw throwable;
        }
    }

    @Around("execution(* com.wegame.capi..service.SingleWalletService.getCredit(..))")
    public Object getCreditAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            UserInfo userInfo = UserInfoContext.getUserInfo();
            if (Objects.isNull(userInfo) || !StringUtils.equals(userInfo.getGameId(), GameTypeEnum.MARIA_MAKILING.getId())) {
                return joinPoint.proceed();
            } else {
                return BigDecimal.valueOf(10000000L);
            }
        } catch (Throwable throwable) {
            log.error("SingleWalletService#getCredit, param：{}, errMsg: {}",
                    FastJsonUtils.toJSONString(joinPoint.getArgs()),
                    throwable.getMessage(),
                    throwable);
            throw throwable;
        }
    }

    @Around("execution(* com.wegame.capi..service.SingleWalletService.betWithPayout(..))")
    public Object betWithPayoutAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            UserInfo userInfo = UserInfoContext.getUserInfo();
            if (Objects.isNull(userInfo) || !StringUtils.equals(userInfo.getGameId(), GameTypeEnum.MARIA_MAKILING.getId())) {
                return joinPoint.proceed();
            } else {
                return SingleWalletBetResultDo.builder()
                        .betStatus(BetStatusEnum.SUCCESS)
                        .betId(String.valueOf(RandomUtils.nextInt()))
                        .credit(10000000L)
                        .build();
            }
        } catch (Throwable throwable) {
            log.error("SingleWalletService#betWithPayout, param：{}, errMsg: {}",
                    FastJsonUtils.toJSONString(joinPoint.getArgs()),
                    throwable.getMessage(),
                    throwable);
            throw throwable;
        }
    }

    @Around("execution(* com.wegame.capi..service.SingleWalletService.getBetStatus(..))")
    public Object getBetStatusAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            UserInfo userInfo = UserInfoContext.getUserInfo();
            if (Objects.isNull(userInfo) || !StringUtils.equals(userInfo.getGameId(), GameTypeEnum.MARIA_MAKILING.getId())) {
                return joinPoint.proceed();
            } else {
                return BetStatusRes.builder()
                        .betId(String.valueOf(RandomUtils.nextInt()))
                        .build();
            }
        } catch (Throwable throwable) {
            log.error("SingleWalletService#getBetStatus, param：{}, errMsg: {}",
                    FastJsonUtils.toJSONString(joinPoint.getArgs()),
                    throwable.getMessage(),
                    throwable);
            throw throwable;
        }
    }

    @Around("execution(* com.wegame.capi..service.SingleWalletService.verifyHash(..))")
    public Object verifyHashAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            UserInfo userInfo = UserInfoContext.getUserInfo();
            if (Objects.isNull(userInfo) || !StringUtils.equals(userInfo.getGameId(), GameTypeEnum.MARIA_MAKILING.getId())) {
                return joinPoint.proceed();
            } else {
                return true;
            }
        } catch (Throwable throwable) {
            log.error("SingleWalletService#getBetStatusAround, param：{}, errMsg: {}",
                    FastJsonUtils.toJSONString(joinPoint.getArgs()),
                    throwable.getMessage(),
                    throwable);
            throw throwable;
        }
    }
}

