package com.wegame.capi.biz.innerdos.decider;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.innerdos.context.InnerDosContext;
import com.wegame.capi.biz.innerdos.initializer.InnerDosContextInitializerManager;
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

@Slf4j
@Component
public class InnerDosDeciderManager implements InitializingBean, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private final Map<GameTypeEnum, InnerDosDecider> innerDosDeciderMap = new HashMap<>();

    @Autowired
    @Qualifier("defaultInnerDosDecider")
    private DefaultInnerDosDecider defaultInnerDosDecider;
    @Autowired
    private InnerDosContextInitializerManager innerDosContextInitializerManager;

    public InnerDosDecideResult decide(GameTypeEnum gameType, GamePlayDo gamePlayDo, Object... extParams) {
        InnerDosContext context = innerDosContextInitializerManager.getInitializer(gameType).init(gamePlayDo, extParams);
        return this.getOrDefaultInnerDosDecider(gameType).decide(context);
    }

    public InnerDosDecider getOrDefaultInnerDosDecider(GameTypeEnum gameType) {
        return Optional.ofNullable(innerDosDeciderMap.get(gameType)).orElse(this.defaultInnerDosDecider);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, InnerDosDecider> beansOfType = applicationContext.getBeansOfType(InnerDosDecider.class);
        if (CollectionUtil.isNotEmpty(beansOfType)) {
            beansOfType.values().forEach(decider -> {
                if (Objects.nonNull(decider.gameType())) {
                    innerDosDeciderMap.put(decider.gameType(), decider);
                }
            });
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
