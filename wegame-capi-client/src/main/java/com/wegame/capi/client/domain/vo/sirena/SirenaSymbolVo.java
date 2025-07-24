package com.wegame.capi.client.domain.vo.sirena;

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
@ApiModel("Sirena元素")
@EqualsAndHashCode(callSuper = false)
public class SirenaSymbolVo implements Serializable {
    private static final long serialVersionUID = 344353465626303L;
    @ApiModelProperty("元素Id")
    private String symbolId;
    @ApiModelProperty("元素赔率,现金symbol可使用")
    private BigDecimal symbolOdd;
    @ApiModelProperty("嵌套symbol")
    private SirenaSymbolVo nestSymbol;

    @Override
    public String toString() {
        return "";
    }
}
