package com.wegame.capi.biz.listener.operation;

import com.wegame.capi.biz.config.pulsar.OperationEventPulsarConfig;
import com.wegame.capi.client.enums.GameTypeEnum;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.wegame.capi.common.Constants.MARIA_MAKILING_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Component
@ConditionalOnProperty(name = MARIA_MAKILING_PROP, havingValue = TRUE_VAL)
@ConditionalOnBean(OperationEventPulsarConfig.class)
public class MariamakilingOperationEventPulsarListener extends AbstractOperationEventPulsarListener {

    @Override
    public GameTypeEnum gameType() {
        return GameTypeEnum.MARIA_MAKILING;
    }

}
