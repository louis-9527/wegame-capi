package com.wegame.capi.biz.innerdos.decider.chain;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 内部关于决策器链管理器
 */
@Slf4j
@Component
public class InnerDosDeciderChainManager implements InitializingBean, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private final Map<GameTypeEnum, InnerDosDeciderChain> innerDosDeciderChainMap = new HashMap<>();
    @Autowired
    @Qualifier("defaultInnerDosDeciderChain")
    private DefaultInnerDosDeciderChain defaultInnerDosDeciderChain;

    public InnerDosDeciderChain getOrDefaultInnerDosDeciderChain(GameTypeEnum gameType) {
        return Optional.ofNullable(innerDosDeciderChainMap.get(gameType)).orElse(this.defaultInnerDosDeciderChain);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, InnerDosDeciderChain> beansOfType = applicationContext.getBeansOfType(InnerDosDeciderChain.class);
        if (CollectionUtil.isNotEmpty(beansOfType)) {
            beansOfType.values().forEach(deciderChain -> {
                if (Objects.nonNull(deciderChain.gameType())) {
                    innerDosDeciderChainMap.put(deciderChain.gameType(), deciderChain);
                }
            });
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
