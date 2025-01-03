package com.wegame.capi.biz.converter;

import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
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
                .gameRecordId(String.valueOf(betResultDo.getGameRecordId()))
                .gameId(betResultDo.getGameId())
                .betType(betResultDo.getBetType().getCode())
                .betSubType(betResultDo.getBetSubType().getCode())
                .credit(betResultDo.getCredit())
                .resultDetail(betResultDo.getResultDetail())
                .build();
    }
}