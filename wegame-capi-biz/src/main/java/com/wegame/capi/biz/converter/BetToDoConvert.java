package com.wegame.capi.biz.converter;

import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.domain.request.BetRequestValidate;

public interface BetToDoConvert<T extends BetRequestValidate, R> {
    R toDo(T betRequset);

    T toDetail(BetRequest betRequest);
}
