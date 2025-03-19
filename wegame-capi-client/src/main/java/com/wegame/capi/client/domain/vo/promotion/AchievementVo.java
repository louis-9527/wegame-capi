package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "成就")
public class AchievementVo implements  Serializable {
    private static final long serialVersionUID = -1704382640858325682L;
    @ApiModelProperty(value = "成就id", required = true)
    private String id;
    @ApiModelProperty(value = "成就名称", required = true)
    private String name;
    @ApiModelProperty(value = "成就描述", required = true)
    private String description;
    @ApiModelProperty(value = "成就图标", required = true)
    private String iconUrl;
    @ApiModelProperty(value = "进度", required = true)
    private ProgressVo progressList;
    @ApiModelProperty(value = "完成时间", required = true)
    private Long completeAt;
    @ApiModelProperty(value = "状态 1进行中 2可兑换 3兑换", required = true)
    private Integer status;
    @ApiModelProperty(value = "兑换id", required = true)
    private String redeemId;
    @ApiModelProperty(value = "奖励", required = true)
    private List<RewardVo> rewards;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgressVo implements Serializable {
        private static final long serialVersionUID = 1L;
        @ApiModelProperty(value = "目标值", required = true)
        private BigDecimal target;
        @ApiModelProperty(value = "当前值", required = true)
        private BigDecimal current;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RewardVo implements Serializable {
        private static final long serialVersionUID = 1L;
        @ApiModelProperty(value = "奖励配置id", required = true)
        private String rewardConfigId;
        @ApiModelProperty(value = "奖励名称", required = true)
        private String name;
        @ApiModelProperty(value = "奖励类型", required = true)
        private String type;
        @ApiModelProperty(value = "奖励值", required = true)
        private String starLevel;
        @ApiModelProperty(value = "数量", required = true)
        private Integer count;
        @ApiModelProperty(value = "图片", required = true)
        private String imgUrl;
        @ApiModelProperty(value = "奖励投注金额", required = true)
        private BigDecimal betValue;
        @ApiModelProperty(value = "使用余额要求", required = true)
        private BigDecimal balanceRequired;
    }
}
