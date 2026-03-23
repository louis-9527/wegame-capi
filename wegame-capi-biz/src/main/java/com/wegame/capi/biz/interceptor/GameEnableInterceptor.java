package com.wegame.capi.biz.interceptor;

import com.wegame.base.domain.CommonResponse;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.infrastructure.config.properties.GameProperty;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 游戏-拦截器
 *
 * @author apple
 */
@Slf4j
@Component
@NoArgsConstructor
public class GameEnableInterceptor extends HandlerInterceptorAdapter {

    private static final String DEFAULT_HEADER_GAME_ID = "gameid";

    @Autowired
    GameProperty gameProperty;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        if (!isGameEnabled(request)) {
//            log.info("preHandle - game : {} is not enabled", getGameId(request));
//            CommonResponse<Object> commonResponse = CommonResponse.build(ErrorCode.ERR_PARAM.getCode(), ErrorCode.ERR_PARAM.getMsg());
//            response.setContentType("application/json");
//            response.setCharacterEncoding("UTF-8");
//            response.setHeader(ThreadMdcUtil.TRACE_ID, MDC.get(ThreadMdcUtil.TRACE_ID));
//            response.getWriter().write(FastJsonUtils.toJSONString(commonResponse));
//            return false;
//        }
        return true;
    }

    private boolean isGameEnabled(HttpServletRequest request) {
        String gameId = getGameId(request);
        if (StringUtils.isEmpty(gameId)) {
            return false;
        }
        for (GameTypeEnum gameType : CollectionUtils.emptyIfNull(gameProperty.enabledGameTypes())) {
            if (gameType.getId().equals(gameId)) {
                return true;
            }
        }
        return false;
    }

    private String getGameId(HttpServletRequest request) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        if (Objects.nonNull(userInfo)) {
            return userInfo.getGameId();
        } else {
            return request.getHeader(DEFAULT_HEADER_GAME_ID);
        }
    }

}
