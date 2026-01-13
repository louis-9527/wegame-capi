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
@ApiModel(description = "FreeSpin请求模型")
public class FreeSpinOfPlayerRequest extends BaseRequest implements Validate, Serializable {
    private static final long serialVersionUID = -3133207779105163762L;
    @ApiModelProperty(value = "checkCurrent true-过滤；false-不过滤", required = false)
    private Boolean checkCurrent = false;

    @Override
    public void validateParam() {
        AssertUtil.assertNotNull(checkCurrent, ErrorCode.ERR_PARAM);
    }
}
