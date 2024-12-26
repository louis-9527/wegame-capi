package com.wegame.capi.client.domain.request;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 用户登录请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "用户登录请求参数")
public class UserLoginRequest implements Validate, Serializable {
    @ApiModelProperty(value = "平台id", required = true)
    private String platformId;
    @ApiModelProperty(value = "渠道id", required = true)
    private String chanelId;
    @ApiModelProperty(value = "用户id", required = true)
    private String playerId;
    @ApiModelProperty(value = "用户姓名", required = true)
    private String playerName;
    @ApiModelProperty(value = "用户昵称", required = false)
    private String nickName;
    @ApiModelProperty(value = "用户级别", required = false)
    private String userLevel;
    @ApiModelProperty(value = "用户姓名", required = false)
    private String userName;
    @ApiModelProperty(value = "用户生日", required = false)
    private String birthday;
    @ApiModelProperty(value = "中台token", required = true)
    private String token;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(platformId, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(playerId, ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(playerName.length() <= 50, ErrorCode.ERR_PARAM);
        if (StringUtils.isNotBlank(nickName)) {
            AssertUtil.assertTrue(nickName.length() <= 50, ErrorCode.ERR_PARAM);
        }
        if (StringUtils.isNotBlank(userLevel)) {
            AssertUtil.assertTrue(userLevel.length() <= 50, ErrorCode.ERR_PARAM);
        }
        if (StringUtils.isNotBlank(userName)) {
            AssertUtil.assertTrue(userName.length() <= 50, ErrorCode.ERR_PARAM);
        }
        if (StringUtils.isNotBlank(birthday)) {
            AssertUtil.assertTrue(birthday.length() <= 20, ErrorCode.ERR_PARAM);
        }
        AssertUtil.assertStrNotEmpty(token, ErrorCode.ERR_PARAM);
    }
}
