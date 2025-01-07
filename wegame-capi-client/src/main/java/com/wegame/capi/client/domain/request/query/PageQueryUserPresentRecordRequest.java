package com.wegame.capi.client.domain.request.query;

import com.wegame.base.domain.PageQueryRequest;
import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;


/**
 * 分页查询用户赠送记录请求参数
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "分页查询用户赠送记录请求参数", parent = PageQueryRequest.class)
public class PageQueryUserPresentRecordRequest extends PageQueryRequest implements Validate {
    private static final long serialVersionUID = -5875545618001062912L;
    @ApiModelProperty(value = "主键ID", required = false)
    private String id;
    @ApiModelProperty(value = "用户ID", required = false)
    private String userId;
    @ApiModelProperty(value = "玩家ID", required = false)
    private String playerId;
    @ApiModelProperty(value = "类型：1增加,2扣减", required = false)
    private Integer type;
    @ApiModelProperty(value = "子类型：11营销赠送, 12游戏赠送失败回滚, 21游戏赠送后扣减", required = false)
    private Integer subType;
    @ApiModelProperty(value = "业务Id(增加：赠送流水号Id/,减少为投注记录Id)", required = false)
    private String bizId;
    @ApiModelProperty(value = "金额", required = false)
    private String amount;
    @ApiModelProperty(value = "状态：0进行中,1已完成", required = false)
    private Integer status;
    @ApiModelProperty(value = "创建人", required = false)
    private String creator;
    @ApiModelProperty(value = "创建时间", required = false)
    private Long createTime;
    @ApiModelProperty(value = "更新人", required = false)
    private String updater;
    @ApiModelProperty(value = "更新时间", required = false)
    private Long updateTime;
    @ApiModelProperty(value = "开始创建时间", required = false)
    private Long startCreateTime;
    @ApiModelProperty(value = "结束创建时间", required = false)
    private Long endCreateTime;
    @ApiModelProperty(value = "开始更新时间", required = false)
    private Long startUpdateTime;
    @ApiModelProperty(value = "结束更新时间", required = false)
    private Long endUpdateTime;

    @Override
    public void validateParam() {
        // AssertUtil.assertStrNotEmpty(id, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(userId, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(playerId, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(type, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(subType, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(bizId, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(amount, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(status, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(creator, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(createTime, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(updater, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(updateTime, ErrorCode.ERR_PARAM);
    }

    @Override
    public void validateBiz() {

    }
}

