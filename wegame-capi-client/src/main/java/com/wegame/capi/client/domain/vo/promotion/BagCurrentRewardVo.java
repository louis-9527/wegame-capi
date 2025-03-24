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
@ApiModel(description = "背包")
public class BagCurrentRewardVo implements Serializable {
    private static final long serialVersionUID = -6853372222077178716L;
    @ApiModelProperty(value = "背包列表", required = true)
    private List<BagRewardDetailVo> rewardDetails;
}
