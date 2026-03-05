package com.wegame.capi.biz.service.promotion;

import com.wegame.capi.api.domain.res.promotion.RedeemPlayerCodeRes;
import com.wegame.capi.api.promotion.RedeemPlayerCodeApi;
import com.wegame.capi.biz.builder.req.RedeemPlayerCodeReqBuilder;
import com.wegame.capi.biz.converter.promotion.RedeemPlayerCodeConverter;
import com.wegame.capi.biz.domain.biz.promotion.RedeemPlayerCodeDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 兑换码 service
 */
@Component
public class RedeemPlayerCodeService {

    @Autowired
    RedeemPlayerCodeApi redeemPlayerCodeApi;

    public RedeemPlayerCodeDo redeemPlayerCode(String code) {
        RedeemPlayerCodeRes res = redeemPlayerCodeApi.redeemPlayerCode(RedeemPlayerCodeReqBuilder.buildRedeemPlayerCodeReq(code));
        return RedeemPlayerCodeConverter.resToDo(res);
    }
}
