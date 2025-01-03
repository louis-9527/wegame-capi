package com.wegame.capi.biz.module.bet.strategy;


import com.wegame.base.Validate;
import com.wegame.capi.biz.converter.Converter;
import com.wegame.capi.biz.converter.Converter;
import com.wegame.capi.biz.domain.biz.GameBetResultDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;

/**
 * 投注策略
 */
public interface BetStrategy<T, R extends Converter> {


    BetResultDo<R> bet(BetDo<T> betDo);

    GameBetResultDetailDo queryBetFreeSpin(String gameId, Long recordId, Integer freeSpinId);

}
