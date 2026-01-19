package com.wegame.capi.client.domain.vo.promotion;

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
@ApiModel(description = "获取转发游戏url模型")
public class GetRedirectGameUrlVo implements Serializable {

    private static final long serialVersionUID = -8947662892053165172L;
    @ApiModelProperty(value = "url", required = true)
    private String url;
    @ApiModelProperty(value = "playerName", required = true)
    private String playerName;
    @ApiModelProperty(value = "refId", required = true)
    private String refId;
}
