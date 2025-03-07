package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "宝箱列表VO")
public class TreasureListVo implements Serializable {
    private static final long serialVersionUID = -4339383416464490170L;
    @ApiModelProperty(value = "宝箱ID", required = true)
    private String treasureId;
    @ApiModelProperty(value = "宝箱名称", required = true)
    private String name;
    @ApiModelProperty(value = "当前进度", required = true)
    private BigDecimal currentExperience;
    @ApiModelProperty(value = "目标进度", required = true)
    private BigDecimal targetExperience;
    @ApiModelProperty(value = "状态:1 - in progress , 2 - redeemable , 3 - redeemed", required = true)
    private Integer status;
    @ApiModelProperty(value = "宝箱等级", required = true)
    private Integer vipLevel;
    @ApiModelProperty(value = "宝箱奖励列表", required = true)
    private List<TreasureRewardVo> rewards;
}
