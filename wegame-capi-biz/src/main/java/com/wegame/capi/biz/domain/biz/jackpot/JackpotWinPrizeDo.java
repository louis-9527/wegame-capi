package com.wegame.capi.biz.domain.biz.jackpot;

import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import lombok.Builder;
import lombok.Data;


/**
 * 中奖DO
 */
@Data
@Builder
public class JackpotWinPrizeDo {
    /**
     * 游戏ID
     */
    private Long betGameId;

    /**
     * 中奖金额
     */
    private Long amount;

    /**
     * 奖池类型
     */
    private JackpotRewardPoolTypeEnum jackpotRewardPoolType;

}
