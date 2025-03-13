package com.wegame.capi.infrastructure.filter;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.context.SystemInfo;
import com.wegame.base.context.SystemInfoContext;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.common.Constants;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 上下文过滤器
 *
 * @author apple
 */
@Slf4j
@Setter
@NoArgsConstructor
public class ContextFilter extends OncePerRequestFilter {

    RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // build context
        buildSystemInfoContextInfo(request);
        buildUserInfoContextInfo(request);
        buildOperatorContextInfo(request);

        // do filter
        filterChain.doFilter(request, response);

        response.setHeader("Connection", "keep-alive");
        response.setHeader("Keep-Alive", "timeout=180");

        // clean context
        SystemInfoContext.clear();
        UserInfoContext.clear();
        OperatorContext.clear();
    }

    private void buildSystemInfoContextInfo(HttpServletRequest request) {
        try {
            String forwardedFor = request.getHeader("X-Forwarded-For");
            String clientIp = request.getHeader("X-Real-IP");
            String forwardedIps = StringUtils.isNotBlank(forwardedFor) ? forwardedFor : request.getHeader("X-Real-IP");
            if (StringUtils.isNotBlank(forwardedIps) && forwardedIps.split(",").length > 0) {
                clientIp = forwardedIps.split(",")[0];
            }
            SystemInfo systemInfo = SystemInfo.builder()
                    .reqPath(request.getRequestURI())
                    .reqFullPath(request.getRequestURL().toString())
                    .referer(request.getHeader("Referer"))
                    .userAgent(request.getHeader("User-Agent"))
                    .locale(request.getLocale())
                    .appVersion(request.getHeader(SystemInfo.APP_VERSION))
                    .appType(request.getHeader(SystemInfo.APP_TYPE))
                    .deviceId(request.getHeader(SystemInfo.DEVICE_ID))
                    .deviceName(request.getHeader(SystemInfo.DEVICE_NAME))
                    .systemName(request.getHeader(SystemInfo.SYSTEM_NAME))
                    .systemVersion(request.getHeader(SystemInfo.SYSTEM_VERSION))
                    .platformId(request.getHeader(SystemInfo.PLATFORM_ID))
                    .clientIp(clientIp)
                    .build();
            SystemInfoContext.setSysContext(systemInfo);
        } catch (Exception e) {
            log.error("buildSystemInfoContextInfo err: {}", e.getMessage(), e);
        }
    }

    private void buildUserInfoContextInfo(HttpServletRequest request) {
        try {
            String _token_ = request.getHeader(Constants._TOKEN_);
            if ("null".equals(_token_)) {
                _token_ = null;
            }
            UserInfo user = null;
            if (StringUtils.isNotEmpty(_token_)) {
                user = (UserInfo) redisTemplate.opsForValue().get(String.format(Constants.CUSER_LOGIN_TOKEN, _token_));
            }
            log.info("login user info {}", FastJsonUtils.toJSONString(user));
            UserInfoContext.setUserInfo(user);
        } catch (Exception e) {
            log.error("buildUserInfoContextInfo error: {}", e.getMessage(), e);
        }
    }

    private void buildOperatorContextInfo(HttpServletRequest request) {
        try {
            UserInfo userInfo = UserInfoContext.getUserInfo();
            if (Objects.nonNull(userInfo)) {
                OperatorContext.setOperator(Operator.builder()
                        .userId(Long.valueOf(userInfo.getId()))
                        .playerId(userInfo.getPlayerId())
                        .name(userInfo.getPlayerName())
                        .build());
            }
        } catch (Exception e) {
            log.error("buildOperatorContextInfo error: {}", e.getMessage(), e);
        }
    }
}
