package com.wegame.capi.biz.module.bakunawa.bet.strategy;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.BakunawaConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.BakunawaGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.BakunawaBetRecordManager;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.BakunawaOperationEventManager;
import com.wegame.capi.biz.module.bakunawa.bet.BakunawaBetHelper;
import com.wegame.capi.biz.module.bakunawa.game.BakunawaGameContext;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractCommonBetStrategy;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Objects;

import static com.wegame.capi.biz.builder.GamePlayBuilder.build;
import static com.wegame.capi.client.enums.GamePlayTypeEnum.NORMAL_PLAY;

/**
 * Bakunawa类游戏投注抽象接口
 */
@Slf4j
public abstract class AbstractBakunawaBetStrategy
        extends AbstractCommonBetStrategy<BakunawaGameRecordDo, BakunawaGameRecordDo>
        implements BakunawaBetStrategy<BakunawaGameRecordDo> {

    @Autowired
    BakunawaGameContext bakunawaGameContext;
    @Autowired
    BakunawaBetRecordManager bakunawaBetRecordManager;
    @Autowired
    BakunawaBetHelper bakunawaBetHelper;
    @Autowired
    @Qualifier("bakunawaOperationEventManager")
    BakunawaOperationEventManager bakunawaOperationEventManager;
    @Autowired
    BakunawaConfig bakunawaConfig;

    @Override
    public AbstractGameContext<BakunawaGameRecordDo> gameContext() {
        return bakunawaGameContext;
    }

    @Override
    public AbstractBetRecordManager<BakunawaGameRecordDo> betRecordManager() {
        return bakunawaBetRecordManager;
    }

    @Override
    public AbstractOperationEventManager operationEventManager() {
        return bakunawaOperationEventManager;
    }

    @Override
    public BetHelper<BakunawaGameRecordDo, BakunawaGameRecordDo> betHelper() {
        return bakunawaBetHelper;
    }


    @Override
    public BetResultDo<BakunawaGameRecordDo> bet(BetDo betDo) {
        //// 1、是否使用free spin
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

    @Override
    protected long getTotalRewardAmount(List<BakunawaGameRecordDo> gameRecordList, Object extParam) {
        if (CollectionUtil.isEmpty(gameRecordList)) {
            return 0L;
        } else {
            return gameRecordList.stream()
                    .filter(item -> Objects.nonNull(item.getRewardResult()))
                    .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                    .sum();
        }
    }

    private BetResultDo<BakunawaGameRecordDo> normalBet(BetDo betDo) {
        long betRecordId = generateBetRecordId();
        List<BakunawaGameRecordDo> list = super.play(betDo, betRecordId, this.bakunawaConfig.getPayoutAmountLimitConfig(), null);
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        BakunawaGameRecordDo gameRecord = list.get(0);
        // 构建投注记录
        BetRecordDo<BakunawaGameRecordDo> betRecord = bakunawaBetHelper.buildBetRecord(betRecordId, betDo, list);
        // 保存投注记录并派奖
        SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betDo, betRecord, gameRecord.hasRewardFreeSpins());
        BetResultDo<BakunawaGameRecordDo> result = bakunawaBetHelper.handleBetResult(betRecord, resultDo);
        // 投注成功后发送延迟派奖通知
        afterBetSuccessSendDelayRewardNotify(resultDo, betRecord);
        return result;
    }
}
