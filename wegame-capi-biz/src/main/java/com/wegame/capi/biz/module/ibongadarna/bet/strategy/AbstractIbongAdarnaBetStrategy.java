package com.wegame.capi.biz.module.ibongadarna.bet.strategy;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.IbongAdarnaGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.IbongAdarnaBetRecordManager;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.IbongAdarnaOperationEventManager;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractCommonBetStrategy;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.biz.module.ibongadarna.bet.IbongAdarnaBetHelper;
import com.wegame.capi.biz.module.ibongadarna.game.IbongAdarnaGameContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Objects;

import static com.wegame.capi.biz.builder.GamePlayBuilder.build;

/**
 * IbongAdarna类游戏投注抽象接口
 */
@Slf4j
public abstract class AbstractIbongAdarnaBetStrategy
        extends AbstractCommonBetStrategy<IbongAdarnaGameRecordDo, IbongAdarnaGameRecordDo>
        implements IbongAdarnaBetStrategy<IbongAdarnaGameRecordDo> {

    @Autowired
    IbongAdarnaGameContext ibongAdarnaGameContext;
    @Autowired
    IbongAdarnaBetRecordManager ibongAdarnaBetRecordManager;
    @Autowired
    IbongAdarnaBetHelper ibongAdarnaBetHelper;
    @Autowired
    @Qualifier("ibongAdarnaOperationEventManager")
    IbongAdarnaOperationEventManager ibongAdarnaOperationEventManager;
    @Autowired
    IbongAdarnaConfig ibongAdarnaConfig;

    @Override
    public AbstractGameContext<IbongAdarnaGameRecordDo> gameContext() {
        return ibongAdarnaGameContext;
    }

    @Override
    public AbstractBetRecordManager<IbongAdarnaGameRecordDo> betRecordManager() {
        return ibongAdarnaBetRecordManager;
    }

    @Override
    public AbstractOperationEventManager operationEventManager() {
        return ibongAdarnaOperationEventManager;
    }

    @Override
    public BetHelper<IbongAdarnaGameRecordDo, IbongAdarnaGameRecordDo> betHelper() {
        return ibongAdarnaBetHelper;
    }


    @Override
    public BetResultDo<IbongAdarnaGameRecordDo> bet(BetDo betDo) {
        //// 1、是否使用星卡
        if (isCanFreeSpinBet(betDo)) {
            // 1.1 是，则预定投注流程
            return betForFreeSpin(betDo);
        } else {
            // 1.2 否，则走其他投注流程
            // 1.2.1、判断能否走干预事件投注
            if (isCanOperationEventBet(betDo)) {
                // 1.2.11 命中干预事件：走干预事件投注
                return betForOperationEvent(betDo);
            } else {
                // 1.2.12 未命中干预事件：走正常投注。
                return normalBet(betDo);
            }
        }
    }

    private BetResultDo<IbongAdarnaGameRecordDo> normalBet(BetDo betDo) {
        long betRecordId = generateBetRecordId();
        List<IbongAdarnaGameRecordDo> list = super.play(betDo, betRecordId, this.ibongAdarnaConfig.getPayoutAmountLimitConfig(), null);
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        IbongAdarnaGameRecordDo gameRecord = list.get(0);
        // 构建投注记录
        BetRecordDo<IbongAdarnaGameRecordDo> betRecord = ibongAdarnaBetHelper.buildBetRecord(betRecordId, betDo, list);
        // 保存投注记录并派奖
        SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betDo, betRecord, gameRecord.hasRewardFreeSpins());
        BetResultDo<IbongAdarnaGameRecordDo> result = ibongAdarnaBetHelper.handleBetResult(betRecord, resultDo);
        // 投注成功后发送延迟派奖通知
        sendDelayRewardNotifyAfterBet(resultDo, betRecord);
        return result;
    }

    @Override
    protected long getTotalRewardAmount(List<IbongAdarnaGameRecordDo> gameRecordList, Object extParam) {
        if (CollectionUtil.isEmpty(gameRecordList)) {
            return 0L;
        } else {
            return gameRecordList.stream()
                    .filter(item -> Objects.nonNull(item.getRewardResult()))
                    .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                    .sum();
        }
    }
}
