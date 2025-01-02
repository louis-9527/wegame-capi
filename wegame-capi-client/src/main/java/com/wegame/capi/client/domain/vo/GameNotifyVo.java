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
@ApiModel("投注记录")
@EqualsAndHashCode(callSuper = false)
public class GameNotifyVo implements Serializable {
    @ApiModelProperty("通知消息")
    private List<String> messages;
    @ApiModelProperty("余额是否变动 0否 1是")
    private int creditChange;
}
