package com.wegame.capi.common.context;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 用户
 * @author apple
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("用户信息")
public class UserInfo implements Serializable {
    private static final long serialVersionUID = -701740428094376475L;

    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty(value = "平台id")
    private String platformId;
    @ApiModelProperty(value = "用户id")
    private String playerId;
    @ApiModelProperty(value = "用户姓名")
    private String playerName;
    @ApiModelProperty(value = "用户昵称")
    private String nickName;
    @ApiModelProperty(value = "用户级别")
    private String userLevel;
    @ApiModelProperty(value = "用户姓名")
    private String userName;
    @ApiModelProperty(value = "用户生日")
    private String birthday;
    @ApiModelProperty(value = "中台token")
    private String token;
}