package com.wegame.capi.client.domain.vo.kapre;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Kapre中奖线详情")
@EqualsAndHashCode(callSuper = false)
public class KapreRewardLineVo implements Serializable {
    private static final long serialVersionUID = 644353235380369L;
    @ApiModelProperty("中奖线元素Id: 中奖线第一个中奖元素id")
    private String symbolId;
    @ApiModelProperty("倍率")
    private BigDecimal multiple;
    @ApiModelProperty("赔率")
    private BigDecimal odds;
    @ApiModelProperty("中奖线类型：THREE（三连），FOUR（四连），FIVE（5连），SIX(6连)")
    private String rewardLineType;
    @ApiModelProperty("中奖线子类型：COMMON（普通），SCATTER（SCATTER）")
    private String rewardLineSubType;
    @ApiModelProperty("中奖线数量")
    private Integer rewardLineCount;
    @ApiModelProperty("中奖金额： COMMON中奖计算公式（投注金额 * 赔率 * (中奖线数量WAYS) = 中奖金额）； CASH中奖计算公式（投注金额 * (现金元素总倍率) = 中奖金额）；")
    private Long rewardAmount;
    @ApiModelProperty("投注金额")
    private Long betAmount;
}