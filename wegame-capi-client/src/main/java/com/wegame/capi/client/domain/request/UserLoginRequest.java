package com.wegame.capi.client.domain.request;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.SitePlatformEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.NumberUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * 用户登录请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "用户登录请求参数")
public class UserLoginRequest implements Validate, Serializable {
    @ApiModelProperty(value = "玩家id", required = true)
    private String playerId;
    @ApiModelProperty(value = "玩家姓名", required = true)
    private String name;
    @ApiModelProperty(value = "玩家昵称", required = false)
    private String nickName;
    @ApiModelProperty(value = "玩家真实姓名", required = false)
    private String realName;
    @ApiModelProperty(value = "电话号码", required = false)
    private String phoneNumber;
    @ApiModelProperty(value = "性别: -1未知", required = false)
    private Integer gender;
    @ApiModelProperty(value = "头像", required = false)
    private String playerAvatar;
    @ApiModelProperty(value = "密码", required = false)
    private String password;
    @ApiModelProperty(value = "邮箱", required = false)
    private String email;
    @ApiModelProperty(value = "国家", required = false)
    private String nationality;
    @ApiModelProperty(value = "省", required = false)
    private String province;
    @ApiModelProperty(value = "市", required = false)
    private String city;
    @ApiModelProperty(value = "地址", required = false)
    private String address;
    @ApiModelProperty(value = "平台id", required = true)
    private String platformId;
    @ApiModelProperty(value = "注册时间", required = false)
    private Long registrationTime;
    @ApiModelProperty(value = "时间戳", required = false)
    private String timestamp;
    @ApiModelProperty(value = "签名参数", required = false)
    private String sign;
    @ApiModelProperty(value = "中台token", required = true)
    private String token;
    @ApiModelProperty(value = "文件服务器domain", required = false)
    private String ftpDomain;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(playerId, ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(name.length() <= 50, ErrorCode.ERR_PARAM);
        if (StringUtils.isNotBlank(nickName)) {
            AssertUtil.assertTrue(nickName.length() <= 50, ErrorCode.ERR_PARAM);
        }
        if (StringUtils.isNotBlank(realName)) {
            AssertUtil.assertTrue(realName.length() <= 50, ErrorCode.ERR_PARAM);
        }
        if (StringUtils.isNotBlank(phoneNumber)) {
            AssertUtil.assertTrue(phoneNumber.length() <= 20, ErrorCode.ERR_PARAM);
        }
        if (Objects.nonNull(gender)) {
            AssertUtil.assertTrue(NumberUtil.isNumberInRange(gender, -1, 1 ), ErrorCode.ERR_PARAM);
        }
        if (Objects.nonNull(playerAvatar)) {
            AssertUtil.assertTrue(playerAvatar.length() <= 255, ErrorCode.ERR_PARAM);
        }
        if (StringUtils.isNotBlank(password)) {
            AssertUtil.assertTrue(password.length() <= 255, ErrorCode.ERR_PARAM);
        }
        if (StringUtils.isNotBlank(email)) {
            AssertUtil.assertTrue(email.length() <= 100, ErrorCode.ERR_PARAM);
        }
        if (StringUtils.isNotBlank(address)) {
            AssertUtil.assertTrue(address.length() <= 255, ErrorCode.ERR_PARAM);
        }
        AssertUtil.assertNotNull(SitePlatformEnum.valueOfCode(platformId), ErrorCode.ERR_PARAM);
        if (Objects.nonNull(registrationTime)) {
            AssertUtil.assertTrue(registrationTime <= DateUtil.MAX_MYSQL_TIMESTAMP_MILL_SECONDS, ErrorCode.ERR_PARAM);
        }
        AssertUtil.assertStrNotEmpty(token, ErrorCode.ERR_PARAM);
    }
}
