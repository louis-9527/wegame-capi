package com.wegame.capi.biz.converter;

import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.domain.biz.bet.MalakasBetDo;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.domain.request.MalakasBetRequest;
import org.apache.commons.lang3.StringUtils;

public class MalakasBetRequestConvert <T, R> implements BetToDoConvert<MalakasBetRequest, MalakasBetDo> {
    @Override
    public MalakasBetDo toDo(MalakasBetRequest betRequest) {
        return MalakasBetDo.builder()
                .betAmount(betRequest.getBetAmount())
                .payAmount(betRequest.getPayAmount())
                .betRecordId(StringUtils.isEmpty(betRequest.getBetRecordId()) ? null : Long.valueOf(betRequest.getBetRecordId()))
                .freeSpinsId(StringUtils.isEmpty(betRequest.getGameRecordId()) ? null : Long.valueOf(betRequest.getGameRecordId()))
                .build();
    }

    @Override
    public MalakasBetRequest toDetail(BetRequest betRequest) {
        return FastJsonUtils.fromJSONString(FastJsonUtils.toJSONString(betRequest.getBetDetail()), MalakasBetRequest.class);
    }
}
