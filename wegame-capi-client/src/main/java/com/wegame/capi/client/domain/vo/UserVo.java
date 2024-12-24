package com.wegame.capi.client.domain.vo;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * user：玩家用户
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("玩家用户")
@EqualsAndHashCode(callSuper = false)
public class UserVo implements Validate, Serializable {
    private static final long serialVersionUID = -7365176731574744064L;
    @ApiModelProperty("主键ID")
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
    @ApiModelProperty("玩家头像")
    private String playerAvatar;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("邮箱")
    private String email;
    @ApiModelProperty("国家")
    private String nationality;
    @ApiModelProperty("省")
    private String province;
    @ApiModelProperty("市")
    private String city;
    @ApiModelProperty("地址")
    private String address;
    @ApiModelProperty("平台id")
    private String platformId;
    @ApiModelProperty("注册时间")
    private Long registrationTime;
    @ApiModelProperty("用户状态：1为正常，其他非正常")
    private Integer status;
    @ApiModelProperty("扩展信息")
    private String extend;
    @ApiModelProperty("创建人")
    private String creator;
    @ApiModelProperty("创建时间")
    private Long createTime;
    @ApiModelProperty("更新人")
    private String updater;
    @ApiModelProperty("更新时间")
    private Long updateTime;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(id, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(playerId, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(name, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(nickName, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(realName, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(phoneNumber, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(gender, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(playerAvatar, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(password, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(email, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(nationality, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(province, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(city, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(address, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(platformId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(registrationTime, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(status, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(extend, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(creator, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(createTime, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(updater, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(updateTime, ErrorCode.ERR_PARAM);
    }

    @Override
    public void validateBiz() {

    }

}

