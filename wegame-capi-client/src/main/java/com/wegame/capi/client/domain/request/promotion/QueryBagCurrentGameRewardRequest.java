package com.wegame.capi.client.domain.request.promotion;

import com.wegame.base.Validate;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "获取背包列表请求(当前游戏)")
public class QueryBagCurrentGameRewardRequest implements Validate, Serializable {
    private static final long serialVersionUID = -4002950733224509502L;
}
