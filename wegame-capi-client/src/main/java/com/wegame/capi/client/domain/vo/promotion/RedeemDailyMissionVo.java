package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "领取任务响应模型")
public class RedeemDailyMissionVo implements Serializable {
    private static final long serialVersionUID = 8611560671796550491L;
    @ApiModelProperty(value = "任务ID", required = true)
    private String dailyMissionId;
    @ApiModelProperty(value = "玩家姓名", required = true)
    private String playerName;
    @ApiModelProperty(value = "渠道ID", required = true)
    private String channelId;
    @ApiModelProperty(value = "平台ID", required = true)
    private String pId;
    @ApiModelProperty(value = "refId", required = true)
    private String refId;
    @ApiModelProperty(value = "奖励", required = true)
    private Reward reward;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Reward implements Serializable {
        private static final long serialVersionUID = -8127809443738466541L;
        @ApiModelProperty(value = "奖励配置id", required = true)
        private String rewardConfigId;
        @ApiModelProperty(value = "奖励名称", required = true)
        private String name;
        @ApiModelProperty(value = "奖励数量", required = true)
        private Integer count;
        @ApiModelProperty(value = "奖励图片", required = true)
        private String imgUrl;
        @ApiModelProperty(value = "奖励投注金额", required = true)
        private BigDecimal betValue;
        @ApiModelProperty(value = "使用余额要求", required = true)
        private BigDecimal balanceRequired;
        @ApiModelProperty(value = "星级", required = true)
        private Integer starLevel;
        @ApiModelProperty(value = "奖励id", required = true)
        private String rewardId;
        @ApiModelProperty(value = "游戏id", required = true)
        private String gameId;
        @ApiModelProperty(value = "游戏code", required = true)
        private String gameCode;
        @ApiModelProperty(value = "游戏名称", required = true)
        private String gameName;
    }
}
