package com.wegame.capi.biz.innerdos.decider;


import com.wegame.capi.biz.innerdos.context.InnerDosContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 内部干预决策器（按rtp）
 */
@Slf4j
@Component("defaultByRtpInnerDosDecider")
public class DefaultByRtpInnerDosDecider extends ByRtpInnerDosDecider {

    @Override
    public InnerDosDecideResult decide(InnerDosContext context) {
        return null;
    }
}
