package com.wegame.capi.biz.interceptor;

import com.google.common.base.Strings;
import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.biz.converter.backend.GameMaintainConfigConverter;
import com.wegame.capi.biz.domain.backend.GameMaintainConfigDo;
import com.wegame.capi.biz.service.backend.GameMaintainConfigService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.domain.vo.GameMaintainConfigVo;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 登陆校验-拦截器
 *
 * @author apple
 */
@Slf4j
@NoArgsConstructor
@Component
public class GameMaintainInterceptor extends HandlerInterceptorAdapter {

    private static final String DEFAULT_HEADER_GAME_ID = "gameid";

    @Autowired
    GameMaintainConfigService gameMaintainConfigService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        GameMaintainConfigDo configDo = getMaintainConfig(request);
        if (isGameInMaintain(configDo)) {
            log.info("preHandle - game : {} is under maintain", configDo.getGameId());
            CommonResponse<GameMaintainConfigVo> commonResponse = CommonResponse.build(ErrorCode.GAME_UNDER_MAINTAIN.getCode(), ErrorCode.GAME_UNDER_MAINTAIN.getMsg());
            commonResponse.setData(GameMaintainConfigConverter.doToVo(configDo));
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setHeader(ThreadMdcUtil.TRACE_ID, MDC.get(ThreadMdcUtil.TRACE_ID));
            response.getWriter().write(FastJsonUtils.toJSONString(commonResponse));
            return false;
        }
        return true;
    }

    private boolean isGameInMaintain(GameMaintainConfigDo configDo) {
        if (Objects.isNull(configDo)) {
            return false;
        }
        if (Objects.isNull(configDo.getEnableStatus())) {
            return false;
        }
        if (configDo.getEnableStatus().equals(0)) {
            return false;
        }
        if (configDo.getEnableStatus().equals(1)) {
            long now = DateUtil.getCurrentMilli();
            return now > configDo.getMaintainStartTime().getTime() && now < configDo.getMaintainEndTime().getTime();
        }
        return false;
    }

    private GameMaintainConfigDo getMaintainConfig(HttpServletRequest request) {
        String gameId = getGameId(request);
        if (Strings.isNullOrEmpty(gameId)) {
            return null;
        }
        return gameMaintainConfigService.selectByGameId(gameId);
    }

    private String getGameId(HttpServletRequest request) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        if (Objects.nonNull(userInfo)) {
            return userInfo.getGameId();
        }else {
            return request.getHeader(DEFAULT_HEADER_GAME_ID);
        }
    }

}
