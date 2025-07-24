package com.wegame.capi.client.domain.vo.sirena;

import com.wegame.base.Validate;
import com.wegame.capi.client.domain.vo.kapre.KapreRewardLineVo;
import com.wegame.capi.client.domain.vo.kapre.KapreSymbolVo;
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
@ApiModel("Malakas游戏记录")
@EqualsAndHashCode(callSuper = false)
public class SirenaGameRecordVo implements Validate, Serializable {
    private static final long serialVersionUID = -5833456741184L;
    @ApiModelProperty("游戏记录id")
    private String id;
    @ApiModelProperty("序号：0为初始轮，大于0为消除轮")
    private Integer index;
    @ApiModelProperty("投注金额")
    private Long betAmount;
    @ApiModelProperty("中奖金额")
    private Long rewardAmount;
    @ApiModelProperty("滚轮结果")
    private SirenaSymbolVo[][] wheel;
    @ApiModelProperty("倍率")
    private BigDecimal multiple;
    @ApiModelProperty("中奖线列表")
    private List<SirenaRewardLineVo> rewardLineList;
    @ApiModelProperty("免费轮标识 false否 true是")
    private Boolean freeWheelFlag;
}