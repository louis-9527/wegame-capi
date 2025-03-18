package com.wegame.capi.biz.domain.biz.promotion;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryMessageDo {

    /**
     * 玩家名称
     */
    private String playerName;
    /**
     * 渠道ID
     */
    private String channelId;
    /**
     * 消息ID
     */
    private String missionId;
    /**
     * 消息名称
     */
    private String missionName;
    /**
     * 消息描述
     */
    private String missionDescription;
    /**
     * 奖励ID
     */
    private String rewardId;
    /**
     * 奖励名称
     */
    private String rewardName;
    /**
     * 奖励描述
     */
    private String rewardDescription;
    /**
     * 类型:1: COMPLETE_ACHIEVEMENT 2: COMPLETE_TREASURE 3: COMPLETE_DAILY_MISSION
     * 4: REDEEM_ACHIEVEMENT 5: REDEEM_TREASURE 6: REDEEM_REWARD
     */
    private Integer type;
    /**
     * 创建时间
     */
    private Long createTime;
}
