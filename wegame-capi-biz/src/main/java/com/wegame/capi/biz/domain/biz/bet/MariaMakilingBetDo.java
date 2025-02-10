package com.wegame.capi.biz.domain.biz.bet;


import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Mysterious正常投注请求参数
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MariaMakilingBetDo implements Validate ,Serializable {
    @ApiModelProperty(value = "投注金额", required = true)
    private Long betAmount;
    /**
     * 投注记录id
     */
    private Long betRecordId;
    /**
     * 免费轮游戏记录id
     */
    private Long gameRecordId;
    /**
     * 支付金额
     */
    private Long payAmount;

    @Override
    public void validateParam() {
        AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(betAmount > 0L, ErrorCode.ERR_PARAM);
    }
}

