package com.wegame.capi.client.domain.request.promotion;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "获取背包列表请求(当前游戏)")
public class QueryBagCurrentGameRewardRequest implements Validate, Serializable {
    private static final long serialVersionUID = -4002950733224509502L;

    @ApiModelProperty(value = "游戏Id", required = true)
    private String gameId;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(gameId, ErrorCode.ERR_PARAM);
    }
}
