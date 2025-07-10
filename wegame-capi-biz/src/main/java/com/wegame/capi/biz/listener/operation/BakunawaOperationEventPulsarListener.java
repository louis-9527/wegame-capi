package com.wegame.capi.biz.listener.operation;

import com.wegame.capi.client.enums.GameTypeEnum;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.wegame.capi.common.Constants.BAKUNAWA_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Component
@ConditionalOnProperty(name = BAKUNAWA_PROP, havingValue = TRUE_VAL)
public class BakunawaOperationEventPulsarListener extends AbstractOperationEventPulsarListener {
    private static final long serialVersionUID = 3582600617847359068L;

    @Override
    public GameTypeEnum gameType() {
        return GameTypeEnum.BAKUNAWA;
    }
}
