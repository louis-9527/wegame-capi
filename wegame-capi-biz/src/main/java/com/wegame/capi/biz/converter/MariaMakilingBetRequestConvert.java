package com.wegame.capi.biz.converter;

import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingBetDo;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.domain.request.MariaMakilingBetRequest;
import org.apache.commons.lang3.StringUtils;

public class MariaMakilingBetRequestConvert<T, R> implements BetToDoConvert<MariaMakilingBetRequest, MariaMakilingBetDo> {
    @Override
    public MariaMakilingBetDo toDo(MariaMakilingBetRequest betRequest) {
        return MariaMakilingBetDo.builder()
                .betAmount(betRequest.getBetAmount())
                .payAmount(betRequest.getPayAmount())
                .betRecordId(StringUtils.isNotEmpty(betRequest.getBetRecordId()) ? Long.valueOf(betRequest.getBetRecordId()) : null)
                .gameRecordId(StringUtils.isNotEmpty(betRequest.getGameRecordId()) ? Long.valueOf(betRequest.getGameRecordId()) : null)
                .build();
    }

    @Override
    public MariaMakilingBetRequest toDetail(BetRequest betRequest) {
        return FastJsonUtils.fromJSONString(FastJsonUtils.toJSONString(betRequest.getBetDetail()), MariaMakilingBetRequest.class);
    }
}
