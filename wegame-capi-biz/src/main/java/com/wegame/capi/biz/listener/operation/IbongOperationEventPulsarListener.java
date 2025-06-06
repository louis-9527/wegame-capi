package com.wegame.capi.biz.listener.operation;

import com.wegame.capi.client.enums.GameTypeEnum;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.wegame.capi.common.Constants.IBONG_ADARNA_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Component
@ConditionalOnProperty(name = IBONG_ADARNA_PROP, havingValue = TRUE_VAL)
public class IbongOperationEventPulsarListener extends AbstractOperationEventPulsarListener {
    @Override
    public GameTypeEnum gameType() {
        return GameTypeEnum.IBONG_ADARNA;
    }
}
