package com.wegame.capi.biz.listener;

import com.wegame.capi.client.enums.GameTypeEnum;
import org.springframework.stereotype.Service;

@Service
public class MariamakilingOperationEventPulsarListener extends AbsOperationEventPulsarListener{
    @Override
    public GameTypeEnum gameType() {
        return GameTypeEnum.MARIA_MAKILING;
    }
}
