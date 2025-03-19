package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "其他游戏背包")
public class BagOtherRewordVo implements Serializable {
    @ApiModelProperty(value = "背包列表", required = true)
    private List<BagOtherRewardDetailVo> rewardDetails;
}
