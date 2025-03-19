package com.wegame.capi.biz.service.promotion;

import com.wegame.capi.api.domain.res.promotion.GetTreasureRes;
import com.wegame.capi.api.domain.res.promotion.RedeemTreasureRes;
import com.wegame.capi.api.promotion.TreasureApi;
import com.wegame.capi.biz.builder.req.TreasureReqBuilder;
import com.wegame.capi.biz.converter.promotion.RedeemTreasureConverter;
import com.wegame.capi.biz.converter.promotion.TreasureConverter;
import com.wegame.capi.client.domain.vo.promotion.TreasureVo;
import com.wegame.capi.client.domain.vo.promotion.RedeemTreasureVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 宝箱service
 */
@Component
public class TreasureService {

    @Autowired
    TreasureApi treasureApi;

    public List<TreasureVo> getTreasure() {
        GetTreasureRes getTreasureRes = treasureApi.getTreasure(TreasureReqBuilder.buildGetTreasureReq());
        return TreasureConverter.toVoList(getTreasureRes.getTreasure());
    }

    public RedeemTreasureVo redeemTreasure(String treasureId, Integer level) {
        RedeemTreasureRes redeemTreasureRes = treasureApi.redeemTreasure(TreasureReqBuilder.buildRedeemTreasureReq(treasureId, level));
        return RedeemTreasureConverter.toVo(redeemTreasureRes);
    }
}
