package com.wegame.capi.biz.service.promotion;

import com.wegame.capi.api.domain.res.promotion.GetRedirectGameUrlRes;
import com.wegame.capi.api.promotion.RedirectApi;
import com.wegame.capi.biz.builder.req.GetRedirectGameUrlReqBuilder;
import com.wegame.capi.biz.converter.promotion.GameRedirectGameUrlConverter;
import com.wegame.capi.biz.domain.biz.promotion.GetRedirectGameUrlDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 游戏转发 service
 */
@Component
public class RedirectService {

    @Autowired
    RedirectApi redirectApi;

    public GetRedirectGameUrlDo getRedirectGameUrl(String gameId) {
        GetRedirectGameUrlRes res = redirectApi.getRedirectGameUrl(GetRedirectGameUrlReqBuilder.buildRedirectReq(gameId));
        return GameRedirectGameUrlConverter.resToDo(res);
    }
}
