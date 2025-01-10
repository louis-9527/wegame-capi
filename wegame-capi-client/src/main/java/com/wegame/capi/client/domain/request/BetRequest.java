package com.wegame.capi.client.domain.request;

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
@ApiModel(description = "投注请求参数")
public class BetRequest implements Validate, Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "游戏Id", required = true)
    private String gameId;
    @ApiModelProperty(value = "投注类型:NORMAL_BET,购买投注:BUY_BET", required = true)
    private String betType;
    @ApiModelProperty(value = "投注子类型:" +
            "NORMAL_BET:正常投注, " +
            "FREE_SPINS_NORMAL_BET: 正常免费轮投注，" +
            "BUY_BET:购买投注, " +
            "FREE_SPINS_BUY_BET: 免费轮购买投注", required = true)
    private String betSubType;
    // todo 调试结束后， 修改类型为Object @bruce
    @ApiModelProperty(value = "投注明细,根据投注类型不同,明细不同", required = true, reference = "MariaMakilingBuyFreeSpinsBetRequest")
    private MariaMakilingBetRequest betDetail;

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
}
