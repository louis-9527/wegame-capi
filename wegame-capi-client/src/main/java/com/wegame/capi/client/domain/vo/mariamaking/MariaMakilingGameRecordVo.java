package com.wegame.capi.client.domain.vo.mariamaking;

import com.wegame.base.Validate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("MariaMakiling游戏记录")
public class MariaMakilingGameRecordVo implements Validate, Serializable {
    private static final long serialVersionUID = -5835715678741184L;
    @ApiModelProperty("游戏记录id")
    private String id;
    @ApiModelProperty("序号：0为Main，大于0为Free")
    private Integer index;
    @ApiModelProperty("投注金额")
    private Long betAmount;
    @ApiModelProperty("中奖金额")
    private Long rewardAmount;
    @ApiModelProperty("滚轮结果")
    private String[][] wheel;
    @ApiModelProperty("倍率")
    private BigDecimal multiple;
    @ApiModelProperty("累计达到最大中奖倍数：true是，false否")
    private Boolean accumulateReachMaxMultiple;
    @ApiModelProperty("中奖线列表")
    private List<MariaMakilingRewardLineVo> rewardLineList;
}
