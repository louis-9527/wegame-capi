package com.wegame.capi.biz.converter;

import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.domain.biz.bet.MalakasBetDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingBetDo;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.domain.request.MalakasBetRequest;
import com.wegame.capi.client.domain.request.MariaMakilingBetRequest;
import org.apache.commons.lang3.StringUtils;

public class MalakasBetRequestConvert <T, R> implements BetToDoConvert<MalakasBetRequest, MalakasBetDo> {
    @Override
    public MalakasBetDo toDo(MalakasBetRequest betRequest) {
        return MalakasBetDo.builder()
                .betAmount(betRequest.getBetAmount())
                .payAmount(betRequest.getPayAmount())
                .betRecordId(StringUtils.isEmpty(betRequest.getBetRecordId()) ? null : Long.valueOf(betRequest.getBetRecordId()))
                .freeSpinsId(Long.valueOf(betRequest.getFreeSpinsId()))
                .build();
    }

    @Override
    public MalakasBetRequest toDetail(BetRequest betRequest) {
        return FastJsonUtils.fromJSONString(FastJsonUtils.toJSONString(betRequest.getBetDetail()), MalakasBetRequest.class);
    }
}
