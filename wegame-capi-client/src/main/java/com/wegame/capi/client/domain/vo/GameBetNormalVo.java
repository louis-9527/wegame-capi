package com.wegame.capi.client.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("正常投注返回")
@EqualsAndHashCode(callSuper = false)
public class GameBetNormalVo implements Serializable {
    private static final long serialVersionUID = -4792907227811616042L;

    @ApiModelProperty("描述:大奖类型,-1:未中大奖:0:niec,1:mege,2:super,3:sensati")
    private Integer payOutAmountType;
    @ApiModelProperty("描述:派奖总金额")
    private Long payOutTotalAmount;
    @ApiModelProperty("免费轮标识,0:否,1:是")
    private Integer freeSpinFlag;
    @ApiModelProperty("当前轮盘结果")
    private String[][] spinsResult;
    @ApiModelProperty("中奖结果")
    private List<GameBetPayOutDetailVo> payOutResult;

}
