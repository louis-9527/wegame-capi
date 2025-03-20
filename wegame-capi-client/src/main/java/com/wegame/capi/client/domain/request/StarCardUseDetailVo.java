package com.wegame.capi.client.domain.request;

import com.wegame.base.Validate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.repository.cdi.Eager;

import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "星卡使用明细")
public class StarCardUseDetailVo implements Validate, Serializable {
    @ApiModelProperty(value = "奖励配置id", required = true)
    private String rewardConfigId;
    @ApiModelProperty(value = "奖励id", required = true)
    private String rewardId;
}
