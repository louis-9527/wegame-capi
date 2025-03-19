package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "领取宝箱响应模型")
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
    private TreasureRewardVo reward;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "宝箱奖励")
    public static class TreasureRewardVo implements Serializable {
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
    }
}
