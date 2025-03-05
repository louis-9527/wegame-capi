package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "背包游戏汇总列表")
public class BagRewardGameGroupVo implements  Serializable {
    private static final long serialVersionUID = -1704392640858105682L;
    @ApiModelProperty(value = "跳转时游戏ID", required = true)
    private String skipGameId;
    @ApiModelProperty(value = "系统游戏ID", required = true)
    private String gameId;
    @ApiModelProperty(value = "游戏名称", required = true)
    private String gameName;
    @ApiModelProperty(value = "可用数量", required = true)
    private Integer own;
    @ApiModelProperty(value = "icon", required = true)
    private String imgUrl;
}
