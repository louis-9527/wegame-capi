package com.wegame.capi.client.domain.vo.malakas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Malakas元素")
@EqualsAndHashCode(callSuper = false)
public class MalakasSymbolVo implements Serializable {
    private static final long serialVersionUID = 3447556878793626303L;
    @ApiModelProperty("元素Id")
    private String symbolId;
    @ApiModelProperty("元素大小:S小型,M中型,L大型,XL超大型")
    private String symbolSize;
    @ApiModelProperty("元素金边(false否,true是)")
    private boolean symbolGoldBorder;
    @ApiModelProperty("元素清除次数，仅支持Wild元素")
    private Integer symbolClearTimes;
}
