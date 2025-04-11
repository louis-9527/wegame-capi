package com.wegame.capi.biz.service.promotion;

import com.wegame.capi.api.domain.res.promotion.FreeSpinHistoryOfPlayerRes;
import com.wegame.capi.api.domain.res.promotion.FreeSpinOfPlayerRes;
import com.wegame.capi.api.promotion.FreeSpinApi;
import com.wegame.capi.biz.domain.biz.promotion.FreeSpinHistoryOfPlayerDo;
import com.wegame.capi.biz.domain.biz.promotion.FreeSpinOfPlayerDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.wegame.capi.biz.builder.req.FreeSpinHistoryOfPlayerReqBuilder.buildReq;
import static com.wegame.capi.biz.builder.req.FreeSpinOfPlayerReqBuilder.buildReq;
import static com.wegame.capi.biz.converter.promotion.FreeSpinConverter.toDo;
import static com.wegame.capi.biz.converter.promotion.HistoryFreeSpinConverter.toDo;

/**
 * free spin service
 */
@Component
public class FreeSpinService {

    @Autowired
    FreeSpinApi freeSpinApi;

    public FreeSpinOfPlayerDo freeSpinOfPlayer() {
        FreeSpinOfPlayerRes res = freeSpinApi.freeSpinOfPlayer(buildReq());
        return toDo(res);
    }

    public FreeSpinHistoryOfPlayerDo freeSpinHistoryOfPlayer(Integer page, Integer limit, Integer startTime, Integer endTime) {
        FreeSpinHistoryOfPlayerRes res = freeSpinApi.freeSpinHistoryOfPlayer(buildReq(page, limit, startTime, endTime));
        return toDo(res);
    }
}
