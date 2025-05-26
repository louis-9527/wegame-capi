package com.wegame.capi.biz.domain.biz.jackpot;

import lombok.Data;

@Data
public class JackpotWinPrizeReqDo {
    /**
     * 投注游戏id
     */
    private Long betGameId;

    /**
     * 计算次数
     */
    private Integer count;
}
