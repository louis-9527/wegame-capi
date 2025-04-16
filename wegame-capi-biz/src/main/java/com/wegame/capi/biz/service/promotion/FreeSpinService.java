package com.wegame.capi.biz.service.promotion;

import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.domain.res.promotion.FreeSpinHistoryOfPlayerRes;
import com.wegame.capi.api.domain.res.promotion.FreeSpinOfPlayerRes;
import com.wegame.capi.api.domain.res.promotion.FreeSpinRes;
import com.wegame.capi.biz.domain.biz.promotion.FreeSpinDo;
import com.wegame.capi.biz.domain.biz.promotion.FreeSpinHistoryOfPlayerDo;
import com.wegame.capi.biz.domain.biz.promotion.FreeSpinOfPlayerDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.wegame.capi.biz.builder.req.FreeSpinHistoryOfPlayerReqBuilder.buildReq;
import static com.wegame.capi.biz.builder.req.FreeSpinOfPlayerReqBuilder.buildReq;
import static com.wegame.capi.biz.converter.promotion.FreeSpinConverter.toDo;
import static com.wegame.capi.biz.converter.promotion.FreeSpinHistoryConverter.toDo;
import static com.wegame.capi.biz.converter.promotion.FreeSpinPlayerConverter.toDo;

/**
 * free spin service
 */
@Component
public class FreeSpinService {

    @Autowired
    SingleWalletApi singleWalletApi;

    public FreeSpinDo freeSpin() {
        return null;
    }
    public FreeSpinOfPlayerDo freeSpinOfPlayer() {
        FreeSpinOfPlayerRes res = singleWalletApi.freeSpinOfPlayer(buildReq());
        return toDo(res);
    }
    public FreeSpinHistoryOfPlayerDo freeSpinHistoryOfPlayer(Integer page, Integer limit) {
        FreeSpinHistoryOfPlayerRes res = singleWalletApi.freeSpinHistoryOfPlayer(buildReq(page, limit));
        return toDo(res);
    }
}
