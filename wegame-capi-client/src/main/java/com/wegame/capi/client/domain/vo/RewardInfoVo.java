package com.wegame.capi.client.domain.vo;

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
@ApiModel("中奖信息")
@EqualsAndHashCode(callSuper = false)
public class RewardInfoVo implements Serializable {

    @ApiModelProperty("中奖信息列表")
    private List<String> messages;

    @ApiModelProperty("当前用户余额是否变动 0否 1是")
    private int creditChange;
}
