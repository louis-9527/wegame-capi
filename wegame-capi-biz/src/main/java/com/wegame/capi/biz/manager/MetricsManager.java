package com.wegame.capi.biz.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 指标采集处理
 */
@Slf4j
@Component
public class MetricsManager implements InitializingBean, ApplicationContextAware {
    private ApplicationContext applicationContext;





    @Override
    public void afterPropertiesSet() throws Exception {




    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
