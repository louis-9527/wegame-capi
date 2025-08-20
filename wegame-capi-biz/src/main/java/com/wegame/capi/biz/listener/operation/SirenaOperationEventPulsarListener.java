package com.wegame.capi.biz.listener.operation;

import com.wegame.capi.client.enums.GameTypeEnum;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.wegame.capi.common.Constants.*;

@Component
@ConditionalOnProperty(name = SIRENA_PROP, havingValue = TRUE_VAL)
public class SirenaOperationEventPulsarListener extends AbstractOperationEventPulsarListener {
    @Override
    public GameTypeEnum gameType() {
        return GameTypeEnum.SIRENA;
    }
}
