package com.wegame.capi.client.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Malakas投注结果明细")
@EqualsAndHashCode(callSuper = false)
public class MalakasBetResultDetailVo implements Serializable {
    private static final long serialVersionUID = -4792907227811616042L;
    @ApiModelProperty("投注结果列表")
    private List<MalakasBetResultVo> resultList;
}
