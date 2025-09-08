package com.wegame.capi.biz.innerdos.decider;


import com.wegame.capi.biz.innerdos.context.InnerDosContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 默认内部干预决策器（按局）
 */
@Slf4j
@Component("defaultByRoundInnerDosDecider")
public class DefaultByRoundInnerDosDecider extends ByRoundInnerDosDecider {

    @Override
    public InnerDosDecideResult decide(InnerDosContext context) {
        return null;
    }
}
