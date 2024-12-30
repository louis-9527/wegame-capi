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
 * user_present_record：用户赠送记录
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("用户赠送记录")
@EqualsAndHashCode(callSuper = false)
public class UserPresentRecordVo implements Validate, Serializable {
    private static final long serialVersionUID = -5895285229481818112L;
    @ApiModelProperty("主键ID")
    private String id;
    @ApiModelProperty("用户ID")
    private String userId;
    @ApiModelProperty("玩家ID")
    private String playerId;
    @ApiModelProperty("类型：1增加,2扣减")
    private Integer type;
    @ApiModelProperty("子类型：11营销赠送, 12游戏赠送失败回滚, 21游戏赠送后扣减")
    private Integer subType;
    @ApiModelProperty("业务Id(增加：赠送流水号Id/,减少为投注记录Id)")
    private String bizId;
    @ApiModelProperty("金额")
    private String amount;
    @ApiModelProperty("状态：0进行中,1已完成")
    private Integer status;
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
        AssertUtil.assertStrNotEmpty(userId, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(playerId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(type, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(subType, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(bizId, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(amount, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(status, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(creator, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(createTime, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(updater, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(updateTime, ErrorCode.ERR_PARAM);
    }

    @Override
    public void validateBiz() {

    }

}

