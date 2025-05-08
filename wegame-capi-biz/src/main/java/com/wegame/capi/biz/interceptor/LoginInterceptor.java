package com.wegame.capi.biz.interceptor;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.client.context.UserInfoContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            LoginRequired annotation = handlerMethod.getMethodAnnotation(LoginRequired.class);
            if (Objects.isNull(annotation)) {
                Class<?> type = handlerMethod.getBeanType();
                annotation = type.getDeclaredAnnotation(LoginRequired.class);
            }
            if (Objects.isNull(annotation)) {
                return true;
            }
            if (Objects.isNull(UserInfoContext.getUserInfo())) {
                log.info("preHandle - Not Logged In");
                CommonResponse commonResponse = CommonResponse.build(ErrorCode.ERR_NOT_LOGIN.getCode(), ErrorCode.ERR_NOT_LOGIN.getMsg());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setHeader(ThreadMdcUtil.TRACE_ID, MDC.get(ThreadMdcUtil.TRACE_ID));
                response.getWriter().write(FastJsonUtils.toJSONString(commonResponse));
                return false;
            }
            return true;
        }
        return true;
    }

}
