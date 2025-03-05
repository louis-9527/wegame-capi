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
@ApiModel(description = "背包星卡列表VO")
public class RewardStarCardVo implements  Serializable {
    private static final long serialVersionUID = -1704392640858105682L;
    @ApiModelProperty(value = "星卡ID", required = true)
    private String id;
    @ApiModelProperty(value = "星卡名称", required = true)
    private String name;
    @ApiModelProperty(value = "星卡图标URL", required = true)
    private String url;
}
