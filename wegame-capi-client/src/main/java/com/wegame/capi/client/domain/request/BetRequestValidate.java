package com.wegame.capi.client.domain.request;

import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;

public interface BetRequestValidate {

    default void validateParam(GameTypeEnum gameType, BetTypeEnum betType, BetSubTypeEnum betSubType,Boolean startCardUse) {
        // do nothing
    }
}
