package com.wegame.capi.common.context;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 玩家
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
    @ApiModelProperty("玩家id")
    private String playerId;
    @ApiModelProperty("玩家姓名")
    private String name;
    @ApiModelProperty("玩家昵称")
    private String nickName;
    @ApiModelProperty("玩家真实姓名")
    private String realName;
    @ApiModelProperty("电话号码")
    private String phoneNumber;
    @ApiModelProperty("性别: -1未知")
    private Integer gender;
    @ApiModelProperty("是否设置了密码：0未设置")
    private Boolean hasPassword;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("邮箱")
    private String email;
    @ApiModelProperty(value = "头像")
    private String playerAvatar;
    @ApiModelProperty(value = "国家")
    private String nationality;
    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty("地址")
    private String address;
    @ApiModelProperty("银行名称")
    private String bankName;
    @ApiModelProperty("银行账户")
    private String bankAccount;
    @ApiModelProperty("银行账户名称")
    private String bankAccountName;
    @ApiModelProperty("平台id")
    private String platformId;
    @ApiModelProperty("注册时间")
    private Long registrationTime;
    @ApiModelProperty("token")
    private String token;
}