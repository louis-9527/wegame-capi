package com.wegame.capi.biz.config;

import com.wegame.capi.biz.interceptor.GameMaintainInterceptor;
import com.wegame.capi.biz.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;
import java.util.List;

/**
 * WebMvcConfig
 * @author apple
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    GameMaintainInterceptor gameMaintainInterceptor;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);
        configurer.setPathMatcher(matcher);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        converters.add(converter);
    }



    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .order(1)
                .excludePathPatterns("/META-INF/resources/",
                        "/resources/",
                        "/static/**",
                        "/public/**",
                        "/webjars/**",
                        "/v2/**",
                        "/doc.html",
                        "/swagger-resources")
                .addPathPatterns("/**");

        registry.addInterceptor(gameMaintainInterceptor)
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/META-INF/resources/",
                        "/resources/",
                        "/static/**",
                        "/public/**",
                        "/webjars/**",
                        "/v2/**",
                        "/doc.html",
                        "/swagger-resources",
                        "/operationEvent/getStatusOfEvents",
                        "/common/ping");
    }
}
