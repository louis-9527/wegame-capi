package com.wegame.capi.client.validator;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;

/**
 * @author bruce
 */
public class GameCommonValidator {

    public static void validateStarCartBet(BetTypeEnum betType, BetSubTypeEnum betSubType, BetRequest.BetDetail betDetail) {
        // 判断是否使用星卡
        if (betDetail.isUseStarCard()) {
            AssertUtil.assertTrue(BetTypeEnum.BUY_BET == betType, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(BetSubTypeEnum.BUY_BET == betSubType || BetSubTypeEnum.FREE_SPINS_BUY_BET == betSubType, ErrorCode.ERR_PARAM);
            if (BetSubTypeEnum.BUY_BET == betSubType) {
                AssertUtil.assertNotNull(betDetail.getRewardConfigId(), ErrorCode.ERR_PARAM);
                AssertUtil.assertNotNull(betDetail.getRewardId(), ErrorCode.ERR_PARAM);
            }
        }
    }

    public static void validateFreeSpinBet(BetTypeEnum betType, BetSubTypeEnum betSubType, BetRequest.BetDetail betDetail) {
        // 判断是否使用预定投注
        if (betDetail.isUseFreeSpin()) {
            AssertUtil.assertTrue(BetTypeEnum.NORMAL_BET == betType, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(BetSubTypeEnum.NORMAL_BET == betSubType || BetSubTypeEnum.FREE_SPINS_NORMAL_BET == betSubType, ErrorCode.ERR_PARAM);
            if (BetSubTypeEnum.NORMAL_BET == betSubType) {
                AssertUtil.assertStrNotEmpty(betDetail.getCampaignId(), ErrorCode.ERR_PARAM);
            }
        }
    }

}
