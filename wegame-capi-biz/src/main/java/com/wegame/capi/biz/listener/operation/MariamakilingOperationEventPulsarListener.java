package com.wegame.capi.biz.listener.operation;

import com.wegame.capi.client.enums.GameTypeEnum;
import org.springframework.stereotype.Component;

@Component
public class MariamakilingOperationEventPulsarListener extends AbstractOperationEventPulsarListener {

    @Override
    public GameTypeEnum gameType() {
        return GameTypeEnum.MARIA_MAKILING;
    }

}
