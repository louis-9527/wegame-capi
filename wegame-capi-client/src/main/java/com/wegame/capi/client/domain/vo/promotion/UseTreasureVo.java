package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "使用宝箱返回")
public class UseTreasureVo implements Serializable {
    private static final long serialVersionUID = 2423962589779617794L;
    @ApiModelProperty(value = "宝箱ID", required = true)
    private String treasureId;
    @ApiModelProperty(value = "宝箱奖励列表", required = true)
    private List<TreasureRewardVo> reward;
}
