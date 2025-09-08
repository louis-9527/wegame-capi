package com.wegame.capi.biz.innerdos.decider;

import com.wegame.capi.biz.innerdos.context.InnerDosContext;

/**
 * 内部干预决策器
 */
public interface InnerDosDecider {

    /**
     * 决策
     *
     * @param context context
     * @return result
     */
    InnerDosDecideResult decide(InnerDosContext context);
}
