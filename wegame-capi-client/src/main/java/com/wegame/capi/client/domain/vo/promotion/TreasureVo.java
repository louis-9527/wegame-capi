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
@ApiModel(description = "宝箱响应模型")
public class TreasureVo implements  Serializable {
    private static final long serialVersionUID = -635350446049642268L;
    @ApiModelProperty(value = "宝箱ID", required = true)
    private String treasureId;
    @ApiModelProperty(value = "宝箱名称", required = true)
    private String name;
    @ApiModelProperty(value = "当前进度", required = true)
    private BigDecimal currentExperience;
    @ApiModelProperty(value = "目标进度", required = true)
    private BigDecimal targetExperience;
    @ApiModelProperty(value = "状态:1进行中 2可兑换 3已兑换", required = true)
    private Integer status;
    @ApiModelProperty(value = "宝箱等级", required = true)
    private Integer level;
    @ApiModelProperty(value = "宝箱奖励列表", required = true)
    private List<Reward> rewards;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ApiModel(description = "宝箱奖励")
    public static class Reward implements Serializable {
        private static final long serialVersionUID = 7692352397871708565L;
        @ApiModelProperty(value = "奖励配置id")
        private String rewardConfigId;
        @ApiModelProperty(value = "奖励名称")
        private String name;
        @ApiModelProperty(value = "奖励数量")
        private Integer count;
        @ApiModelProperty(value = "奖励图片")
        private String imgUrl;
        @ApiModelProperty(value = "奖励投注金额")
        private BigDecimal betValue;
        @ApiModelProperty(value = "使用余额要求")
        private BigDecimal balanceRequired;
        @ApiModelProperty(value = "星级")
        private Integer starLevel;
    }
}


