package com.wegame.capi.biz.service.promotion;

import com.wegame.capi.api.domain.res.promotion.GetTreasureRes;
import com.wegame.capi.api.domain.res.promotion.RedeemTreasureRes;
import com.wegame.capi.api.promotion.TreasureApi;
import com.wegame.capi.biz.domain.biz.promotion.RedeemTreasureDo;
import com.wegame.capi.biz.domain.biz.promotion.TreasureDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.wegame.capi.biz.builder.req.TreasureReqBuilder.buildGetTreasureReq;
import static com.wegame.capi.biz.builder.req.TreasureReqBuilder.buildRedeemTreasureReq;
import static com.wegame.capi.biz.converter.promotion.RedeemTreasureConverter.resToDo;
import static com.wegame.capi.biz.converter.promotion.TreasureConverter.toDoList;

/**
 * 宝箱service
 */
@Component
public class TreasureService {

    @Autowired
    TreasureApi treasureApi;

    public List<TreasureDo> getTreasure() {
        GetTreasureRes res = treasureApi.getTreasure(buildGetTreasureReq());
        return toDoList(res.getTreasure());
    }

    public RedeemTreasureDo redeemTreasure(String treasureId, Integer level) {
        RedeemTreasureRes res = treasureApi.redeemTreasure(buildRedeemTreasureReq(treasureId, level));
        return resToDo(res);
    }
}
