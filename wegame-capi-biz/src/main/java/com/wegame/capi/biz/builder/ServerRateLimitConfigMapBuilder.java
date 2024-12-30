package com.wegame.capi.biz.builder;

import com.wegame.capi.common.domain.ServerRateLimitConfigDo;
import com.wegame.capi.common.domain.ServerRateLimitConfigMapDo;

import java.util.Objects;

public class ServerRateLimitConfigMapBuilder {
    public static ServerRateLimitConfigMapDo build(ServerRateLimitConfigDo rateLimitConfigDo) {
        if (Objects.isNull(rateLimitConfigDo)) {
            return new ServerRateLimitConfigMapDo();
        }

        return new ServerRateLimitConfigMapDo();
    }
}
