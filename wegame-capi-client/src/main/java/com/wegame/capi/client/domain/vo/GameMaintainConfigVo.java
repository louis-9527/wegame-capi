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
 * game_maintain_config：游戏维护配置
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("游戏维护配置")
@EqualsAndHashCode(callSuper = false)
public class GameMaintainConfigVo implements Validate, Serializable {
    private static final long serialVersionUID = -3526433233123028992L;
    @ApiModelProperty("主键ID/配置id")
    private String id;
    @ApiModelProperty("游戏ID")
    private String gameId;
    @ApiModelProperty("维护开始时间")
    private Long maintainStartTime;
    @ApiModelProperty("维护结束时间")
    private Long maintainEndTime;
    @ApiModelProperty("启用状态 0:禁用 1:启用")
    private Integer enableStatus;
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
        AssertUtil.assertStrNotEmpty(gameId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(maintainStartTime, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(maintainEndTime, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(enableStatus, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(creator, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(createTime, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(updater, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(updateTime, ErrorCode.ERR_PARAM);
    }

    @Override
    public void validateBiz() {

    }

}

