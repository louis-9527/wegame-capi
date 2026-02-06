package com.wegame.capi.client.domain.vo.promotion;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "兑换码响应模型")
public class RedeemPlayerCodeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "玩家名称")
    private String playerName;
    @ApiModelProperty(value = "渠道id")
    private String channelId;
    @ApiModelProperty(value = "平台id")
    @JsonProperty("pId")
    private String pId;
    @ApiModelProperty(value = "refId")
    private String refId;
    @ApiModelProperty(value = "兑换码")
    private String code;
    @ApiModelProperty(value = "奖励类型: 1: starCard / 2: freeSpin")
    private String rewardType;
    @ApiModelProperty(value = "奖励对象")
    private Object reward;
}
