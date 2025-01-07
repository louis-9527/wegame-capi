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
 * game_record：游戏记录
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("游戏记录")
@EqualsAndHashCode(callSuper = false)
public class GameRecordVo<T> implements Validate, Serializable {
    private static final long serialVersionUID = -9065666521052837888L;
    @ApiModelProperty("主键ID")
    private String id;
    @ApiModelProperty("平台id")
    private String pId;
    @ApiModelProperty("渠道id")
    private String channelId;
    @ApiModelProperty("游戏id")
    private String gameId;
    @ApiModelProperty("用户id")
    private String userId;
    @ApiModelProperty("玩家id")
    private String playerId;
    @ApiModelProperty("玩家名称")
    private String playerName;
    @ApiModelProperty("投注类型 1正常投注 2免费轮投注")
    private Integer betType;
    @ApiModelProperty("投注子类型 11正常投注 21购买免费轮投注 22非购买免费轮投注")
    private Integer betSubType;
    @ApiModelProperty("投注id（单钱包）")
    private String betId;
    @ApiModelProperty("投注流水号（对应单钱包refId）")
    private String betSerialNo;
    @ApiModelProperty("投注金额 单位：分")
    private String betAmount;
    @ApiModelProperty("派彩金额 单位：分")
    private String payOutAmount;
    @ApiModelProperty("营销标识 0否 1是")
    private Integer marketFlag;
    @ApiModelProperty("状态 0进行中 1已完结 2无效")
    private Integer status;
    @ApiModelProperty("免费轮获得标识 0否 1是")
    private Integer freeSpinsFlag;
    @ApiModelProperty("免费轮次数")
    private Integer freeSpinsTimes;
    @ApiModelProperty("明细json")
    private T detail;
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
        AssertUtil.assertStrNotEmpty(channelId, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(gameId, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(userId, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(playerId, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(playerName, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betType, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betSubType, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(betId, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(betSerialNo, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(betAmount, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(payOutAmount, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(marketFlag, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(status, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(freeSpinsFlag, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(freeSpinsTimes, ErrorCode.ERR_PARAM);
//        AssertUtil.assertStrNotEmpty(detail, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(creator, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(createTime, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(updater, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(updateTime, ErrorCode.ERR_PARAM);
    }

    @Override
    public void validateBiz() {

    }

}

