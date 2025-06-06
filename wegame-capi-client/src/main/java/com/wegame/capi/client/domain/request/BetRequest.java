package com.wegame.capi.client.domain.request;

import cn.hutool.core.util.NumberUtil;
import com.wegame.base.Validate;
import com.wegame.base.domain.BaseRequest;
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
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "投注请求参数")
public class BetRequest extends BaseRequest implements Validate, Serializable {
    private static final long serialVersionUID = -7256326783223327817L;
    @ApiModelProperty(value = "游戏Id", required = true)
    private String gameId;
    @ApiModelProperty(value = "投注类型:NORMAL_BET,购买投注:BUY_BET", required = true)
    private String betType;
    @ApiModelProperty(value = "投注子类型:" + "NORMAL_BET:正常投注, " + "FREE_SPINS_NORMAL_BET: 正常免费轮投注，" + "BUY_BET:购买投注, " + "FREE_SPINS_BUY_BET: 免费轮购买投注", required = true)
    private String betSubType;
    @ApiModelProperty(value = "投注明细,根据投注类型不同,明细不同", required = true)
    private BetDetail betDetail;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(gameId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(GameTypeEnum.valueOfId(gameId), ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(betType, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(BetTypeEnum.valueOfCode(betType), ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(betSubType, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(BetSubTypeEnum.valueOfCode(betSubType), ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betDetail, ErrorCode.ERR_PARAM);
        betDetail.validateParam(GameTypeEnum.valueOfId(gameId),
                BetTypeEnum.valueOfCode(betType),
                BetSubTypeEnum.valueOfCode(betSubType));
    }


    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ApiModel(description = "投注详情")
    public static class BetDetail implements BetRequestValidate, Serializable {
        private static final long serialVersionUID = 196176475253019928L;
        private static final long MIN_BET_AMOUNT = 300L;
        private static final long MAX_BET_AMOUNT = 10000000L;
        private static final long MULTIPLE = 35;

        @ApiModelProperty(value = "投注金额", required = true)
        private Long betAmount;
        @ApiModelProperty(value = "支付金额/购买金额", required = true)
        private Long payAmount;
        @ApiModelProperty(value = "投注记录ID, 获得免费轮的投注记录的投注id，非必填")
        private String betRecordId;
        @ApiModelProperty(value = "免费轮游戏记录ID, 非必填")
        private String gameRecordId;
        // 星卡投注
        @ApiModelProperty(value = "使用星卡, 非必填")
        private boolean useStarCard;
        @ApiModelProperty(value = "奖励配置id, 使用星卡时必填")
        private String rewardConfigId;
        @ApiModelProperty(value = "奖励id, 使用星卡时必填")
        private String rewardId;
        // 预定投注
        @ApiModelProperty(value = "使用预定投注, 非必填")
        private boolean useFreeSpin;
        @ApiModelProperty(value = "预定投注Id, 使用预定投注时必填")
        private String campaignId;

        @Override
        public void validateParam(GameTypeEnum gameType, BetTypeEnum betType, BetSubTypeEnum betSubType) {
            AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(betAmount >= 0, ErrorCode.ERR_PARAM);
            AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(payAmount >= 0, ErrorCode.ERR_PARAM);
            if (Objects.equals(gameType, GameTypeEnum.MARIA_MAKILING)) {
                mariaMakilingValidateParam(betType, betSubType);
                validateStartCartBet(betType, betSubType);
                validateFreeSpinBet(betType, betSubType);
            } else if (Objects.equals(gameType, GameTypeEnum.MALAKAS)) {
                malakasValidateParam(betType, betSubType);
                validateStartCartBet(betType, betSubType);
                validateFreeSpinBet(betType, betSubType);
            } else if (Objects.equals(gameType, GameTypeEnum.IBONG_ADARNA)) {
                ibongAdarnaValidateParam(betType, betSubType);
                validateFreeSpinBet(betType, betSubType);
                ibongAdarnaValidateParam(betType, betSubType);
                validateFreeSpinBet(betType, betSubType);
            } else if (Objects.equals(gameType, GameTypeEnum.BAKUNAWA)) {
                bakunawaValidateParam(betType, betSubType);
                validateFreeSpinBet(betType, betSubType);
            } else {
                throw new BusinessException(ErrorCode.ERR_PARAM, "NOT SUPPORT");
            }
        }

        private void mariaMakilingValidateParam(BetTypeEnum betType, BetSubTypeEnum betSubType) {
            AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
            if(useStarCard){
                AssertUtil.assertTrue(betAmount >= 10, ErrorCode.ERR_PARAM);
            }else if(useFreeSpin){
                AssertUtil.assertTrue(betAmount >= 10, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(payAmount == 0L, ErrorCode.ERR_PARAM);
            } else {
                AssertUtil.assertTrue(betAmount >= MIN_BET_AMOUNT, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(betAmount <= MAX_BET_AMOUNT, ErrorCode.ERR_PARAM);
            }
            if (Objects.equals(betType, BetTypeEnum.NORMAL_BET)) {
                if (Objects.equals(betSubType, BetSubTypeEnum.NORMAL_BET)) {
                    AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
                    if (useFreeSpin) {
                        AssertUtil.assertTrue(Objects.equals(payAmount, 0L), ErrorCode.ERR_PARAM);
                    } else {
                        AssertUtil.assertTrue(Objects.equals(betAmount, payAmount), ErrorCode.ERR_PARAM);
                    }
                } else if (Objects.equals(betSubType, BetSubTypeEnum.FREE_SPINS_NORMAL_BET)) {
                    AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
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
                    if (useStarCard) {
                        AssertUtil.assertTrue(Objects.equals(payAmount, betAmount), ErrorCode.ERR_PARAM);
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
        }

        public void malakasValidateParam(BetTypeEnum betType, BetSubTypeEnum betSubType) {
            AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
            if(useStarCard){
                AssertUtil.assertTrue(betAmount >= 10, ErrorCode.ERR_PARAM);
            }else if(useFreeSpin){
                AssertUtil.assertTrue(betAmount >= 10, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(payAmount == 0L, ErrorCode.ERR_PARAM);
            } else {
                AssertUtil.assertTrue(betAmount >= MIN_BET_AMOUNT, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(betAmount <= MAX_BET_AMOUNT, ErrorCode.ERR_PARAM);
            }
            if (Objects.equals(betType, BetTypeEnum.NORMAL_BET)) {
                if (Objects.equals(betSubType, BetSubTypeEnum.NORMAL_BET)) {
                    AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
                    if (useFreeSpin) {
                        AssertUtil.assertTrue(Objects.equals(payAmount, 0L), ErrorCode.ERR_PARAM);
                    } else {
                        AssertUtil.assertTrue(Objects.equals(betAmount, payAmount), ErrorCode.ERR_PARAM);
                    }
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
                    if (useStarCard) {
                        AssertUtil.assertTrue(Objects.equals(payAmount, betAmount), ErrorCode.ERR_PARAM);
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
        }

        public void ibongAdarnaValidateParam(BetTypeEnum betType, BetSubTypeEnum betSubType) {
            AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
            // 投注类型只能正常投注
            AssertUtil.assertTrue(BetTypeEnum.NORMAL_BET.equals(betType), ErrorCode.ERR_PARAM);
            // 投注子类型只能是正常投注/ex投注
            AssertUtil.assertTrue(BetSubTypeEnum.NORMAL_BET.equals(betSubType) || BetSubTypeEnum.EX_BET.equals(betSubType), ErrorCode.ERR_PARAM);
            if (useFreeSpin) {
                AssertUtil.assertTrue(Objects.equals(payAmount, 0L), ErrorCode.ERR_PARAM);
            } else {
                AssertUtil.assertTrue(betAmount >= MIN_BET_AMOUNT, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(betAmount <= MAX_BET_AMOUNT, ErrorCode.ERR_PARAM);
                AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(payAmount > 0L, ErrorCode.ERR_PARAM);

                if (Objects.equals(BetSubTypeEnum.NORMAL_BET, betSubType)) {
                    AssertUtil.assertTrue(Objects.equals(betAmount, payAmount), ErrorCode.ERR_PARAM);
                } else if (Objects.equals(BetSubTypeEnum.EX_BET, betSubType)) {
                    // ex类型，支付金额必须是投注金额的1.5倍
                    long exceptPayAmount = new BigDecimal(String.valueOf(betAmount)).multiply(new BigDecimal("1.5")).longValue();
                    AssertUtil.assertTrue(exceptPayAmount == payAmount, ErrorCode.ERR_PARAM);
                } else {
                    throw new BusinessException(ErrorCode.ERR_PARAM);
                }
            }
        }

        public void bakunawaValidateParam(BetTypeEnum betType, BetSubTypeEnum betSubType) {
            AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
            // 投注类型只能正常投注
            AssertUtil.assertTrue(BetTypeEnum.NORMAL_BET.equals(betType), ErrorCode.ERR_PARAM);
            // 投注子类型只能是正常投注
            AssertUtil.assertTrue(BetSubTypeEnum.NORMAL_BET.equals(betSubType), ErrorCode.ERR_PARAM);
            if (useFreeSpin) {
                AssertUtil.assertTrue(Objects.equals(payAmount, 0L), ErrorCode.ERR_PARAM);
            } else {
                AssertUtil.assertTrue(betAmount >= MIN_BET_AMOUNT, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(betAmount <= MAX_BET_AMOUNT, ErrorCode.ERR_PARAM);
                AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(payAmount > 0L, ErrorCode.ERR_PARAM);
                if (Objects.equals(BetSubTypeEnum.NORMAL_BET, betSubType)) {
                    AssertUtil.assertTrue(Objects.equals(betAmount, payAmount), ErrorCode.ERR_PARAM);
                } else {
                    throw new BusinessException(ErrorCode.ERR_PARAM);
                }
            }
        }

        private void validateStartCartBet(BetTypeEnum betType, BetSubTypeEnum betSubType) {
            // 判断是否使用星卡
            if (useStarCard) {
                AssertUtil.assertTrue(BetTypeEnum.BUY_BET == betType, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(BetSubTypeEnum.BUY_BET == betSubType || BetSubTypeEnum.FREE_SPINS_BUY_BET == betSubType, ErrorCode.ERR_PARAM);
                if (BetSubTypeEnum.BUY_BET == betSubType) {
                    AssertUtil.assertNotNull(rewardConfigId, ErrorCode.ERR_PARAM);
                    AssertUtil.assertNotNull(rewardId, ErrorCode.ERR_PARAM);
                }
            }
        }
        private void validateFreeSpinBet(BetTypeEnum betType, BetSubTypeEnum betSubType) {
            // 判断是否使用预定投注
            if (useFreeSpin) {
                AssertUtil.assertTrue(BetTypeEnum.NORMAL_BET == betType, ErrorCode.ERR_PARAM);
                AssertUtil.assertTrue(BetSubTypeEnum.NORMAL_BET == betSubType || BetSubTypeEnum.FREE_SPINS_NORMAL_BET == betSubType, ErrorCode.ERR_PARAM);
                if (BetSubTypeEnum.NORMAL_BET == betSubType) {
                    AssertUtil.assertStrNotEmpty(campaignId, ErrorCode.ERR_PARAM);
                }
            }
        }
    }
}
