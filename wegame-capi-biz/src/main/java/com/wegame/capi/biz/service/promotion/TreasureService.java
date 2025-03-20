package com.wegame.capi.biz.service.promotion;

import com.wegame.capi.api.domain.res.promotion.GetTreasureRes;
import com.wegame.capi.api.domain.res.promotion.RedeemTreasureRes;
import com.wegame.capi.api.promotion.TreasureApi;
import com.wegame.capi.biz.builder.req.TreasureReqBuilder;
import com.wegame.capi.biz.converter.promotion.RedeemTreasureConverter;
import com.wegame.capi.biz.converter.promotion.TreasureConverter;
import com.wegame.capi.biz.domain.biz.promotion.RedeemTreasureDo;
import com.wegame.capi.biz.domain.biz.promotion.TreasureDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.wegame.capi.biz.converter.promotion.RedeemTreasureConverter.toDo;
import static com.wegame.capi.biz.converter.promotion.TreasureConverter.toDoList;

/**
 * 宝箱service
 */
@Component
public class TreasureService {

    @Autowired
    TreasureApi treasureApi;

    public List<TreasureDo> getTreasure() {
        GetTreasureRes getTreasureRes = treasureApi.getTreasure(TreasureReqBuilder.buildGetTreasureReq());
        return toDoList(getTreasureRes.getTreasure());
    }

    public RedeemTreasureDo redeemTreasure(String treasureId, Integer level) {
        RedeemTreasureRes redeemTreasureRes = treasureApi.redeemTreasure(TreasureReqBuilder.buildRedeemTreasureReq(treasureId, level));
        return toDo(redeemTreasureRes);
    }
}
