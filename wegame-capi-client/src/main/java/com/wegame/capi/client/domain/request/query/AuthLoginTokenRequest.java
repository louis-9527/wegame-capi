package com.wegame.capi.client.domain.request.query;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("验证主站登录token请求模型")
public class AuthLoginTokenRequest implements Validate, Serializable {
    @ApiModelProperty("玩家姓名")
    private String playerName;
    @ApiModelProperty("cp主站token")
    private String token;
    @ApiModelProperty("渠道id")
    private String channelId;
    @ApiModelProperty("平台id")
    private String pid;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(playerName, ErrorCode.ERR_PARAM, "playerName is required");
        AssertUtil.assertStrNotEmpty(token, ErrorCode.ERR_PARAM, "token is required");
        AssertUtil.assertStrNotEmpty(channelId, ErrorCode.ERR_PARAM, "channelId is required");
        AssertUtil.assertStrNotEmpty(pid, ErrorCode.ERR_PARAM, "pid is required");
    }

}
