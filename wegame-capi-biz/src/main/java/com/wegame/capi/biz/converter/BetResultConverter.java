package com.wegame.capi.biz.converter;

import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.client.domain.vo.BetResultVo;
import com.wegame.capi.client.domain.vo.MariaMakilingBetResultVo;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                .resultDetail(Objects.nonNull(betResultDo.getResultDetail())
                        ? (MariaMakilingBetResultVo)betResultDo.getResultDetail().toVo() // todo fix
                        : null)
                .build();
    }
}