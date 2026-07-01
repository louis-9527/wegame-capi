package com.wegame.capi.biz.service.promotion;

import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.promotion.ForwardApi;
import com.wegame.capi.biz.builder.req.PromotionForwardReqBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 游戏转发 service
 */
@Component
public class ForwardService {

    @Autowired
    ForwardApi forwardApi;

    public CommonRes<Map<String, Object>> forward(String urlPath, Map<String, Object> data) {
        return forwardApi.forward(urlPath, PromotionForwardReqBuilder.build(data));
    }
}
