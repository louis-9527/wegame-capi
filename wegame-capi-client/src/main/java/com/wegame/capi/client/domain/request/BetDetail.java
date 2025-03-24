package com.wegame.capi.client.domain.request;

import cn.hutool.core.util.NumberUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "投注详情")
public class BetDetail implements BetRequestValidate, Serializable {
    private static final long MIN_BET_AMOUNT = 500L;
    private static final long MAX_BET_AMOUNT = 1000000L;
    private static final long MULTIPLE = 35;

    @ApiModelProperty(value = "投注金额", required = true)
    private Long betAmount;
    @ApiModelProperty(value = "支付金额/购买金额", required = true)
    private Long payAmount;
    @ApiModelProperty(value = "投注记录ID, 获得免费轮的投注记录的投注id，非必填", required = false)
    private String betRecordId;
    @ApiModelProperty(value = "免费轮游戏记录ID, 非必填", required = false)
    private String gameRecordId;
    // 星卡投注
    @ApiModelProperty(value = "使用星卡, 非必填", required = false)
    private boolean userStarCart;
    @ApiModelProperty(value = "奖励配置id", required = true)
    private String rewardConfigId;
    @ApiModelProperty(value = "奖励id", required = true)
    private String rewardId;

    @Override
    public void validateParam(GameTypeEnum gameType, BetTypeEnum betType, BetSubTypeEnum betSubType) {
        if (Objects.equals(gameType, GameTypeEnum.MARIA_MAKILING)) {
            mariaMakilingValidateParam(gameType, betType, betSubType);
        } else if (Objects.equals(gameType, GameTypeEnum.MALAKAS)) {
            malakasValidateParam(gameType, betType, betSubType);
        } else if (Objects.equals(gameType, GameTypeEnum.IBONG_ADARNA)) {
            ibongAdarnaValidateParam(gameType, betType, betSubType);
        } else {
            throw new BusinessException(ErrorCode.ERR_PARAM, "NOT SUPPORT");
        }
    }

