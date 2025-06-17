package com.wegame.capi.client.domain.request;

import com.wegame.base.Validate;
import com.wegame.base.domain.BaseRequest;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.validator.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
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
        public static final long MIN_BET_AMOUNT = 300L;
        public static final long MAX_BET_AMOUNT = 10000000L;
        public static final long MULTIPLE = 35;

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
                MariaMakilingValidator.validateParam(betType, betSubType, this);
                CommonGameValidator.validateStarCardBet(betType, betSubType, this);
                CommonGameValidator.validateFreeSpinBet(betType, betSubType, this);
            } else if (Objects.equals(gameType, GameTypeEnum.MALAKAS)) {
                MalakasValidator.validateParam(betType, betSubType, this);
                CommonGameValidator.validateStarCardBet(betType, betSubType, this);
                CommonGameValidator.validateFreeSpinBet(betType, betSubType, this);
            } else if (Objects.equals(gameType, GameTypeEnum.IBONG_ADARNA)) {
                IbongAdarnaValidator.validateParam(betType, betSubType, this);
                CommonGameValidator.validateFreeSpinBet(betType, betSubType, this);
            } else if (Objects.equals(gameType, GameTypeEnum.DIWATA_MAYARI)) {
                DiwataMayariValidator.validateParam(betType, betSubType, this);
                CommonGameValidator.validateFreeSpinBet(betType, betSubType, this);
            } else {
                throw new BusinessException(ErrorCode.ERR_PARAM, "NOT SUPPORT");
            }
        }
    }
}
