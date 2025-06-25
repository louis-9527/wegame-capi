package com.wegame.capi.biz.config.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class KapreConfig {
    /**
     * 中奖最大倍数
     */
    private Integer rewardMaxMultiple;

    /**
     * 免费次数
     */
    private Integer freeWheelTimes;

    public void validate() {

    }
}
