package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "历史消息明细")
public class HistoryMessageVo implements Serializable {
    private static final long serialVersionUID = 590053783690069207L;
    @ApiModelProperty(value = "玩家名称", required = true)
    private String playerName;
    @ApiModelProperty(value = "渠道ID", required = true)
    private String channelId;
    @ApiModelProperty(value = "消息ID", required = true)
    private String missionId;
    @ApiModelProperty(value = "消息名称", required = true)
    private String missionName;
    @ApiModelProperty(value = "消息描述", required = true)
    private String missionDescription;
    @ApiModelProperty(value = "奖励ID", required = true)
    private String rewardId;
    @ApiModelProperty(value = "奖励名称", required = true)
    private String rewardName;
    @ApiModelProperty(value = "奖励描述", required = true)
    private String rewardDescription;
    @ApiModelProperty(value = "类型:1: COMPLETE_ACHIEVEMENT 2: COMPLETE_TREASURE 3: COMPLETE_DAILY_MISSION " +
            "4: REDEEM_ACHIEVEMENT 5: REDEEM_TREASURE 6: REDEEM_REWARD: ", required = true)
    private Integer type;
    @ApiModelProperty(value = "创建时间", required = true)
    private Long createTime;
}
