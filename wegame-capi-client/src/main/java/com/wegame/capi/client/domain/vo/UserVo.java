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
 * user：用户
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("用户")
@EqualsAndHashCode(callSuper = false)
public class UserVo implements Validate, Serializable {
    private static final long serialVersionUID = -5262605043879432192L;
    @ApiModelProperty("主键ID")
    private String id;
    @ApiModelProperty("平台id")
    private String pId;
    @ApiModelProperty("用户姓名")
    private String playerId;
    @ApiModelProperty("用户姓名")
    private String playerName;
    @ApiModelProperty("用户昵称")
    private String nickName;
    @ApiModelProperty("用户级别")
    private String userLevel;
    @ApiModelProperty("用户姓名")
    private String userName;
    @ApiModelProperty("赠送金额")
    private String presentAmount;
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
        AssertUtil.assertStrNotEmpty(pId, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(playerId, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(playerName, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(nickName, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(userLevel, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(userName, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(presentAmount, ErrorCode.ERR_PARAM);
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

