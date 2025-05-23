package com.wegame.capi.client.domain.request.query;

import com.wegame.base.domain.PageQueryRequest;
import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;


/**
 * 分页查询用户jackpot中奖记录请求参数
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "分页查询用户jackpot中奖记录请求参数", parent = PageQueryRequest.class)
public class PageQueryJackpotUserRewardRecordRequest extends PageQueryRequest implements Validate {
    private static final long serialVersionUID = -6826899944453791744L;
    @ApiModelProperty(value = "主键ID", required = false)
    private String id;
    @ApiModelProperty(value = "用户id", required = false)
    private String userId;
    @ApiModelProperty(value = "玩家id", required = false)
    private String playerName;
    @ApiModelProperty(value = "投注id", required = false)
    private Long betRecordId;
    @ApiModelProperty(value = "投注id", required = false)
    private Long gameRecordId;
    @ApiModelProperty(value = "投注id（单钱包）", required = false)
    private String betId;
    @ApiModelProperty(value = "奖池id", required = false)
    private String rewardPoolId;
    @ApiModelProperty(value = "类型 1：grand 2:major 3:minor 4:mini 5:betBonus", required = false)
    private Integer rewardPoolType;
    @ApiModelProperty(value = "类型列表 ： 类型 1：grand 2:major 3:minor 4:mini 5:betBonus", required = false)
    private List<Integer> rewardPoolTypeList;
    @ApiModelProperty(value = "中奖金额.单位：厘", required = false)
    private String rewardAmount;
    @ApiModelProperty(value = "派奖流水号（refid）", required = false)
    private String rewardGrantSerialNo;
    @ApiModelProperty(value = "派奖状态.0:未派奖.1:派奖中（状态待定）.2:已派奖", required = false)
    private Integer rewardGrantStatus;
    @ApiModelProperty(value = "派奖成功时间", required = false)
    private Long rewardGrantTime;
    @ApiModelProperty(value = "创建人", required = false)
    private String creator;
    @ApiModelProperty(value = "创建时间/中奖时间", required = false)
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
        // AssertUtil.assertStrNotEmpty(playerName, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(betRecordId, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(gameRecordId, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(betId, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(rewardPoolId, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(rewardPoolType, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(rewardAmount, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(rewardGrantSerialNo, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(rewardGrantStatus, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(rewardGrantTime, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(creator, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(createTime, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(updater, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(updateTime, ErrorCode.ERR_PARAM);
    }

    @Override
    public void validateBiz() {

    }
}

