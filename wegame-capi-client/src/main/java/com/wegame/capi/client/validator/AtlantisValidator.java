package com.wegame.capi.client.validator;

import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;

import java.util.List;
import java.util.Objects;

/**
 * @author bruce
 */
public class AtlantisValidator {
    private static final long MULTIPLE_2 = 2;
    private static final long MULTIPLE_20 = 20;
    private static final long MULTIPLE_50 = 50;
    private static final long MULTIPLE_200 = 200;
    private static final List<Long> SUPPORT_BET_AMOUNTS = Lists.newArrayList(300L, 500L, 1_000L,
            2_000L, 3_000L, 5_000L,
            10_000L, 20_000L, 30_000L,
            40_000L, 50_000L, 80_000L,
            100_000L, 200_000L, 500_000L,
            1000_000L, 5000_000L, 10000_000L);

    private static final List<BetSubTypeEnum> NOT_SUPPORT_BET_SUB_TYPES = Lists.newArrayList(BetSubTypeEnum.FREE_WHEEL_NORMAL_BET, BetSubTypeEnum.FREE_WHEEL_BUY_BET);

    public static void validateParam(BetTypeEnum betType, BetSubTypeEnum betSubType, BetRequest.BetDetail betDetail) {
        AssertUtil.assertNotNull(betDetail.getBetAmount(), ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betDetail.getPayAmount(), ErrorCode.ERR_PARAM);
        AssertUtil.assertFalse(NOT_SUPPORT_BET_SUB_TYPES.contains(betSubType), ErrorCode.ERR_PARAM, "no support bet sub type");
        AssertUtil.assertTrue(SUPPORT_BET_AMOUNTS.contains(betDetail.getBetAmount()), ErrorCode.ERR_PARAM, "illegal bet amount");
        if (Objects.equals(betType, BetTypeEnum.NORMAL_BET)) {
            // 不能使用星卡
            AssertUtil.assertFalse(betDetail.isUseStarCard(), ErrorCode.ERR_PARAM);
            if (Objects.equals(betSubType, BetSubTypeEnum.NORMAL_BET)) {
                AssertUtil.assertNotNull(betDetail.getPayAmount(), ErrorCode.ERR_PARAM);
                if (betDetail.isUseFreeSpin()) {
                    AssertUtil.assertTrue(betDetail.getBetAmount() >= 10, ErrorCode.ERR_PARAM);
                    AssertUtil.assertTrue(betDetail.getPayAmount() == 0L, ErrorCode.ERR_PARAM);
                } else {
                    AssertUtil.assertTrue(Objects.equals(betDetail.getBetAmount(), betDetail.getPayAmount()), ErrorCode.ERR_PARAM);
                }
            } else if (Objects.equals(betSubType, BetSubTypeEnum.EX_BET)) {
                AssertUtil.assertTrue(Objects.equals(betDetail.getPayAmount(), betDetail.getBetAmount() * MULTIPLE_2), ErrorCode.ERR_PARAM);
            } else if (Objects.equals(betSubType, BetSubTypeEnum.EX_BET_ENHANCE)) {
                AssertUtil.assertTrue(Objects.equals(betDetail.getPayAmount(), betDetail.getBetAmount() * MULTIPLE_20), ErrorCode.ERR_PARAM);
            } else if (Objects.equals(betSubType, BetSubTypeEnum.FREE_SPINS_NORMAL_BET)) {
                AssertUtil.assertTrue(Objects.equals(0L, betDetail.getPayAmount()), ErrorCode.ERR_PARAM);
                AssertUtil.assertStrNotEmpty(betDetail.getBetRecordId(), ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(NumberUtil.isLong(betDetail.getBetRecordId()), ErrorCode.ERR_PARAM);
                AssertUtil.assertNotNull(betDetail.getGameRecordId(), ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(NumberUtil.isLong(betDetail.getGameRecordId()), ErrorCode.ERR_PARAM);
            } else {
                throw new BusinessException(ErrorCode.ERR_PARAM);
            }
        } else if (Objects.equals(betType, BetTypeEnum.BUY_BET)) {
            // 不能使用freeSpin
            AssertUtil.assertFalse(betDetail.isUseFreeSpin(), ErrorCode.ERR_PARAM);
            if (Objects.equals(betSubType, BetSubTypeEnum.BUY_BET)) {
                if (betDetail.isUseStarCard()) {
                    AssertUtil.assertTrue(betDetail.getBetAmount() >= 10, ErrorCode.ERR_PARAM);
                    AssertUtil.assertTrue(Objects.equals(betDetail.getPayAmount(), betDetail.getBetAmount()), ErrorCode.ERR_PARAM);
                } else {
                    AssertUtil.assertTrue(Objects.equals(betDetail.getPayAmount(), betDetail.getBetAmount() * MULTIPLE_50), ErrorCode.ERR_PARAM);
                }
            } else if (Objects.equals(betSubType, BetSubTypeEnum.BUY_BET_ENHANCE)) {
                // 不能使用星卡
                AssertUtil.assertFalse(betDetail.isUseStarCard(), ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(Objects.equals(betDetail.getPayAmount(), betDetail.getBetAmount() * MULTIPLE_200), ErrorCode.ERR_PARAM);
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

    public static void validateStarCardBet(BetTypeEnum betType, BetSubTypeEnum betSubType, BetRequest.BetDetail betDetail) {
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
}
