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
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("MariaMakiling投注结果")
@EqualsAndHashCode(callSuper = false)
public class MariaMakilingBetResultVo implements Serializable {
    private static final long serialVersionUID = -4792907227811616042L;
    @ApiModelProperty("中奖类型: NONE未中奖，SINGLE单奖励，MULTIPLE多奖励 ")
    private String rewardType;
    @ApiModelProperty("中奖子类型: NONE:未中奖,other,nice,mega,super,sensational")
    private String rewardSubType;
    @ApiModelProperty("中奖总金额")
    private Long totalRewardAmount;
    @ApiModelProperty("中奖总倍率")
    private BigDecimal totalRewardMultiple;
    @ApiModelProperty("滚轮结果")
    private String[][] spinsResult;
    @ApiModelProperty("中奖元素坐标集合")
    private Set<IndexVo> rewardSymbolIndexSet;
    @ApiModelProperty("中奖线列表")
    private List<MariaMakilingRewardLineVo> rewardLineList;
    @ApiModelProperty("免费轮标识,0:否,1:是")
    private Integer freeSpinFlag;
    @ApiModelProperty("免费轮Id列表")
    private List<Integer> freeSpinIds;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel("MariaMakiling中奖线详情")
    @EqualsAndHashCode(callSuper = false)
    public static class MariaMakilingRewardLineVo implements Serializable {
        private static final long serialVersionUID = 6487808344235380369L;
        @ApiModelProperty("中奖线元素Id: 中奖线第一个中奖元素id")
        private String symbolId;
        @ApiModelProperty("倍率")
        private BigDecimal multiple;
        @ApiModelProperty("赔率")
        private BigDecimal odds;
        @ApiModelProperty("中奖线类型：THREE（三连），FOUR（四连）FIVE（5连）")
        private String rewardLineType;
        @ApiModelProperty("中奖线子类型：COMMON（普通），SCATTER（SCATTER）")
        private String rewardLineSubType;
        @ApiModelProperty("中奖线数量")
        private Integer rewardLineCount;
        @ApiModelProperty("中奖金额： COMMON中奖计算公式（投注金额 * 赔率 * (中奖线数量WAYS)*(1 + 倍率) = 中奖金额）； " +
                "SCATTER中奖计算公式（投注金额 * 赔率 * （中奖线数量WAYS）= 中奖金额）")
        private Long rewardAmount;
        @ApiModelProperty("投注金额")
        private Long betAmount;
    }
}
