package com.wegame.capi.client.domain.request;

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
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "MariaMakiling正常投注请求参数")
public class MariaMakilingBetRequest implements BetRequestValidate, Serializable {
    @ApiModelProperty(value = "投注金额", required = true)
    private Long betAmount;
    @ApiModelProperty(value = "支付金额/购买金额", required = true)
    private Long payAmount;
    @ApiModelProperty(value = "投注记录ID, 获得免费轮的投注记录的投注id，非必填", required = false)
    private String betRecordId;
    @ApiModelProperty(value = "免费轮ID, 非必填", required = false)
    private Integer freeSpinsId;

    @Override
    public void validateParam(GameTypeEnum gameType, BetTypeEnum betType, BetSubTypeEnum betSubType) {
        AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(betAmount > 0L, ErrorCode.ERR_PARAM);
        if (Objects.equals(betType, BetTypeEnum.NORMAL_BET)) {
            if (Objects.equals(betSubType, BetSubTypeEnum.FREE_SPINS_NORMAL_BET)) {
                AssertUtil.assertStrNotEmpty(betRecordId, ErrorCode.ERR_PARAM);
                AssertUtil.assertNotNull(freeSpinsId, ErrorCode.ERR_PARAM);
            }
        } else if (Objects.equals(betType, BetTypeEnum.BUY_BET)) {
            AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(payAmount > 0L, ErrorCode.ERR_PARAM);
            if (Objects.equals(betSubType, BetSubTypeEnum.FREE_SPINS_BUY_BET)) {
                AssertUtil.assertStrNotEmpty(betRecordId, ErrorCode.ERR_PARAM);
                AssertUtil.assertNotNull(freeSpinsId, ErrorCode.ERR_PARAM);
            }
        }
    }
}
