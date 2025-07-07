package com.wegame.capi.client.domain.vo.mangkukulam;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Mangkukulam元素")
@EqualsAndHashCode(callSuper = false)
public class MangkukulamSymbolVo implements Serializable {
    private static final long serialVersionUID = 3447556878793626303L;
    @ApiModelProperty("元素Id")
    private String symbolId;
    @ApiModelProperty("元素火边(false否,true是)")
    private boolean symbolFireBorder;

    @ApiModelProperty("清除次数 只有特殊百搭有")
    private Integer remainClearTimes;

    @ApiModelProperty("倍率列表 只有特殊百搭有")
    private List<BigDecimal> multiples;

    @Override
    public String toString() {
        return "";
    }
}
