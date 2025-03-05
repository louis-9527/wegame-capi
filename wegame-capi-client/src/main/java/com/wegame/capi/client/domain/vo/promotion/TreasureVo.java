package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "宝箱列表")
public class TreasureVo implements  Serializable {

    private static final long serialVersionUID = -1704392640858105682L;
    @ApiModelProperty(value = "宝箱列表", required = true)
    private List<TreasureListVo> treasureList;

}
