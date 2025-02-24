package com.wegame.capi.biz.module.ibongadarna.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.IbongAdarnaBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.IbongAdarnaBetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.IbongAdarnaGameRecordDo;
import com.wegame.capi.biz.manager.AbstractRecordManager;
import com.wegame.capi.biz.manager.IbongAdarnaRecordManager;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractBetStrategy;
import com.wegame.capi.biz.module.base.game.Game;
import com.wegame.capi.biz.module.ibongadarna.game.IbongAdarnaGame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * IbongAdarna类游戏投注抽象接口
 */
@Slf4j
public abstract class AbstractIbongAdarnaBetStrategy
        extends AbstractBetStrategy<IbongAdarnaGameRecordDo, IbongAdarnaBetDo, IbongAdarnaGameRecordDo>
        implements IbongAdarnaBetStrategy<IbongAdarnaBetDo, IbongAdarnaGameRecordDo> {

    @Autowired
    IbongAdarnaGame ibongAdarnaGame;
    @Autowired
    IbongAdarnaRecordManager ibongAdarnaRecordManager;
    @Autowired
    IbongAdarnaConfig ibongAdarnaConfig;

    @Override
    public Game<IbongAdarnaGameRecordDo> getGame() {
        return ibongAdarnaGame;
    }

    @Override
    public AbstractRecordManager<IbongAdarnaGameRecordDo> getRecordManager() {
        return ibongAdarnaRecordManager;
    }

    @Override
    public BetResultDo<IbongAdarnaGameRecordDo> bet(BetDo<IbongAdarnaBetDo> betDo) {
        long betRecordId = generateBetRecordId();
        List<IbongAdarnaGameRecordDo> list = getGame().play(GamePlayDo.builder()
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .betAmount(betDo.getBetDetail().getBetAmount())
                .betRecordId(betRecordId)
                .build());
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        IbongAdarnaGameRecordDo gameRecord = list.get(0);

        // 构建投注记录
        BetRecordDo<IbongAdarnaGameRecordDo> betRecord = BetRecordBuilder.build(betRecordId,
                betDo,
                betDo.getBetDetail().getBetAmount(),
                betDo.getBetDetail().getPayAmount(),
                getActualRewardAmount
                        (gameRecord.getRewardResult().getTotalRewardAmount(), betDo.getBetDetail().getBetAmount()),
                list);

        // 保存投注记录并派奖
        Long userCredit = saveBetRecordAndBetWithPayout(betRecord, gameRecord.hasRewardFreeSpins());
        // 中大奖处理
        this.rewardAfterHandle(betDo, getActualRewardAmount
                (gameRecord.getRewardResult().getTotalRewardAmount(), betDo.getBetDetail().getBetAmount()));

        // 组装返回结果
        return BetResultDo.<IbongAdarnaGameRecordDo>builder()
                .betRecordId(betRecord.getId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .credit(userCredit)
                .overallRewardAmount(Objects.nonNull(gameRecord.getRewardResult()) ? gameRecord.getRewardResult().getTotalRewardAmount() : 0L)
                .totalPayoutAmount(betRecord.getPayOutAmount())
                .resultDetail(gameRecord)
                .build();
    }


    public void rewardAfterHandle(BetDo<IbongAdarnaBetDo> betDo, Long totalRewardAmount) {
        // do nothing
    }

    private long getActualRewardAmount(long currentTotalAmount, Long betAmount) {
        // 大于投注金额的
        Integer rewardMaxMultiple = ibongAdarnaConfig.getMaxRewardMultiple();
        return Math.min(currentTotalAmount, betAmount * rewardMaxMultiple);
    }
}
