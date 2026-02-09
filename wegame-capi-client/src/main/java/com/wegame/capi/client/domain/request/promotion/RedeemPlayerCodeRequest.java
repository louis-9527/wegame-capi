package com.wegame.capi.client.domain.request.promotion;

import com.wegame.base.Validate;
import com.wegame.base.domain.BaseRequest;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "兑换码请求模型")
public class RedeemPlayerCodeRequest extends BaseRequest implements Validate, Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "兑换码", required = true)
    private String code;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(code, ErrorCode.ERR_PARAM);
    }
}
