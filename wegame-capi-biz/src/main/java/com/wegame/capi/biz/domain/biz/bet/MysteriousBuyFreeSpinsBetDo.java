package com.wegame.capi.biz.domain.biz.bet;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * Mysterious购买免费轮投注请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MysteriousBuyFreeSpinsBetDo implements Validate, Serializable {
    /**
     * 投注记录id
     */
    private Long betRecordId;
    /**
     * 免费轮记录id
     */
    private Integer freeSpinsId;
    /**
     * 投注金额
     */
    private Long betAmount;
    /**
     * 支付金额
     */
    private Long payAmount;

    @Override
    public void validateParam() {
        if (Objects.nonNull(freeSpinsId)) {
            AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(betAmount > 0L, ErrorCode.ERR_PARAM);
            AssertUtil.assertNotNull(payAmount, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(payAmount > 0L, ErrorCode.ERR_PARAM);
        } else {
            AssertUtil.assertNotNull(betRecordId, ErrorCode.ERR_PARAM);
        }
    }
}
