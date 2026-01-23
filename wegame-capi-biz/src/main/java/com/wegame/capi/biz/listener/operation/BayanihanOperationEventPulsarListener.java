package com.wegame.capi.biz.listener.operation;

import com.wegame.capi.client.enums.GameTypeEnum;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.wegame.capi.common.Constants.BAYANIHAN_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Component
@ConditionalOnProperty(name = BAYANIHAN_PROP, havingValue = TRUE_VAL)
public class BayanihanOperationEventPulsarListener extends AbstractOperationEventPulsarListener {
    @Override
    public GameTypeEnum gameType() {
        return GameTypeEnum.BAYANIHAN;
    }
}
