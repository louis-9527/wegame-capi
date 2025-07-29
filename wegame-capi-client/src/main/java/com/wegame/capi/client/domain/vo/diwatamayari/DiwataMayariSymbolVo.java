package com.wegame.capi.client.domain.vo.diwatamayari;

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
@ApiModel("DiwataMayari元素")
@EqualsAndHashCode(callSuper = false)
public class DiwataMayariSymbolVo implements Serializable {
    private static final long serialVersionUID = 349924343626303L;
    @ApiModelProperty("元素Id")
    private String symbolId;
    @ApiModelProperty("主元素")
    private boolean main;

    @Override
    public String toString() {
        return "";
    }
}
