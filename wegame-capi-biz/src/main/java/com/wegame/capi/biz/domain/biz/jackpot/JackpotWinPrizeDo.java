package com.wegame.capi.biz.domain.biz.jackpot;

import lombok.Data;


/**
 * 中奖DO
 */
@Data
public class JackpotWinPrizeDo {
    /**
     * 游戏ID
     */
    private Long betGameId;


    /**
     * 中奖金额
     */
    private Long amount;

}
