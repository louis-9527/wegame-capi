package com.wegame.capi.client.domain.vo.bayanihan;

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

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Bayanihan投注结果明细")
@EqualsAndHashCode(callSuper = false)
public class BayanihanBetResultDetailVo implements Serializable {
    private static final long serialVersionUID = -49874237842L;
    @ApiModelProperty("整轮中奖子类型: none:未中奖,other,nice,mega,super,sensational")
    private String allWheelRewardSubType;
    @ApiModelProperty("投注结果列表")
    private List<BayanihanBetResultVo> resultList;
    @ApiModelProperty("免费轮信息")
    private FreeGameInfoVo freeGameInfo;

    @Data
    @SuperBuilder
    public static class FreeGameInfoVo{
        private BigDecimal initialMultiple;
        private BigDecimal finalMultiple;
        private int freeWheelCount;
    }
}
