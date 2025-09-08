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
@ApiModel(description = "领取成就响应模型")
public class RedeemTreasureVo implements Serializable {
    private static final long serialVersionUID = 2423962589779617794L;
    @ApiModelProperty(value = "宝箱ID", required = true)
    private String treasureId;
    @ApiModelProperty(value = "玩家姓名", required = true)
    private String playerName;
    @ApiModelProperty(value = "渠道id", required = true)
    private String channelId;
    @ApiModelProperty(value = "平台id", required = true)
    private String pId;
    @ApiModelProperty(value = "等级", required = true)
    private Integer level;
    @ApiModelProperty(value = "refId", required = true)
    private String refId;
    @ApiModelProperty(value = "经验值", required = true)
    private String experience;
    @ApiModelProperty(value = "宝箱奖励", required = true)
    private Reward reward;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ApiModel(description = "宝箱奖励")
    public static class Reward implements Serializable {
        private static final long serialVersionUID = 7692352397871708565L;
        @ApiModelProperty(value = "奖励配置id", required = true)
        private String rewardConfigId;
        @ApiModelProperty(value = "奖励名称", required = true)
        private String name;
        @ApiModelProperty(value = "奖励数量", required = true)
        private Integer count;
        @ApiModelProperty(value = "奖励图标", required = true)
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
