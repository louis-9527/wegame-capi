package com.wegame.capi.biz.domain.biz.promotion;

import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class HistoryMessageDo implements Serializable {
    private static final long serialVersionUID = -7520762475340389572L;
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
    /**
     * 奖励领取详情
     */
    private RewardDetailsDo rewardDetails;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ApiModel(description = "领取详情")
    public static class RewardDetailsDo implements Serializable {
        private static final long serialVersionUID = -2515942190714418574L;
        /**
         * 领取数量
         */
        private Integer count;
        /**
         * 星级
         */
        private Integer startLevel;
    }
}
