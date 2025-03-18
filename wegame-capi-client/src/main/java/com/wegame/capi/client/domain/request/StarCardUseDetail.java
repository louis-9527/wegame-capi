package com.wegame.capi.client.domain.request;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "startCard使用明细")
public class StarCardUseDetail {
    /**
     * 奖励配置id
     */
    private String rewardConfigId;

    /**
     * Reward id
     */
    private String rewardId;
}
