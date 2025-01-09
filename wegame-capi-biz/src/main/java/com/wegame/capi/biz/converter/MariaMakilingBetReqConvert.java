package com.wegame.capi.biz.converter;

import com.alibaba.fastjson.JSON;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingBetDo;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.domain.request.MariaMakilingBetRequest;

public class MariaMakilingBetReqConvert<T, R> implements BetToDoConvert<MariaMakilingBetRequest, MariaMakilingBetDo> {
    @Override
    public MariaMakilingBetDo toDo(MariaMakilingBetRequest betRequset) {

        return MariaMakilingBetDo.builder()
                .betAmount(betRequset.getBetAmount())
                .payAmount(betRequset.getPayAmount())
                .betRecordId(betRequset.getBetRecordId() == null ? null : Long.valueOf(betRequset.getBetRecordId()))
                .freeSpinsId(betRequset.getFreeSpinsId())
                .build();
    }

    @Override
    public MariaMakilingBetRequest convertReqDetail(BetRequest betRequest) {
        return JSON.parseObject(JSON.toJSONString(betRequest.getBetDetail()), MariaMakilingBetRequest.class);
    }
}
