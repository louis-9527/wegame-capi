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
@EqualsAndHashCode(callSuper = false)
@ApiModel("中奖信息")
public class RewardInfoVo implements Serializable {
    private static final long serialVersionUID = -368238304369095L;
    @ApiModelProperty("中奖信息列表")
    private List<String> rewardMessageList;
    @ApiModelProperty("当前用户余额是否变动 0否 1是")
    private int userCreditChange;
}
