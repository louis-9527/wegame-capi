package com.wegame.capi.client.domain.request.update;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Deprecated
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "正常下注请求明细")
public class BetNormalDetailRequest {
    @ApiModelProperty(value = "下注金额", required = true)
    private Long betAmount;
}
