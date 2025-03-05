package com.wegame.capi.client.domain.request.promotion;

import com.wegame.base.Validate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "领取成就请求")
public class RedeemAchievementRequest implements Validate, Serializable {
    private static final long serialVersionUID = -7035134323452916163L;
    @ApiModelProperty(value = "成就id", required = true)
    private String achievementId;
}
