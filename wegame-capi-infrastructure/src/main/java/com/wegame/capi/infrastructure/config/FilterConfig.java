package com.wegame.capi.infrastructure.config;

import com.wegame.capi.infrastructure.filter.ContextFilter;
import com.wegame.base.filter.MdcFilter;
import com.wegame.base.filter.SignatureFilter;
import com.wegame.capi.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.Filter;

/**
 * @author apple
 */
@Slf4j
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean mdcFilter() {
        MdcFilter mdcFilter = new MdcFilter();
        log.info("build mdcFilter, mdcFilter: {}", mdcFilter);
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
        bean.setFilter(mdcFilter);
        bean.setOrder(-2);
        bean.addUrlPatterns("/*");
        return bean;
    }

    @Bean
    @Profile({ "qa", "pre", "prod"})
    public FilterRegistrationBean signatureFilter() {
        SignatureFilter signatureFilter = new SignatureFilter();
        signatureFilter.setAppId(Constants.APP_ID);
        signatureFilter.setAppSecret(Constants.APP_SECRET);
        signatureFilter.setRequestTimeOut(Constants.REQUEST_TIME_OUT);
        log.info("build signatureFilter, signatureFilter: {}", signatureFilter);
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
        bean.setFilter(signatureFilter);
        bean.setOrder(1);
        bean.addUrlPatterns("/bet/*");
        bean.addUrlPatterns("/betRecord/*");
        bean.addUrlPatterns("/reward/*");
        bean.addUrlPatterns("/user/*");
        bean.addUrlPatterns("/user/*");
        bean.addUrlPatterns("/inner/*");
        return bean;
    }

    @Bean
    public FilterRegistrationBean contextFilter(@Autowired RedisTemplate<String, Object> redisTemplate) {
        ContextFilter contextFilter = new ContextFilter();
        contextFilter.setRedisTemplate(redisTemplate);
        log.info("build contextFilter, contextFilter: {}", contextFilter);
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
        bean.setFilter(contextFilter);
        bean.setOrder(2);
        bean.addUrlPatterns("/*");
        return bean;
    }
}
