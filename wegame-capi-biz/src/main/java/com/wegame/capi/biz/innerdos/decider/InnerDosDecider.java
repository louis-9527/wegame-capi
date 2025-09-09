package com.wegame.capi.biz.innerdos.decider;

import com.wegame.capi.biz.innerdos.context.InnerDosContext;
import com.wegame.capi.client.enums.GameTypeEnum;

/**
 * 内部干预决策器
 */
public interface InnerDosDecider<CONTEXT extends InnerDosContext > {

    GameTypeEnum gameType();

    /**
     * 决策
     *
     * @param context context
     * @return result
     */
    InnerDosDecideResult decide(CONTEXT context);
}
