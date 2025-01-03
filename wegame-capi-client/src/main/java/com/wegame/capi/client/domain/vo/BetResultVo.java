package com.wegame.capi.client.domain.vo;

import com.wegame.base.Validate;
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
    @ApiModelProperty("游戏投注Id")
    private String gameRecordId;
    @ApiModelProperty("游戏ID")
    private String gameId;
    @ApiModelProperty(value = "投注类型:NORMAL_BET,免费轮投注:FREE_SPINS_BET", required = true)
    private String betType;
    @ApiModelProperty(value = "投注子类型:NORMAL_BET:正常投注, BUY_FREE_SPINS_BET:购买免费轮投注, NON_BUY_FREE_SPINS_BET:非购买免费轮投注", required = true)
    private String betSubType;
    @ApiModelProperty("玩家余额")
    private Long credit;
    @ApiModelProperty("结果明细")
    private Object resultDetail;


    @Data
    @NoArgsConstructor
//    @AllArgsConstructor
    @ApiModel(description = "Mysterious正常投注结果")
    public class MysteriousNormalBetResultVo implements Validate, Serializable {
        // todo
    }

    @Data
    @NoArgsConstructor
//    @AllArgsConstructor
    @ApiModel(description = "Mysterious非购买免费轮投注结果")
    public class MysteriousNonBuyFreeSpinsBetVo implements Validate, Serializable {
        // todo
    }

    @Data
    @NoArgsConstructor
//    @AllArgsConstructor
    @ApiModel(description = "Mysterious购买免费轮投注请求参数")
    public class MysteriousBuyFreeSpinsBetResultVo implements Validate, Serializable {
        // todo
    }
}
