package com.wegame.capi.biz.config.es;

import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EsEnableCondition implements Condition {

    private static final Map<GameTypeEnum, String> esEnableGameMap = new HashMap<>();

    static {
        esEnableGameMap.put(GameTypeEnum.MALAKAS, Constants.MALAKAS_PROP);
        esEnableGameMap.put(GameTypeEnum.IBONG_ADARNA, Constants.IBONG_ADARNA_PROP);
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        for (Map.Entry<GameTypeEnum, String> entry: esEnableGameMap.entrySet()) {
            if(env.getProperty(entry.getValue(), Boolean.class, false)) {
                log.info("game: {} depends on es, enable esRestClient", entry.getKey().getId());
                return true;
            }
        }
        log.info("no game depends on es, disable esRestClient");
        return false;
    }
}
