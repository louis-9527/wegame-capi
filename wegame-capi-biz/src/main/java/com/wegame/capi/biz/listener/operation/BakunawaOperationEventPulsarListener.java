package com.wegame.capi.biz.listener.operation;

import com.wegame.capi.client.enums.GameTypeEnum;
import org.springframework.stereotype.Service;

@Service
public class BakunawaOperationEventPulsarListener extends AbstractOperationEventPulsarListener {
    private static final long serialVersionUID = 3582600617847359068L;

    @Override
    public GameTypeEnum gameType() {
        return GameTypeEnum.BAKUNAWA;
    }
}
