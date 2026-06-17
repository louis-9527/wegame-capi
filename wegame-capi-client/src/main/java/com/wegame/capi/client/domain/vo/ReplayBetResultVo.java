package com.wegame.capi.client.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Map;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("回放投注结果")
public class ReplayBetResultVo implements Serializable {
    private static final long serialVersionUID = -36826024304369095L;
    @ApiModelProperty("第一批次id")
    private String firstBatchId;
    @ApiModelProperty("投注结果map：批次id -> 投注结果")
    private Map<String, BetResultVo> betResultMap;
}
