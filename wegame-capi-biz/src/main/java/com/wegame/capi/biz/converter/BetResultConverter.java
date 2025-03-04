package com.wegame.capi.biz.converter;

import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.client.domain.vo.BetResultVo;
import com.wegame.capi.client.domain.vo.ibongadarna.IbongAdarnaBetResultVo;

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
                .betRecordId(String.valueOf(betResultDo.getBetRecordId()))
                .gameId(betResultDo.getGameId())
                .betType(betResultDo.getBetType().getCode())
                .betSubType(betResultDo.getBetSubType().getCode())
                .credit(betResultDo.getCredit())
                .overallRewardAmount(betResultDo.getOverallRewardAmount())
                .totalPayoutAmount(betResultDo.getTotalPayoutAmount())
                .resultDetail(Objects.nonNull(betResultDo.getResultDetail())
                        ? betResultDo.getResultDetail().toResultVo()
                        : null)
                .build();
    }
}