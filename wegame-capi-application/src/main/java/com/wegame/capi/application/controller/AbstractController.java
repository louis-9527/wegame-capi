package com.wegame.capi.application.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import javax.servlet.http.HttpServletResponse;

import static com.wegame.capi.common.Constants.BUILD_BEAN_FORMAT;

@Slf4j
public class AbstractController implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info(BUILD_BEAN_FORMAT, this.getClass().getSimpleName());
    }

    public void noCacheControl(HttpServletResponse response) {
        response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Expires", "0");
    }


}
