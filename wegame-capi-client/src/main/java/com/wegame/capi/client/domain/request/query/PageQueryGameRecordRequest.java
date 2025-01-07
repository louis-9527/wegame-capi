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
 * 分页查询游戏记录请求参数
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "分页查询游戏记录请求参数", parent = PageQueryRequest.class)
public class PageQueryGameRecordRequest extends PageQueryRequest implements Validate {
    private static final long serialVersionUID = -8868654188320789504L;
    @ApiModelProperty(value = "主键ID", required = false)
    private String id;
    @ApiModelProperty(value = "平台id", required = false)
    private String pId;
    @ApiModelProperty(value = "渠道id", required = false)
    private String channelId;
    @ApiModelProperty(value = "游戏id", required = false)
    private String gameId;
    @ApiModelProperty(value = "用户id", required = false)
    private String userId;
    @ApiModelProperty(value = "玩家id", required = false)
    private String playerId;
    @ApiModelProperty(value = "玩家名称", required = false)
    private String playerName;
    @ApiModelProperty(value = "投注类型 1正常投注 2免费轮投注", required = false)
    private Integer betType;
    @ApiModelProperty(value = "投注子类型 11正常投注 21购买免费轮投注 22非购买免费轮投注", required = false)
    private Integer betSubType;
    @ApiModelProperty(value = "投注id（单钱包）", required = false)
    private String betId;
    @ApiModelProperty(value = "投注流水号（对应单钱包refId）", required = false)
    private String betSerialNo;
    @ApiModelProperty(value = "投注金额 单位：分", required = false)
    private String betAmount;
    @ApiModelProperty(value = "派彩金额 单位：分", required = false)
    private String payOutAmount;
    @ApiModelProperty(value = "营销标识 0否 1是", required = false)
    private Integer marketFlag;
    @ApiModelProperty(value = "状态 0进行中 1已完结 2无效", required = false)
    private Integer status;
    @ApiModelProperty(value = "免费轮获得标识 0否 1是", required = false)
    private Integer freeSpinsFlag;
    @ApiModelProperty(value = "免费轮次数", required = false)
    private Integer freeSpinsTimes;
    @ApiModelProperty(value = "明细json", required = false)
    private String detail;
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
        // AssertUtil.assertStrNotEmpty(pId, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(channelId, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(gameId, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(userId, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(playerId, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(playerName, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(betType, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(betSubType, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(betId, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(betSerialNo, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(betAmount, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(payOutAmount, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(marketFlag, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(status, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(freeSpinsFlag, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(freeSpinsTimes, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(detail, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(creator, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(createTime, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(updater, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(updateTime, ErrorCode.ERR_PARAM);
    }

    @Override
    public void validateBiz() {

    }
}

