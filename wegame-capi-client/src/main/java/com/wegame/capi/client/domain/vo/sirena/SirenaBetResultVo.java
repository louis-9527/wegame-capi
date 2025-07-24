package com.wegame.capi.client.domain.vo.sirena;

import com.wegame.capi.client.domain.vo.IndexVo;
import com.wegame.capi.client.domain.vo.kapre.KapreRewardLineVo;
import com.wegame.capi.client.domain.vo.kapre.KapreSymbolVo;
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
@ApiModel("Kapre投注结果")
@EqualsAndHashCode(callSuper = false)
public class SirenaBetResultVo implements Serializable {
    private static final long serialVersionUID = -475678911616042L;
    @ApiModelProperty("中奖类型: NONE未中奖，SINGLE单奖励，MULTIPLE多奖励 ")
    private String rewardType;
    @ApiModelProperty("中奖子类型: NONE:未中奖,other,nice,mega,super,sensational")
    private String rewardSubType;
    @ApiModelProperty("中奖总金额")
    private Long totalRewardAmount;
    @ApiModelProperty("中奖总倍率")
    private BigDecimal totalRewardMultiple;
    @ApiModelProperty("滚轮")
    private SirenaSymbolVo[][] wheel;
    @ApiModelProperty("中奖元素坐标集合")
    private Set<IndexVo> rewardSymbolIndexSet;
    @ApiModelProperty("中奖线列表")
    private List<SirenaRewardLineVo> rewardLineList;
    @ApiModelProperty("免费轮标识,0:否,1:是")
    private Integer freeWheelFlag;
    @ApiModelProperty("免费轮游戏记录Id列表")
    private List<String> gameRecordIds;
    @ApiModelProperty("累计达到最大中奖倍数：true是，false否")
    private Boolean accumulateReachMaxMultiple;
}
