package com.wegame.capi.biz.service.freespin;

import com.wegame.capi.api.domain.req.freespin.FreeSpinReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.promotion.FreeSpinDetailRes;
import com.wegame.capi.api.domain.res.promotion.FreeSpinHistoryOfPlayerRes;
import com.wegame.capi.api.domain.res.promotion.FreeSpinOfPlayerRes;
import com.wegame.capi.api.domain.res.singlewallet.SingleWalletBetWithPayoutRes;
import com.wegame.capi.api.SingleWalletFreeSpinApi;
import com.wegame.capi.biz.domain.biz.promotion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.wegame.capi.biz.builder.req.FreeSpinHistoryOfPlayerReqBuilder.buildReq;
import static com.wegame.capi.biz.builder.req.FreeSpinOfPlayerReqBuilder.buildReq;
import static com.wegame.capi.biz.builder.req.FreeSpinDetailReqBuilder.buildReq;
import static com.wegame.capi.biz.converter.promotion.FreeSpinHistoryConverter.toDo;
import static com.wegame.capi.biz.converter.promotion.FreeSpinPlayerConverter.resToDo;
import static com.wegame.capi.biz.converter.promotion.FreeSpinDetailConverter.toDo;

/**
 * free spin service
 */
@Component
public class FreeSpinService {

    @Autowired
    SingleWalletFreeSpinApi singleWalletFreeSpinApi;

    public CommonRes<SingleWalletBetWithPayoutRes> freeSpin(FreeSpinReq req) {
        return singleWalletFreeSpinApi.freeSpin(req);
    }

    public FreeSpinOfPlayerDo freeSpinOfPlayer() {
        FreeSpinOfPlayerRes res = singleWalletFreeSpinApi.freeSpinOfPlayer(buildReq());
        return resToDo(res);
    }

    public FreeSpinHistoryOfPlayerDo freeSpinHistoryOfPlayer(Integer page, Integer limit) {
        FreeSpinHistoryOfPlayerRes res = singleWalletFreeSpinApi.freeSpinHistoryOfPlayer(buildReq(page, limit));
        return toDo(res);
    }

    public FreeSpinDetailDo freeSpinDetail(String campaignId) {
        FreeSpinDetailRes res = singleWalletFreeSpinApi.freeSpinDetail(buildReq(campaignId));
        return toDo(res);
    }
}
