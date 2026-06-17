package com.wegame.capi.biz.config.processor;
import com.wegame.capi.biz.config.domain.GameBizConfig;
import com.wegame.capi.infrastructure.config.properties.GameProperty;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class CustomBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    GameProperty gameProperty;


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof GameBizConfig) {
            GameBizConfig config = ((GameBizConfig) bean);
            if (gameProperty.enabledGameTypes().contains(config.gameType())) {
                config.validate();
            }
        }
        return bean;
    }
}
