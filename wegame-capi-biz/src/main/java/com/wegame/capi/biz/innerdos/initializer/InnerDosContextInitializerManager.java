package com.wegame.capi.biz.innerdos.initializer;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 内部干预上下文管理器
 */
@Slf4j
@Component
public class InnerDosContextInitializerManager implements InitializingBean, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private final Map<GameTypeEnum, InnerDosContextInitializer> contextInitializerMap = new HashMap<>();

    public InnerDosContextInitializer getInitializer(GameTypeEnum gameType) {
        return contextInitializerMap.get(gameType);
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, InnerDosContextInitializer> beansOfType = applicationContext.getBeansOfType(InnerDosContextInitializer.class);
        if (CollectionUtil.isNotEmpty(beansOfType)) {
            beansOfType.values().forEach(initializer -> {
                if (Objects.nonNull(initializer.gameType())) {
                    contextInitializerMap.put(initializer.gameType(), initializer);
                }
            });
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
