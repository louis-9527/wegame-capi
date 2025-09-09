package com.wegame.capi.biz.innerdos.decider.chain;

import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.innerdos.context.InnerDosContext;
import com.wegame.capi.biz.innerdos.decider.InnerDosDecideResult;
import com.wegame.capi.biz.innerdos.decider.InnerDosDecider;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 抽象干预决策器链
 */
@Slf4j
@Deprecated
public abstract class AbstractInnerDosDeciderChain implements InnerDosDeciderChain {

    @Override
    public InnerDosDecideResult decide(BetDo betDo) {
        // 获取context
        InnerDosContext context = this.initContext(betDo);
        // 决策初始化
        InnerDosDecideResult result = null;
        for (InnerDosDecider decider : this.deciders(betDo)) {
            // 调用决策器
            result = decider.decide(context);
//            if (result.hasEnd()) {
//                return result;
//            }
        }
        return result;
    }

    /**
     * 初始化决策器链
     *
     * @param betDo betDo
     * @return list
     */
    protected abstract List<InnerDosDecider> deciders(BetDo betDo);

    /**
     * 初始化当前链的上下文
     *
     * @param betDo betDo
     * @return context
     */
    protected abstract InnerDosContext initContext(BetDo betDo);
}
