package com.wegame.capi.client.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("正常投注返回")
@EqualsAndHashCode(callSuper = false)
public class MariaMakilingRewardDetailVo implements Serializable {
    private static final long serialVersionUID = 6487808344235380369L;
    @ApiModelProperty("倍率")
    private BigDecimal multiple;
    @ApiModelProperty("赔率")
    private BigDecimal odds;
    @ApiModelProperty("中奖元素坐标列表")
    private List<SymbolIndexVo> symbolIndexList;
    @ApiModelProperty("中奖线元素Id列表")
    private List<String> symbolIdList;
    @ApiModelProperty("中奖金额")
    private Long rewardAmount;
    @ApiModelProperty("投注金额")
    private Long betAmount;
}

