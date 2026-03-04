package com.wegame.capi.biz.config.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.google.common.collect.ImmutableMap;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.config.domain.Validate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.wegame.capi.biz.config.GameConfig.MalakasConfig_BeanName;
import static com.wegame.capi.biz.config.GameConfig.MariaMakilingConfig_BeanName;

@Slf4j
@Component
public class NacosConfigRefreshListener implements InitializingBean, ApplicationContextAware {

    private static final Map<String, String> BEAN_NAME_MAP = new ImmutableMap.Builder<String, String>().put("wegame-capi-local-maria_makiling_config.json", MariaMakilingConfig_BeanName).put("wegame-capi-local-malakas_config.json", MalakasConfig_BeanName).build();
    private static final Map<String, Class<? extends Validate>> CLASS_MAP = new ImmutableMap.Builder<String, Class<? extends Validate>>().put("wegame-capi-local-maria_makiling_config.json", MariaMakilingConfig.class).put("wegame-capi-local-malakas_config.json", MalakasConfig.class).build();
    private static final Object lock = new Object();
    @Autowired
    NacosConfigManager nacosConfigManager;
    @Autowired
    NacosConfigProperties nacosConfigProperties;
    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        BEAN_NAME_MAP.keySet().stream().forEach(item -> addListener(item, nacosConfigProperties.getGroup()));
    }

    private void addListener(String dataId, String group) {
        try {
            nacosConfigManager.getConfigService().addListener(dataId, group, new AbstractListener() {
                @Override
                public void receiveConfigInfo(String config) {
                    try {
                        log.info("receiveConfigInfo group: {}, dataId: {}, config: {}", group, dataId, config);
                        Validate obj = FastJsonUtils.fromJSONString(config, CLASS_MAP.get(dataId));
                        obj.validate();
                        replaceBean(BEAN_NAME_MAP.get(dataId), obj);
                    } catch (Exception e) {
                        log.error("receiveConfigInfo error {}/{}", group, dataId, e);
                    }
                }
            });
            log.info("add listener success: {}/{}", group, dataId);
        } catch (Exception e) {
            log.error("add listener error {}/{}", group, dataId, e);
        }
    }

    /**
     * 替换Spring容器中的Bean（核心方法）
     *
     * @param beanName Bean名称（需与旧Bean一致）
     * @param newBean  新的Bean实例
     */
    public void replaceBean(String beanName, Object newBean) {
        synchronized (lock) {
            // 1. 将ApplicationContext转为可配置上下文（获取Bean注册器）
            ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) applicationContext;
            ConfigurableListableBeanFactory beanFactory = configurableContext.getBeanFactory();
//            // 2. 注销旧Bean（如果存在）
//            if (beanFactory.containsBeanDefinition(beanName)) {
//                ((BeanDefinitionRegistry) beanFactory).removeBeanDefinition(beanName);
//                log.info("remove old Bean：{}", beanName);
//            }
            // 3. 注册新Bean（单例模式）
            beanFactory.registerSingleton(beanName, newBean);
            // 4. 验证：获取新Bean，确认替换成功
            Object replacedBean = applicationContext.getBean(beanName);
            log.info("replaceBean success：{} = {}", beanName, replacedBean);
        }
    }


}
