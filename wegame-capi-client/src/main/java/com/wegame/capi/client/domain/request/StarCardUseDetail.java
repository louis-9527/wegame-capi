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
     * Reward config id
     */
    private String rewardConfigId;

    /**
     * Reward id
     */
    private String rewardId;
}
