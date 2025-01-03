package com.wegame.capi.biz.module.bet.strategy;


import com.wegame.capi.biz.domain.BetDo;
import com.wegame.capi.biz.domain.BetResultDo;
import com.wegame.capi.biz.domain.GameBetResultDetailDo;

/**
 * 投注策略
 */
public interface BetStrategy<T, R> {


    BetResultDo<R> bet(BetDo<T> betDo);

    GameBetResultDetailDo queryBetFreeSpin(String gameId, Long recordId, Integer freeSpinId);

}
