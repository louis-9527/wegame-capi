package com.wegame.capi.client.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wegame.base.Validate;
import com.wegame.base.domain.BaseRequest;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.AbTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * 用户登录请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "用户登录请求参数")
public class UserLoginRequest extends BaseRequest implements Validate, Serializable {
    @ApiModelProperty(value = "平台id", required = true)
    @JsonProperty("pId")
    private String pId;
    @ApiModelProperty(value = "渠道id", required = true)
    private String channelId;
    @ApiModelProperty(value = "游戏id", required = true)
    private String gameId;
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
    @ApiModelProperty(value = "中台token", required = true)
    private String token;
    @ApiModelProperty(value = "AB测试(afe: A（数值化）, aff: B（模拟盘面/模版）)", required = false)
    private String ut = AbTypeEnum.B.getId();
    @ApiModelProperty(value = "扩展参数", required = false)
    private Map<String, Object> ext;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(pId, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(channelId, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(gameId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(GameTypeEnum.valueOfId(gameId), ErrorCode.ERR_PARAM);
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
        AssertUtil.assertStrNotEmpty(token, ErrorCode.ERR_PARAM);
    }
}
