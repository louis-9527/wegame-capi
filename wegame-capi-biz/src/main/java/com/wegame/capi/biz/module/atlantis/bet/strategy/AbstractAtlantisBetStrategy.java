package com.wegame.capi.biz.module.atlantis.bet.strategy;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.AtlantisConfig;
import com.wegame.capi.biz.domain.biz.AtlantisBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.AtlantisGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.AtlantisBetRecordManager;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.AtlantisOperationEventManager;
import com.wegame.capi.biz.module.atlantis.bet.AtlantisBetHelper;
import com.wegame.capi.biz.module.atlantis.game.AtlantisGameContext;
import com.wegame.capi.biz.module.base.RewardHelper;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractCommonBetStrategy;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.dao.repository.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.builder.GamePlayBuilder.build;


/**
 * Atlantis游戏投注抽象接口
 */
@Slf4j
public abstract class AbstractAtlantisBetStrategy
        extends AbstractCommonBetStrategy<AtlantisGameRecordDo, AtlantisBetRecordDetailDo>
        implements AtlantisBetStrategy<AtlantisBetRecordDetailDo> {

    @Autowired
    @Qualifier("atlantisGameContext")
    AtlantisGameContext<AtlantisGameRecordDo> atlantisGameContext;
    @Autowired
    @Qualifier("atlantisBetRecordManager")
    AtlantisBetRecordManager atlantisBetRecordManager;

    @Autowired
    @Qualifier("atlantisOperationEventManager")
    private AtlantisOperationEventManager atlantisOperationEventManager;
    @Autowired
    private AtlantisBetHelper atlantisBetHelper;
    @Autowired
    private AtlantisConfig atlantisConfig;

    @Override
    public AbstractGameContext<AtlantisGameRecordDo> gameContext() {
        return atlantisGameContext;
    }


    @Override
    public AbstractBetRecordManager<AtlantisGameRecordDo> betRecordManager() {
        return atlantisBetRecordManager;
    }


    @Override
    public AbstractOperationEventManager operationEventManager() {
        return atlantisOperationEventManager;
    }

    @Override
    public BetHelper<AtlantisBetRecordDetailDo, AtlantisGameRecordDo> betHelper() {
        return atlantisBetHelper;
    }

    @Override
    public BetResultDo<AtlantisBetRecordDetailDo> bet(BetDo betDo) {
        if (!BetSubTypeEnum.isFreeWheelQuery(betDo.getBetSubType().getCode())) {
            // 非免费轮投注流程
            //// 1、是否使用星卡
            if (isCanStarCardBet(betDo)) {
                // 1.1 是，则星卡投注流程
                return betForStartCard(betDo);
            } else if (isCanFreeSpinBet(betDo)) {
                // 1.1 是，则预定投注流程
                return betForFreeSpin(betDo);
            } else {
                // 1.2 否，则走其他投注流程
                // 1.2.1、判断能否走干预事件投注
                if (this.atlantisIsCanOperationEvent(betDo) && isCanOperationEventBet(betDo)) {
                    // 1.2.11 命中干预事件：走干预事件投注
                    return betForOperationEvent(betDo);
                } else {
                    // 1.2.12 未命中干预事件：走正常投注。
                    return betForNonFreeWheel(betDo);
                }
            }
        } else {
            // 免费轮投注, 走查询
            return betForFreeWheelQuery(betDo);
        }

//        if (!BetSubTypeEnum.isFreeWheel(betDo.getBetSubType().getCode())) {
//            return betForNonFreeWheel(betDo);
//        } else {
//            // 免费轮投注, 走查询
//            return betForFreeWheel(betDo);
//        }
    }

    @Override
    protected long getTotalRewardAmount(List<AtlantisGameRecordDo> gameRecordList, Object extParam) {
        if (CollectionUtil.isEmpty(gameRecordList)) {
            return 0L;
        } else {
            return gameRecordList.stream()
                    .filter(item -> Objects.nonNull(item.getRewardResult()))
                    .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                    .sum();
        }
    }

    private boolean atlantisIsCanOperationEvent(BetDo betDo) {
        // atlantis 只normal 和lockBoy(EX_BET)可支持dos
        BetSubTypeEnum betSubType = betDo.getBetSubType();
        return Objects.equals(betSubType, BetSubTypeEnum.NORMAL_BET);
    }

    /**
     * 非免费轮投注(正常投注、购买投注)
     */
    private BetResultDo<AtlantisBetRecordDetailDo> betForNonFreeWheel(BetDo betDo) {
        // 1、生成游戏记录
        long betRecordId = generateBetRecordId();
        List<AtlantisGameRecordDo> list = super.play(betDo, betRecordId, atlantisConfig.getPayoutAmountLimitConfig(), null);
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        List<AtlantisGameRecordDo> firstBatchList = atlantisBetHelper.getFirstBatchGameRecordList(list);
        // 2、构建投注记录
        BetRecordDo<AtlantisGameRecordDo> betRecord = atlantisBetHelper.buildBetRecord(betRecordId, betDo, list);
        SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betDo, betRecord, atlantisBetHelper.hasRewardFreeWheel(firstBatchList));
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        // 投注成功后发送延迟派奖通知
        sendDelayRewardNotifyAfterBet(resultDo, betRecord);
        return atlantisBetHelper.handleBetResult(betRecord, resultDo);
    }

    /**
     * 免费轮投注
     */
    private BetResultDo<AtlantisBetRecordDetailDo> betForFreeWheelQuery(BetDo betDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        List<AtlantisGameRecordDo> gameRecordList = betRecordManager().listGameRecord(
                GameRecordQuery.builder()
                        .betRecordId(betDo.getBetRecordId())
                        .playerId(userInfo.getPlayerId())
                        .batchId(betDo.getGameRecordId())
                        .build(), gameContext().gameInfo().getGameType());
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_PARAM);
        // 按照index升序排列
        gameRecordList = Optional.ofNullable(gameRecordList).orElse(Collections.emptyList()).stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(AtlantisGameRecordDo::getIndex))
                .collect(Collectors.toList());
        //// 合计中奖总金额
        long overallRewardAmount = gameRecordList.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        return BetResultDo.<AtlantisBetRecordDetailDo>builder()
                .betRecordId(betDo.getBetRecordId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .overallRewardAmount(overallRewardAmount)
                .totalPayoutAmount(0L)
                .resultDetail(AtlantisBetRecordDetailDo.builder()
                        .allWheelRewardSubType(RewardHelper.calculateRewardSubType(betDo.getBetAmount(), overallRewardAmount))
                        .gameRecordList(gameRecordList)
                        .build())
                .build();
    }
}
