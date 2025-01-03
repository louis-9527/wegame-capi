package com.wegame.capi.biz.domain.biz.bet;


import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Mysterious非购买免费轮投注请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MysteriousNonBuyFreeSpinsBetDo implements Validate, Serializable {
    /**
     * 投注记录Id
     */
    private String betRecordId;
    /**
     * 免费轮记录id
     */
    private Integer freeSpinsId;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(betRecordId, ErrorCode.ERR_PARAM);
    }
}

