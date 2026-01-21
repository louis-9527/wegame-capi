package com.wegame.capi.client.domain.request.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@lombok.Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@ApiModel(description = "转发模块请求模型 data")
public class GreRedirectUrlDataRequest {
    @ApiModelProperty(value = "游戏 id", required = true)
    private String gameId;
}
