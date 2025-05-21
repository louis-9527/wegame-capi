package com.wegame.capi.client.domain.vo.bakunawa;

import com.wegame.base.Validate;
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
@ApiModel("Bakunawa游戏记录")
@EqualsAndHashCode(callSuper = false)
public class BakunawaGameRecordVo implements Validate, Serializable {
    private static final long serialVersionUID = -58357876741184L;
    @ApiModelProperty("游戏记录id")
    private String id;
    @ApiModelProperty("序号：0为Main，大于0为Free")
    private Integer index;
    @ApiModelProperty("投注金额")
    private Long betAmount;
    @ApiModelProperty("中奖金额")
    private Long rewardAmount;
    @ApiModelProperty("滚轮结果")
    private BakunawaSymbolVo[][] wheel;
    @ApiModelProperty("倍率")
    private BigDecimal multiple;
    @ApiModelProperty("累计中奖总金额")
    private Long overallRewardAmount;
    @ApiModelProperty("达到触发最大中奖倍数")
    private Boolean accumulateReachMaxMultiple;
    @ApiModelProperty("中奖线列表")
    private List<BakunawaRewardLineVo> rewardLineList; // todo
}
