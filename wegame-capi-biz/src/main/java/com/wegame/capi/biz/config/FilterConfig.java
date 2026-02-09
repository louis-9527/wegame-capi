package com.wegame.capi.biz.config;

import com.wegame.base.filter.MdcFilter;
import com.wegame.base.filter.SignatureFilter;
import com.wegame.capi.biz.filter.ContextFilter;
import com.wegame.capi.biz.service.LoginService;
import com.wegame.capi.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.Filter;

import static com.wegame.capi.common.Constants.BUILD_BEAN_FORMAT;

/**
 * @author apple
 */
@Slf4j
@Configuration
public class FilterConfig {

    private static final String[] URL_PATTERNS = new String[]{
            "/bet/*",
            "/betRecord/*",
            "/reward/*",
            "/user/logout",
            "/user/me",
            "/user/getCredit",
            "/inner/*",
            "/achievement/*",
            "/bag/*",
            "/mission/*",
            "/level/*",
            "/treasure/*",
            "/freeSpin/*",
            "/contract/*",
            "/redirect/*",
            "/redeem/*",
            "/jackpotRewardPool/*",
            "/jackpotUserRewardRecord/*"
    };

    @Bean
    public FilterRegistrationBean mdcFilter() {
        MdcFilter mdcFilter = new MdcFilter();
        log.info(BUILD_BEAN_FORMAT, "mdcFilter");
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
        bean.setFilter(mdcFilter);
        bean.setOrder(-2);
        bean.addUrlPatterns("/*");
        return bean;
    }

    @Bean
    @Profile({"qa", "pre", "prod"})
    public FilterRegistrationBean signatureFilter() {
        log.info(BUILD_BEAN_FORMAT, "signatureFilter");
        SignatureFilter signatureFilter = new SignatureFilter();
        signatureFilter.setAppId(Constants.APP_ID);
        signatureFilter.setAppSecret(Constants.APP_SECRET);
        signatureFilter.setRequestTimeOut(Constants.REQUEST_TIME_OUT);
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
        bean.setFilter(signatureFilter);
        bean.setOrder(1);
        bean.addUrlPatterns(URL_PATTERNS);
        return bean;
    }

    @Bean
    public FilterRegistrationBean contextFilter(@Autowired RedisTemplate<String, Object> redisTemplate,
                                                @Autowired LoginService loginService) {
        log.info(BUILD_BEAN_FORMAT, "contextFilter");
        ContextFilter contextFilter = new ContextFilter();
        //        contextFilter.setRedisTemplate(redisTemplate);
        contextFilter.setLoginService(loginService);
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
        bean.setFilter(contextFilter);
        bean.setOrder(2);
        bean.addUrlPatterns(URL_PATTERNS);
        return bean;
    }
}
