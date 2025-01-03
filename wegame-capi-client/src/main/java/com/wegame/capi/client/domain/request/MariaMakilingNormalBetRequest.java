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

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Mysterious正常投注请求参数")
public class MariaMakilingNormalBetRequest implements Validate, Serializable {
    @ApiModelProperty(value = "投注金额", required = true)
    private Long betAmount;

    @Override
    public void validateParam() {
        AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(betAmount > 0L, ErrorCode.ERR_PARAM);
    }
}
