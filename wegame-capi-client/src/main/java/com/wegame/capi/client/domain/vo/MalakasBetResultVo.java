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
@ApiModel("Malakas投注结果")
@EqualsAndHashCode(callSuper = false)
public class MalakasBetResultVo implements Serializable {
    private static final long serialVersionUID = -4792907227811616042L;
    @ApiModelProperty("中奖类型: NONE未中奖，SINGLE单奖励，MULTIPLE多奖励 ")
    private String rewardType;
    @ApiModelProperty("中奖子类型: NONE:未中奖,other,nice,mega,super,sensational")
    private String rewardSubType;
    @ApiModelProperty("中奖总金额")
    private Long totalRewardAmount;
    @ApiModelProperty("中奖总倍率")
    private BigDecimal totalRewardMultiple;
    @ApiModelProperty("滚轮结果(按列)")
    private List<SpinsResultVo> spinsColResult;
    @ApiModelProperty("滚轮结果第一行")
    private List<SpinsResultVo> spinsRowOneResult;
    @ApiModelProperty("中奖线列表,外层List代表消除次数,内层List代表中奖线列表")
    private List<List<MalakasRewardLineVo>> rewardLineList;
    @ApiModelProperty("免费轮标识,0:否,1:是")
    private Integer freeSpinFlag;
    @ApiModelProperty("免费轮Id列表,免费轮中触发免费轮用_分割")
    private List<String> freeSpinIds;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel("MariaMakiling中奖线详情")
    @EqualsAndHashCode(callSuper = false)
    public static class MalakasRewardLineVo implements Serializable {
        private static final long serialVersionUID = 6487808344235380369L;
        @ApiModelProperty("中奖线元素Id: 中奖线第一个中奖元素id")
        private String symbolId;
        @ApiModelProperty("倍率")
        private BigDecimal multiple;
        @ApiModelProperty("赔率")
        private BigDecimal odds;
        @ApiModelProperty("中奖线类型：THREE（三连），FOUR（四连）FIVE（5连）SIX(6连)")
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

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel("MariaMakiling中奖线详情")
    @EqualsAndHashCode(callSuper = false)
    public static class SpinsResultVo implements Serializable{
        private static final long serialVersionUID = 3447556878793626303L;
        @ApiModelProperty("元素Id")
        private String symbolId;
        @ApiModelProperty("元素大小:1:小型,2:中型,3:大型,4:超大型")
        private Integer size;
        @ApiModelProperty("是否金边(0:否,1:是)")
        private Integer border;
        @ApiModelProperty("消除论数(0:不消除,1:在第一次消除,2:第二次消除...)")
        private Integer cleanSize;
    }
}
