package com.wegame.capi.biz.builder.req;

import com.wegame.capi.api.domain.req.wegamego.MultiMinMaxRandomReq;

public class MultiMinMaxRandomReqBuilder {

    public static MultiMinMaxRandomReq build(Integer min, Integer max, Integer count) {
        return MultiMinMaxRandomReq.builder()
                .min(min)
                .max(max)
                .count(count)
                .build();
    }
}
