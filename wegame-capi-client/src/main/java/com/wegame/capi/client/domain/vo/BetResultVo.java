package com.wegame.capi.client.domain.vo;

import com.wegame.capi.client.domain.vo.diwatamayari.DiwataMayariBetResultVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("投注结果")
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
    // todo 测试完成之后修改成object类型
    @ApiModelProperty("结果明细")
    private Object resultDetail;
}
