package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "背包VO")
public class BagCurrentRewordVo {
    @ApiModelProperty(value = "背包列表", required = true)
    private List<BagRewardDetailVo> rewardDetails;
}
