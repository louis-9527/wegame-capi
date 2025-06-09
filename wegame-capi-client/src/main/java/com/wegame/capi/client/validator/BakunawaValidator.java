package com.wegame.capi.client.validator;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;

import java.util.Objects;

import static com.wegame.capi.client.domain.request.BetRequest.BetDetail.MAX_BET_AMOUNT;
import static com.wegame.capi.client.domain.request.BetRequest.BetDetail.MIN_BET_AMOUNT;

/**
 * @author bruce
 */
public class BakunawaValidator {

    public static void validateParam(BetTypeEnum betType, BetSubTypeEnum betSubType, BetRequest.BetDetail betDetail) {
        AssertUtil.assertNotNull(betDetail.getBetAmount(), ErrorCode.ERR_PARAM);
        // 投注类型只能正常投注
        AssertUtil.assertTrue(BetTypeEnum.NORMAL_BET.equals(betType), ErrorCode.ERR_PARAM);
        // 投注子类型只能是正常投注
        AssertUtil.assertTrue(BetSubTypeEnum.NORMAL_BET.equals(betSubType), ErrorCode.ERR_PARAM);
        if (betDetail.isUseFreeSpin()) {
            AssertUtil.assertTrue(Objects.equals(betDetail.getPayAmount(), 0L), ErrorCode.ERR_PARAM);
        } else {
            AssertUtil.assertTrue(betDetail.getBetAmount() >= MIN_BET_AMOUNT, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(betDetail.getBetAmount() <= MAX_BET_AMOUNT, ErrorCode.ERR_PARAM);
            AssertUtil.assertNotNull(betDetail.getPayAmount(), ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(betDetail.getPayAmount() > 0L, ErrorCode.ERR_PARAM);

            if (Objects.equals(BetSubTypeEnum.NORMAL_BET, betSubType)) {
                AssertUtil.assertTrue(Objects.equals(betDetail.getBetAmount(), betDetail.getPayAmount()), ErrorCode.ERR_PARAM);
            } else {
                throw new BusinessException(ErrorCode.ERR_PARAM);
            }
        }
    }
}