    private void mariaMakilingValidateParam(GameTypeEnum gameType, BetTypeEnum betType, BetSubTypeEnum betSubType) {
        AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(betAmount >= MIN_BET_AMOUNT, ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(betAmount <= MAX_BET_AMOUNT, ErrorCode.ERR_PARAM);
        if (Objects.equals(betType, BetTypeEnum.NORMAL_BET)) {
            if (Objects.equals(betSubType, BetSubTypeEnum.NORMAL_BET)) {
                AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(Objects.equals(betAmount, payAmount), ErrorCode.ERR_PARAM);
            } else if (Objects.equals(betSubType, BetSubTypeEnum.FREE_SPINS_NORMAL_BET)) {
                AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(Objects.equals(0L, payAmount), ErrorCode.ERR_PARAM);
                AssertUtil.assertStrNotEmpty(betRecordId, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(NumberUtil.isLong(betRecordId), ErrorCode.ERR_PARAM);
                AssertUtil.assertNotNull(gameRecordId, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(NumberUtil.isLong(gameRecordId), ErrorCode.ERR_PARAM);
            } else {
                throw new BusinessException(ErrorCode.ERR_PARAM);
            }
        } else if (Objects.equals(betType, BetTypeEnum.BUY_BET)) {
            if (Objects.equals(betSubType, BetSubTypeEnum.BUY_BET)) {
                AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
                if (userStarCart) {
                    AssertUtil.assertTrue(Objects.equals(payAmount, 0L), ErrorCode.ERR_PARAM);
                } else {
                    AssertUtil.assertTrue(Objects.equals(payAmount, betAmount * MULTIPLE), ErrorCode.ERR_PARAM);
                }
            } else if (Objects.equals(betSubType, BetSubTypeEnum.FREE_SPINS_BUY_BET)) {
                AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(Objects.equals(0L, payAmount), ErrorCode.ERR_PARAM);
                AssertUtil.assertStrNotEmpty(betRecordId, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(NumberUtil.isLong(betRecordId), ErrorCode.ERR_PARAM);
                AssertUtil.assertNotNull(gameRecordId, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(NumberUtil.isLong(gameRecordId), ErrorCode.ERR_PARAM);
            } else {
                throw new BusinessException(ErrorCode.ERR_PARAM);
            }
        }
        validateStartCartBet(gameType, betType, betSubType);
    }

    private void validateStartCartBet(GameTypeEnum gameType, BetTypeEnum betType, BetSubTypeEnum betSubType) {
        // 判断是否使用星卡
        if (userStarCart) {
            AssertUtil.assertNotNull(rewardConfigId, ErrorCode.ERR_PARAM);
            AssertUtil.assertNotNull(rewardId, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(BetTypeEnum.BUY_BET == betType, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(BetSubTypeEnum.BUY_BET == betSubType, ErrorCode.ERR_PARAM);
        }
    }

    public void malakasValidateParam(GameTypeEnum gameType, BetTypeEnum betType, BetSubTypeEnum betSubType) {
        AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(betAmount > 0L, ErrorCode.ERR_PARAM);
        if (Objects.equals(betType, BetTypeEnum.NORMAL_BET)) {
            if (Objects.equals(betSubType, BetSubTypeEnum.NORMAL_BET)) {
                AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(Objects.equals(betAmount, payAmount), ErrorCode.ERR_PARAM);
            } else if (Objects.equals(betSubType, BetSubTypeEnum.FREE_SPINS_NORMAL_BET)) {
                AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(Objects.equals(0L, payAmount), ErrorCode.ERR_PARAM);
                AssertUtil.assertStrNotEmpty(betRecordId, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(NumberUtil.isLong(betRecordId), ErrorCode.ERR_PARAM);
                AssertUtil.assertNotNull(gameRecordId, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(NumberUtil.isLong(gameRecordId), ErrorCode.ERR_PARAM);
            } else {
                throw new BusinessException(ErrorCode.ERR_PARAM);
            }
        } else if (Objects.equals(betType, BetTypeEnum.BUY_BET)) {
            if (Objects.equals(betSubType, BetSubTypeEnum.BUY_BET)) {
                AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(Objects.equals(payAmount, betAmount * 35), ErrorCode.ERR_PARAM);
            } else if (Objects.equals(betSubType, BetSubTypeEnum.FREE_SPINS_BUY_BET)) {
                AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(Objects.equals(0L, payAmount), ErrorCode.ERR_PARAM);
                AssertUtil.assertStrNotEmpty(betRecordId, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(NumberUtil.isLong(betRecordId), ErrorCode.ERR_PARAM);
                AssertUtil.assertNotNull(gameRecordId, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(NumberUtil.isLong(gameRecordId), ErrorCode.ERR_PARAM);
            } else {
                throw new BusinessException(ErrorCode.ERR_PARAM);
            }
        }
    }

    public void ibongAdarnaValidateParam(GameTypeEnum gameType, BetTypeEnum betType, BetSubTypeEnum betSubType) {
        AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(betAmount > 0L, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(payAmount > 0L, ErrorCode.ERR_PARAM);
        // 投注类型只能正常投注
        AssertUtil.assertTrue(BetTypeEnum.NORMAL_BET.equals(betType), ErrorCode.ERR_PARAM, "not support betType");
        // 投注子类型只能是正常投注/ex投注
        AssertUtil.assertTrue(BetSubTypeEnum.NORMAL_BET.equals(betSubType) || BetSubTypeEnum.EX_BET.equals(betSubType), ErrorCode.ERR_PARAM, "not support betSubType");
        // ex类型，支付金额必须是投注金额的1.5倍
        if (Objects.equals(BetSubTypeEnum.EX_BET, betSubType)) {
            BigDecimal payAmountDecimal = BigDecimal.valueOf(payAmount);
            BigDecimal betAmountDecimal = BigDecimal.valueOf(betAmount);
            BigDecimal exceptPayAmount = betAmountDecimal.multiply(new BigDecimal("1.5")).setScale(0, RoundingMode.FLOOR);
            AssertUtil.assertTrue(exceptPayAmount.compareTo(payAmountDecimal) == 0, ErrorCode.ERR_PARAM, "ex bet payAmount must be 1.5 times of betAmount");
        }
    }
}
