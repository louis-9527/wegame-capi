package com.wegame.capi.client.domain.vo.kapre;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

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
    @ApiModelProperty("元素赔率,现金symbol可使用")
    private BigDecimal symbolOdd;
    @ApiModelProperty("嵌套symbol")
    private KapreSymbolVo nestSymbol;

    @Override
    public String toString() {
        return "";
    }
}
