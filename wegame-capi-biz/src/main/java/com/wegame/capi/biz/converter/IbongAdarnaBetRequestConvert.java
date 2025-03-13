package com.wegame.capi.biz.converter;

import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.domain.biz.bet.IbongAdarnaBetDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingBetDo;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.domain.request.IbongAdarnaBetRequest;
import com.wegame.capi.client.domain.request.MariaMakilingBetRequest;
import org.apache.commons.lang3.StringUtils;

public class IbongAdarnaBetRequestConvert implements BetToDoConvert<IbongAdarnaBetRequest, IbongAdarnaBetDo> {
    @Override
    public IbongAdarnaBetDo toDo(IbongAdarnaBetRequest betRequest) {
        return IbongAdarnaBetDo.builder()
                .betAmount(betRequest.getBetAmount())
                .payAmount(betRequest.getPayAmount())
                .build();
    }

    @Override
    public IbongAdarnaBetRequest toDetail(BetRequest betRequest) {
        return FastJsonUtils.fromJSONString(FastJsonUtils.toJSONString(betRequest.getBetDetail()), IbongAdarnaBetRequest.class);
    }
}
