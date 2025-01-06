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
    @ApiModelProperty(value = "支付金额", required = true)
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
            // todo fix
        } else if (Objects.equals(betType, BetTypeEnum.FREE_SPINS_BET)) {
            // todo fix
        }
    }
}
