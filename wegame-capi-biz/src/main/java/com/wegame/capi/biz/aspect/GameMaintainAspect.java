package com.wegame.capi.biz.aspect;

import com.google.common.base.Strings;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.backend.GameMaintainConfigDo;
import com.wegame.capi.biz.service.backend.GameMaintainConfigService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Slf4j
@Aspect
@Component
public class GameMaintainAspect {

    private static final String DEFAULT_HEADER_GAME_ID = "gameId";

    @Autowired
    GameMaintainConfigService gameMaintainConfigService;

    @Autowired
    HttpServletRequest request;

    @Around("execution(* com.wegame.capi..*Controller.*(..)) && !execution(* com.wegame.capi..platform.*Controller.*(..))")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        GameMaintainConfigDo configDo = getMaintainConfig();
        if (isGameInMaintain(configDo)) {
            CommonResponse<GameMaintainConfigDo> response = CommonResponse.build(ErrorCode.GAME_UNDER_MAINTAIN.getCode(), ErrorCode.GAME_UNDER_MAINTAIN.getMsg());
            response.setData(configDo);
            return response;
        } else {
            return proceedingJoinPoint.proceed();
        }
    }

    private boolean isGameInMaintain(GameMaintainConfigDo configDo) {
        if (Objects.isNull(configDo)) {
            return false;
        }
        if (Objects.isNull(configDo.getEnableStatus())) {
            return false;
        }
        if (configDo.getEnableStatus().equals(1)) {
            return false;
        }
        if (configDo.getEnableStatus().equals(0)) {
            long now = DateUtil.getCurrentMilli();
            if (now > configDo.getMaintainStartTime().getTime() && now < configDo.getMaintainEndTime().getTime() ) {
                return true;
            }
        }
        return false;
    }

    private GameMaintainConfigDo getMaintainConfig() {
        String gameId = getGameId();
        if (Strings.isNullOrEmpty(gameId)) {
            return null;
        }
        return gameMaintainConfigService.selectByGameId(gameId);
    }

    private String getGameId() {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        if (Objects.nonNull(userInfo)) {
            return userInfo.getGameId();
        }else {
            return request.getHeader(DEFAULT_HEADER_GAME_ID);
        }
    }
}
