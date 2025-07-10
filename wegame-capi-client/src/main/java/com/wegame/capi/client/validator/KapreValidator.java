package com.wegame.capi.client.validator;

import cn.hutool.core.util.NumberUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;

import java.util.Objects;

import static com.wegame.capi.client.domain.request.BetRequest.BetDetail.*;

/**
 * @author bruce
 */
public class KapreValidator {

    public static void validateParam(BetTypeEnum betType, BetSubTypeEnum betSubType, BetRequest.BetDetail betDetail) {
        AssertUtil.assertNotNull(betDetail.getBetAmount(), ErrorCode.ERR_PARAM);
        if (betDetail.isUseStarCard()) {
            AssertUtil.assertTrue(betDetail.getBetAmount() >= 10, ErrorCode.ERR_PARAM);
        } else if (betDetail.isUseFreeSpin()) {
            AssertUtil.assertTrue(betDetail.getBetAmount() >= 10, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(betDetail.getPayAmount() == 0L, ErrorCode.ERR_PARAM);
        } else {
            AssertUtil.assertTrue(betDetail.getBetAmount() >= MIN_BET_AMOUNT, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(betDetail.getBetAmount() <= MAX_BET_AMOUNT, ErrorCode.ERR_PARAM);
        }
        if (Objects.equals(betType, BetTypeEnum.NORMAL_BET)) {
            if (Objects.equals(betSubType, BetSubTypeEnum.NORMAL_BET)) {
                AssertUtil.assertNotNull(betDetail.getPayAmount(), ErrorCode.ERR_PARAM);
                if (betDetail.isUseFreeSpin()) {
                    AssertUtil.assertTrue(Objects.equals(betDetail.getPayAmount(), 0L), ErrorCode.ERR_PARAM);
                } else {
                    AssertUtil.assertTrue(Objects.equals(betDetail.getBetAmount(), betDetail.getPayAmount()), ErrorCode.ERR_PARAM);
                }
            } else if (Objects.equals(betSubType, BetSubTypeEnum.FREE_SPINS_NORMAL_BET)) {
                AssertUtil.assertNotNull(betDetail.getPayAmount(), ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(Objects.equals(0L, betDetail.getPayAmount()), ErrorCode.ERR_PARAM);
                AssertUtil.assertStrNotEmpty(betDetail.getBetRecordId(), ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(NumberUtil.isLong(betDetail.getBetRecordId()), ErrorCode.ERR_PARAM);
                AssertUtil.assertNotNull(betDetail.getGameRecordId(), ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(NumberUtil.isLong(betDetail.getGameRecordId()), ErrorCode.ERR_PARAM);
            } else {
                throw new BusinessException(ErrorCode.ERR_PARAM);
            }
        } else if (Objects.equals(betType, BetTypeEnum.BUY_BET)) {
            if (Objects.equals(betSubType, BetSubTypeEnum.BUY_BET)) {
                AssertUtil.assertNotNull(betDetail.getPayAmount(), ErrorCode.ERR_PARAM);
                if (betDetail.isUseStarCard()) {
                    AssertUtil.assertTrue(Objects.equals(betDetail.getPayAmount(), betDetail.getBetAmount()), ErrorCode.ERR_PARAM);
                } else {
                    AssertUtil.assertTrue(Objects.equals(betDetail.getPayAmount(), betDetail.getBetAmount() * MULTIPLE_35), ErrorCode.ERR_PARAM);
                }
            } else if (Objects.equals(betSubType, BetSubTypeEnum.FREE_SPINS_BUY_BET)) {
                AssertUtil.assertNotNull(betDetail.getPayAmount(), ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(Objects.equals(0L, betDetail.getPayAmount()), ErrorCode.ERR_PARAM);
                AssertUtil.assertStrNotEmpty(betDetail.getBetRecordId(), ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(NumberUtil.isLong(betDetail.getBetRecordId()), ErrorCode.ERR_PARAM);
                AssertUtil.assertNotNull(betDetail.getGameRecordId(), ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(NumberUtil.isLong(betDetail.getGameRecordId()), ErrorCode.ERR_PARAM);
            } else {
                throw new BusinessException(ErrorCode.ERR_PARAM);
            }
        }
    }
}
