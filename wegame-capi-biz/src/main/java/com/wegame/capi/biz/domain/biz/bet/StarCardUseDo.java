package com.wegame.capi.biz.domain.biz.bet;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class StarCardUseDo {
    /**
     * 奖励配置id
     */
    private String rewardConfigId;

    /**
     * Reward id
     */
    private String rewardId;

}
