package com.wegame.capi.client.domain.request;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "IbongAdarna正常投注请求参数")
public class IbongAdarnaBetRequest implements BetRequestValidate, Serializable {
    @ApiModelProperty(value = "投注金额", required = true)
    private Long betAmount;
    @ApiModelProperty(value = "支付金额", required = true)
    private Long payAmount;

    @Override
    public void validateParam(GameTypeEnum gameType, BetTypeEnum betType, BetSubTypeEnum betSubType) {
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
