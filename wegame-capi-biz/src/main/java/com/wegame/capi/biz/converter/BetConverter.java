package com.wegame.capi.biz.converter;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.VolatilityEnum;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
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
                .betPromotionType(BetPromotionTypeEnum.NONE)
                .betAmount(Long.valueOf(betRequest.getBetDetail().getBetAmount()))
                .payAmount(Long.valueOf(betRequest.getBetDetail().getPayAmount()))
                .betRecordId(StringUtils.isNotEmpty(betRequest.getBetDetail().getBetRecordId())
                        ? Long.valueOf(betRequest.getBetDetail().getBetRecordId())
                        : null)
                .gameRecordId(StringUtils.isNotEmpty(betRequest.getBetDetail().getGameRecordId())
                        ? Long.valueOf(betRequest.getBetDetail().getGameRecordId())
                        : null)
                .useStarCard(betRequest.getBetDetail().isUseStarCard())
                .rewardConfigId(betRequest.getBetDetail().getRewardConfigId())
                .rewardId(betRequest.getBetDetail().getRewardId())
                .useFreeSpin(betRequest.getBetDetail().isUseFreeSpin())
                .campaignId(betRequest.getBetDetail().getCampaignId())
                .volatility(VolatilityEnum.valueOfCode(betRequest.getBetDetail().getVolatility()))
                .build();
    }
}