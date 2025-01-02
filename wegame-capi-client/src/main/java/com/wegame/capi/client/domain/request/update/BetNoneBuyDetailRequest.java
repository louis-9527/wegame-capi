package com.wegame.capi.client.domain.request.update;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "非购买免费轮")
public class BetNoneBuyDetailRequest {
    @ApiModelProperty(value = "游戏投注记录", required = true)
    private String gameRecordId;
}
