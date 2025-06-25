package com.wegame.capi.client.domain.vo.kapre;

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
@ApiModel("Kapre投注结果明细")
@EqualsAndHashCode(callSuper = false)
public class KapreBetResultDetailVo implements Serializable {
    private static final long serialVersionUID = -4798764567842L;
    @ApiModelProperty("整轮中奖子类型: none:未中奖,other,nice,mega,super,sensational")
    private String allWheelRewardSubType;
    @ApiModelProperty("投注结果列表")
    private List<KapreBetResultVo> resultList;
}
