package com.wegame.capi.client.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("投注结果")
@EqualsAndHashCode(callSuper = false)
public class BetResultVo implements Serializable {
    private static final long serialVersionUID = -3682602438304369095L;
    @ApiModelProperty("投注记录Id")
    private String betRecordId;
    @ApiModelProperty("游戏ID")
    private String gameId;
    @ApiModelProperty(value = "投注类型:NORMAL_BET,免费轮投注:FREE_SPINS_BET", required = true)
    private String betType;
    @ApiModelProperty(value = "投注子类型:NORMAL_BET:正常投注, BUY_FREE_SPINS_BET:购买免费轮投注, NON_BUY_FREE_SPINS_BET:非购买免费轮投注", required = true)
    private String betSubType;
    @ApiModelProperty("用户余额")
    private Long credit;
    @ApiModelProperty("合计中奖总金额")
    private Long overallRewardAmount;
    @ApiModelProperty("总计派奖金额")
    private Long totalPayoutAmount;
    @ApiModelProperty("结果明细")
    private Object resultDetail;
}
