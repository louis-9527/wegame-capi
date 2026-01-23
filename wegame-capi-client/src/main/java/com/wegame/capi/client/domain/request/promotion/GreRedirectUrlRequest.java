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
@ApiModel(description = "转发模块请求模型")
public class GreRedirectUrlRequest extends BaseRequest implements Validate, Serializable {
    private static final long serialVersionUID = 5858563029658968111L;

    @ApiModelProperty(value = "action", required = true)
    private String action;
    @ApiModelProperty(value = "data", required = true)
    private GreRedirectUrlDataRequest data;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(action, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(data, ErrorCode.ERR_PARAM);
//        AssertUtil.assertStrNotEmpty(data.getGameId(), ErrorCode.ERR_PARAM);
    }
}
