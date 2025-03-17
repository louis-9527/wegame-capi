package com.wegame.capi.client.domain.request.query;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "获取干预事件结果-加密")
public class GetStatusOfEventEncryptRequest implements Validate, Serializable {
    private static final long serialVersionUID = 723094469043351839L;
    private String agentCode;
    private String message;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(agentCode, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(message, ErrorCode.ERR_PARAM);
    }
}
