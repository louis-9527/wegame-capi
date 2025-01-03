package com.wegame.capi.biz.converter;

import com.wegame.capi.biz.domain.BetResultDo;
import com.wegame.capi.client.domain.vo.BetResultVo;

import java.util.Objects;

/**
 * BetResultConverter
 *
 * @author generate
 */
public class BetResultConverter {

    public static BetResultVo doToVo(BetResultDo betResultDo) {
        if (Objects.isNull(betResultDo)) {
            return null;
        }
        return BetResultVo.builder()
                .gameRecordId(betResultDo.getGameRecordId())
                .gameId(betResultDo.getGameId())
                .betType(betResultDo.getBetType().getCode())
                .betSubType(betResultDo.getBetSubType().getCode())
                .credit(betResultDo.getCredit())
                .resultDetail(betResultDo.getBetResult())
                .build();
    }
}