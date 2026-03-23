package com.wegame.capi.biz.service.promotion;

import com.wegame.capi.api.promotion.RedirectApi;
import com.wegame.capi.biz.builder.req.PromotionForwardReqBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 游戏转发 service
 */
@Component
public class RedirectService {

    @Autowired
    RedirectApi redirectApi;

    public Map<String, Object> forward(String urlPath, Map<String, Object> data) {
        return redirectApi.forward(urlPath, PromotionForwardReqBuilder.build(data));
    }
}
