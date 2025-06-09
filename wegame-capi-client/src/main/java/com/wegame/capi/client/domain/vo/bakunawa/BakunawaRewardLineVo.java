package com.wegame.capi.client.domain.vo.bakunawa;

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
@ApiModel("Bakunawa中奖线详情")
@EqualsAndHashCode(callSuper = false)
public class BakunawaRewardLineVo implements Serializable {
    private static final long serialVersionUID = 64878887635380369L;
    @ApiModelProperty("中奖线元素Id: 中奖线第一个中奖元素id")
    private String symbolId;
    @ApiModelProperty("基础倍率")
    private BigDecimal multiple;
    @ApiModelProperty("倍率盘类型：NORMAL-正常倍率， Bakunawa-Bakunawa倍率 ")
    private String multiplePieType;
    @ApiModelProperty("倍率盘倍率")
    private BigDecimal multiplePie;
    @ApiModelProperty("中奖金额：（投注金额 * 基础倍率 * 倍率盘倍率 = 中奖金额")
    private Long rewardAmount;
    @ApiModelProperty("投注金额")
    private Long betAmount;
}
