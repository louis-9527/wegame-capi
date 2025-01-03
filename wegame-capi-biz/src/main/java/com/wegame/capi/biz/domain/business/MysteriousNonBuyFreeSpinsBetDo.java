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
 * Mysterious非购买免费轮投注请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MysteriousNonBuyFreeSpinsBetDo implements Validate, Serializable {
    @ApiModelProperty(value = "游戏记录Id", required = true)
    private String gameRecordId;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(gameRecordId, ErrorCode.ERR_PARAM);
    }
}

