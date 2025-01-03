package com.wegame.capi.client.domain.request.update;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
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

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "下注请求")
public class BetRequest<T> implements Validate, Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "游戏Id", required = true)
    private String gameId;
    @ApiModelProperty(value = "投注类型:NORMAL_BET,免费轮投注:FREE_SPINS_BET", required = true)
    private String betType;
    @ApiModelProperty(value = "投注子类型:NORMAL_BET:正常投注, BUY_FREE_SPINS_BET:购买免费轮投注, NON_BUY_FREE_SPINS_BET:非购买免费轮投注", required = true)
    private String betSubType;
    @ApiModelProperty(value = "投注明细,根据投注类型不同,明细不同", required = true)
    private Object betDetail;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(gameId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(GameTypeEnum.valueOfCode(gameId), ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(betType, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(BetTypeEnum.valueOfCode(betType), ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(betSubType, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(BetSubTypeEnum.valueOfCode(betSubType), ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betDetail, ErrorCode.ERR_PARAM);
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "Mysterious正常投注请求参数")
    public class MysteriousNormalBetRequest implements Validate, Serializable {
        @ApiModelProperty(value = "投注金额", required = true)
        private Long betAmount;

        @Override
        public void validateParam() {
            AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(betAmount > 0L, ErrorCode.ERR_PARAM);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "Mysterious购买免费轮投注请求参数")
    public class MysteriousBuyFreeSpinsBetRequest implements Validate, Serializable {
        @ApiModelProperty(value = "投注金额", required = true)
        private Long betAmount;
        @ApiModelProperty(value = "支付金额", required = true)
        private Long payAmount;

        @Override
        public void validateParam() {
            AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(betAmount > 0L, ErrorCode.ERR_PARAM);
            AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(payAmount > 0L, ErrorCode.ERR_PARAM);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "Mysterious非购买免费轮投注请求参数")
    public class MysteriousNonBuyFreeSpinsBetRequest implements Validate, Serializable {
        @ApiModelProperty(value = "游戏记录Id", required = true)
        private String gameRecordId;

        @Override
        public void validateParam() {
            AssertUtil.assertStrNotEmpty(gameRecordId, ErrorCode.ERR_PARAM);
        }
    }

}
