package com.wegame.capi.client.domain.request.promotion;

import com.wegame.base.Validate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "获取背包列表请求")
public class QueryBagRewardRequest implements Validate, Serializable {
    private static final long serialVersionUID = -4002950733224509502L;
    @ApiModelProperty(value = "游戏Id", required = true)
    private String gameId;
    @ApiModelProperty(value = "(1: Current Game or 2: Other)", required = true)
    private String type;

}
