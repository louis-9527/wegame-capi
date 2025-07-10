package com.wegame.capi.client.domain.vo.malakas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Lamang投注结果明细")
@EqualsAndHashCode(callSuper = false)
public class MalakasBetResultDetailVo implements Serializable {
    private static final long serialVersionUID = -4792907227811616042L;
    @ApiModelProperty("整轮中奖子类型: none:未中奖,other,nice,mega,super,sensational")
    private String allWheelRewardSubType;
    @ApiModelProperty("投注结果列表")
    private List<MalakasBetResultVo> resultList;
}
