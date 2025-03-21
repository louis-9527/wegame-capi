package com.wegame.capi.biz.converter;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * BetConverter
 *
 * @author generate
 */
public class BetConverter {

    public static BetDo requestToDo(BetRequest betRequest) {
        GameTypeEnum gameType = GameTypeEnum.valueOfId(betRequest.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        return BetDo.builder()
                .gameId(betRequest.getGameId())
                .gameType(gameType)
                .betType(BetTypeEnum.valueOfCode(betRequest.getBetType()))
                .betSubType(BetSubTypeEnum.valueOfCode(betRequest.getBetSubType()))
                .betAmount(betRequest.getBetDetail().getBetAmount())
                .payAmount(betRequest.getBetDetail().getPayAmount())
                .betRecordId(StringUtils.isNotEmpty(betRequest.getBetDetail().getBetRecordId())
                        ? Long.valueOf(betRequest.getBetDetail().getBetRecordId())
                        : null)
                .gameRecordId(StringUtils.isNotEmpty(betRequest.getBetDetail().getGameRecordId())
                        ? Long.valueOf(betRequest.getBetDetail().getGameRecordId())
                        : null)
                .useStarCart(betRequest.getBetDetail().isUserStarCart())
                .rewardConfigId(betRequest.getBetDetail().getRewardConfigId())
                .rewardId(betRequest.getBetDetail().getRewardId())
                .build();
    }
}