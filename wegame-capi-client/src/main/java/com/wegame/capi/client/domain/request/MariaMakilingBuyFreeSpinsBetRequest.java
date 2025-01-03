package com.wegame.capi.client.domain.request;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
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
@ApiModel(description = "MariaMakiling购买免费轮投注请求参数")
public class MariaMakilingBuyFreeSpinsBetRequest implements Validate, Serializable {
    @ApiModelProperty(value = "投注金额", required = true)
    private Long betAmount;
    @ApiModelProperty(value = "支付金额", required = true)
    private Long payAmount;
    @ApiModelProperty(value = "投注记录ID, 获得免费轮的投注记录的投注id，非必填", required = false)
    private String betRecordId;
    @ApiModelProperty(value = "免费轮ID, 非必填", required = false)
    private Integer freeSpinsId;

    @Override
    public void validateParam() {
        if (Objects.isNull(freeSpinsId)) {
            AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(betAmount > 0L, ErrorCode.ERR_PARAM);
            AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(payAmount > 0L, ErrorCode.ERR_PARAM);
        } else {
            AssertUtil.assertNotNull(betRecordId, ErrorCode.ERR_PARAM);
        }
    }
}
