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
public class MariaMakilingBetResultVo implements Serializable {
    private static final long serialVersionUID = -4792907227811616042L;
    @ApiModelProperty("中奖类型:SINGLE单奖励，MULTIPLE多奖励 ")
    private String rewardType;
    @ApiModelProperty("中奖子类型,-1:未中大奖:0:niec,1:mege,2:super,3:sensati")
    private Integer rewardSubType;
    @ApiModelProperty("中奖总金额")
    private Long totalRewardAmount;
    @ApiModelProperty("总中奖倍率")
    private BigDecimal totalRewardMultiple;
    @ApiModelProperty("滚轮结果")
    private String[][] spinsResult;
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
    @ApiModel("中奖详情")
    @EqualsAndHashCode(callSuper = false)
    public static class MariaMakilingRewardLineVo implements Serializable {
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
}
