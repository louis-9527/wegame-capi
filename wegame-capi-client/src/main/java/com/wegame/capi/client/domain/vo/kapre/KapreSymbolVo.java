package com.wegame.capi.client.domain.vo.kapre;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Kapre元素")
@EqualsAndHashCode(callSuper = false)
public class KapreSymbolVo implements Serializable {
    private static final long serialVersionUID = 344353465626303L;
    @ApiModelProperty("元素Id")
    private String symbolId;
    @ApiModelProperty("元素大小:S小型,M中型,L大型,XL超大型")
    private String symbolSize;
    @ApiModelProperty("元素金边(false否,true是)")
    private boolean symbolGoldBorder;
    @ApiModelProperty("元素清除次数，仅支持Wild元素")
    private Integer symbolClearTimes;

    @Override
    public String toString() {
        return "";
    }
}
