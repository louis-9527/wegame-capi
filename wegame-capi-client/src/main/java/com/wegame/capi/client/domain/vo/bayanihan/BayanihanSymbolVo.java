package com.wegame.capi.client.domain.vo.bayanihan;

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
@ApiModel("bayanihan元素")
@EqualsAndHashCode(callSuper = false)
public class BayanihanSymbolVo implements Serializable {
    private static final long serialVersionUID = 3447556878793626303L;
    @ApiModelProperty("元素Id")
    private String symbolId;

    @ApiModelProperty("是否是新元素")
    private boolean newSymbol;

    @ApiModelProperty("元素清除次数，仅支持Wild元素")
    private Integer symbolClearTimes;

    @Override
    public String toString() {
        return "";
    }
}
