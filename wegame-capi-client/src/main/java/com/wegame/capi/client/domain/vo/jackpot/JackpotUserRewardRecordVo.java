package com.wegame.capi.client.domain.vo.jackpot;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * jackpot_user_reward_record：用户jackpot中奖记录
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("用户jackpot中奖记录")
@EqualsAndHashCode(callSuper = false)
public class JackpotUserRewardRecordVo implements Validate, Serializable {
    private static final long serialVersionUID = -1946033553559245824L;
    @ApiModelProperty("主键ID")
    private String id;
    @ApiModelProperty("用户id")
    private String userId;
    @ApiModelProperty("玩家id")
    private String playerName;
    @ApiModelProperty("投注id")
    private Long betRecordId;
    @ApiModelProperty("投注id")
    private Long gameRecordId;
    @ApiModelProperty("投注id（单钱包）")
    private String betId;
    @ApiModelProperty("奖池id")
    private String rewardPoolId;
    @ApiModelProperty("类型 1：grand 2:major 3:minor 4:mini 5:betBonus")
    private Integer rewardPoolType;
    @ApiModelProperty("中奖金额.单位：厘")
    private Long rewardAmount;
    @ApiModelProperty("派奖流水号（refid）")
    private String rewardGrantSerialNo;
    @ApiModelProperty("派奖状态.0:未派奖.1:派奖中（状态待定）.2:已派奖")
    private Integer rewardGrantStatus;
    @ApiModelProperty("派奖成功时间")
    private Long rewardGrantTime;
    @ApiModelProperty("创建时间/中奖时间")
    private Long createTime;
}

