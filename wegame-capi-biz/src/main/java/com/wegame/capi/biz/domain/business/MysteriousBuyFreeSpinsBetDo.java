package com.wegame.capi.biz.domain.business;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Mysterious购买免费轮投注请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MysteriousBuyFreeSpinsBetDo implements Validate, Serializable {
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
