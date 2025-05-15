package com.wegame.capi.biz.listener.operation;

import com.wegame.capi.client.enums.GameTypeEnum;
import org.springframework.stereotype.Service;

@Service
public class MalakasOperationEventPulsarListener extends AbstractOperationEventPulsarListener {
    @Override
    public GameTypeEnum gameType() {
        return GameTypeEnum.MALAKAS;
    }
}
