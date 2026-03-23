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
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "转发模块请求模型")
public class GreRedirectUrlRequest extends BaseRequest implements Validate, Serializable {
    private static final long serialVersionUID = 5858563029658968111L;

    @ApiModelProperty(value = "对应第三方 URL 路径片段，如 getRedirectGameUrl", required = true)
    private String action;
    @ApiModelProperty(value = "业务参数 JSON 对象，将与单钱包公共字段平铺后请求第三方", required = true)
    private Map<String, Object> data;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(action, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(data, ErrorCode.ERR_PARAM);
    }
}
