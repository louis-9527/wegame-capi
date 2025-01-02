package com.wegame.capi.client.domain.request.update;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "下注请求")
public class BetBaseRequest<T> {
    @ApiModelProperty(value = "游戏Id", required = true)
    private String gameId;
    @ApiModelProperty(value = "投注类型:1:正常投注, 2:免费投注", required = true)
    private Integer betType;
    @ApiModelProperty(value = "投注子类型:11:正常投注, 21:购买免费轮投注, 22:非购买免费轮投注", required = true)
    private Integer betSubType;
    @ApiModelProperty(value = "投注明细,根据投注类型不同,明细不同", required = true)
    private T betDetail;
}
