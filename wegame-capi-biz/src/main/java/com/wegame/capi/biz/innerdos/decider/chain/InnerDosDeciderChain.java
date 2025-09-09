package com.wegame.capi.biz.innerdos.decider.chain;

import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.innerdos.decider.InnerDosDecideResult;
import com.wegame.capi.client.enums.GameTypeEnum;

/**
 * 内部干预决策器链
 * 所有干预由决策器链发起
 */
@Deprecated
public interface InnerDosDeciderChain {

    GameTypeEnum gameType();

    /**
     * 决策
     *
     * @return result
     */
    InnerDosDecideResult decide(BetDo betDo);
}
