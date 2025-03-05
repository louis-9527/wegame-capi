package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "宝箱列表")
public class TreasureVo implements  Serializable {

    private static final long serialVersionUID = -1704392640858105682L;
    @ApiModelProperty(value = "宝箱ID", required = true)
    private String id;
    @ApiModelProperty(value = "宝箱名称", required = true)
    private String name;
    @ApiModelProperty(value = "当前进度", required = true)
    private BigDecimal currentExperience;
    @ApiModelProperty(value = "目标进度", required = true)
    private BigDecimal targetExperience;
    @ApiModelProperty(value = "状态:1 - in progress , 2 - redeemable , 3 - redeemed", required = true)
    private Integer status;

}
