package com.wegame.capi.client.domain.request.query;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "获取免费轮投注结果")
public class QueryFreeSpinRequest implements Serializable {
    private static final long serialVersionUID = 3042686983373999256L;
    @ApiModelProperty(value = "游戏Id", required = true)
    private String gameId;
    @ApiModelProperty(value = "投注类型:NORMAL_BET,免费轮投注:FREE_SPINS_BET", required = true)
    private String betType;
    @ApiModelProperty(value = "投注子类型:NORMAL_BET:正常投注, BUY_FREE_SPINS_BET:购买免费轮投注, NON_BUY_FREE_SPINS_BET:非购买免费轮投注", required = true)
    private String betSubType;
    @ApiModelProperty(value = "投注记录ID", required = true)
    private String gameRecordId;
    @ApiModelProperty(value = "免费轮ID", required = true)
    private String freeSpinId;
}
