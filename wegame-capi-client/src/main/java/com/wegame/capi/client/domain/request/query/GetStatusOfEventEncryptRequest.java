package com.wegame.capi.client.domain.request.query;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "查询干预事件结果-加密请求参数")
public class GetStatusOfEventEncryptRequest implements Validate, Serializable {
    private static final long serialVersionUID = 723094469043351839L;
    @ApiModelProperty(value = "agentCode", required = true)
    private String agentCode;
    @ApiModelProperty(value = "message", required = true)
    private String message;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(agentCode, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(message, ErrorCode.ERR_PARAM);
    }
}
