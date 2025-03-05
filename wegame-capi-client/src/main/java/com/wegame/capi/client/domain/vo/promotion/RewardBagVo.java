package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "背包VO")
public class RewardBagVo {
    @ApiModelProperty(value = "背包星卡列表", required = true)
    private List<RewardStarCardVo> RewardStarCards;
}
