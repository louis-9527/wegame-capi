package com.wegame.capi.biz.module.kapre.bet.strategy;

import com.wegame.capi.biz.domain.biz.KapreBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.game.KapreGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.KapreBetRecordManager;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.KapreOperationEventManager;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractCommonBetStrategy;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.biz.module.kapre.bet.KapreBetHelper;
import com.wegame.capi.biz.module.kapre.game.KapreGameContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


/**
 * Kapre游戏投注抽象接口
 */
@Slf4j
public abstract class AbstractKapreBetStrategy
        extends AbstractCommonBetStrategy<KapreGameRecordDo, KapreBetRecordDetailDo>
        implements KapreBetStrategy<KapreBetRecordDetailDo> {

    @Autowired
    @Qualifier("kapreGameContext")
    KapreGameContext<KapreGameRecordDo> kapreGameContext;
    @Autowired
    @Qualifier("kapreBetRecordManager")
    KapreBetRecordManager kapreBetRecordManager;

    @Autowired
    @Qualifier("kapreOperationEventManager")
    private KapreOperationEventManager kapreOperationEventManager;
    @Autowired
    private KapreBetHelper kapreBetHelper;

    @Override
    public AbstractGameContext<KapreGameRecordDo> gameContext() {
        return kapreGameContext;
    }


    @Override
    public AbstractBetRecordManager<KapreGameRecordDo> betRecordManager() {
        return kapreBetRecordManager;
    }


    @Override
    public AbstractOperationEventManager operationEventManager() {
        return kapreOperationEventManager;
    }

    @Override
    public BetHelper<KapreBetRecordDetailDo, KapreGameRecordDo> betHelper() {
        return kapreBetHelper;
    }

    @Override
    public BetResultDo<KapreBetRecordDetailDo> bet(BetDo betDo) {
        throw new RuntimeException("todo");
    }



    public void freeWheelAfterHandle(Long betRecordId) {
        // do nothing
    }

}
