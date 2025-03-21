package com.wegame.capi.biz.builder.req;

import com.wegame.capi.api.domain.req.go.MultiMinMaxRandomReq;

public class MultiMinMaxRandomReqBuilder {

    public static MultiMinMaxRandomReq buildReq(Integer min, Integer max, Integer count) {
        return MultiMinMaxRandomReq.builder()
                .min(min)
                .max(max)
                .count(count)
                .build();
    }
}
