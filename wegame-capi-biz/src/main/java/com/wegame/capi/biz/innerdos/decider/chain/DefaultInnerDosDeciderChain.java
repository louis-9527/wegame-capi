package com.wegame.capi.biz.innerdos.decider.chain;

import com.google.common.collect.Lists;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.innerdos.context.InnerDosContext;
import com.wegame.capi.biz.innerdos.decider.*;
import com.wegame.capi.biz.innerdos.initializer.InnerDosContextInitializerManager;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 默认干预决策器链
 */
@Slf4j
@Component("defaultInnerDosDeciderChain")
public class DefaultInnerDosDeciderChain extends AbstractInnerDosDeciderChain {
    @Autowired
    @Qualifier("defaultByEmptyDiskInnerDosDecider")
    private ByEmptyDiskInnerDosDecider byEmptyDiskInnerDosDecider;
    @Autowired
    @Qualifier("defaultByRoundInnerDosDecider")
    private ByRoundInnerDosDecider byRoundInnerDosDecider;
    @Autowired
    @Qualifier("defaultByRtpInnerDosDecider")
    private ByRtpInnerDosDecider byRtpInnerDosDecider;
    @Autowired
    private InnerDosContextInitializerManager innerDosContextInitializerManager;

    @Override
    public GameTypeEnum gameType() {
        // 默认链返回null
        return null;
    }

    @Override
    public List<InnerDosDecider> deciders(BetDo betDo) {
        return Lists.newArrayList(byRoundInnerDosDecider, byRtpInnerDosDecider, byEmptyDiskInnerDosDecider);
    }

    @Override
    protected InnerDosContext initContext(BetDo betDo) {
        return this.innerDosContextInitializerManager.getInitializer(betDo.getGameType()).init(betDo);
    }
}
