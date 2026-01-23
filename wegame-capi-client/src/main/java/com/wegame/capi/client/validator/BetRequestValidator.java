package com.wegame.capi.client.validator;

import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;

@FunctionalInterface
public interface BetRequestValidator {
    void validate(BetTypeEnum betType, BetSubTypeEnum betSubType, BetRequest.BetDetail detail);
}