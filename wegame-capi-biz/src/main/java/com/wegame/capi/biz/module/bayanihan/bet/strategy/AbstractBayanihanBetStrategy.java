package com.wegame.capi.biz.module.bayanihan.bet.strategy;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.BayanihanConfig;
import com.wegame.capi.biz.domain.BayanihanBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.BayanihanGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.BayanihanBetRecordManager;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.BayanihanOperationEventManager;
import com.wegame.capi.biz.module.base.RewardHelper;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractCommonBetStrategy;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.biz.module.bayanihan.bet.BayanihanBetHelper;
import com.wegame.capi.biz.module.bayanihan.game.BayanihanGameContext;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.dao.repository.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Atlantis游戏投注抽象接口
 */
@Slf4j
public abstract class AbstractBayanihanBetStrategy
        extends AbstractCommonBetStrategy<BayanihanGameRecordDo, BayanihanBetRecordDetailDo>
        implements BayanihanBetStrategy<BayanihanBetRecordDetailDo> {

    @Autowired
    @Qualifier("bayanihanGameContext")
    BayanihanGameContext<BayanihanGameRecordDo> bayanihanGameContext;
    @Autowired
    @Qualifier("bayanihanBetRecordManager")
    BayanihanBetRecordManager bayanihanBetRecordManager;

    @Autowired
    @Qualifier("bayanihanOperationEventManager")
    private BayanihanOperationEventManager bayanihanOperationEventManager;
    @Autowired
    private BayanihanBetHelper bayanihanBetHelper;
    @Autowired
    private BayanihanConfig bayanihanConfig;

    @Override
    public AbstractGameContext<BayanihanGameRecordDo> gameContext() {
        return bayanihanGameContext;
    }


    @Override
    public AbstractBetRecordManager<BayanihanGameRecordDo> betRecordManager() {
        return bayanihanBetRecordManager;
    }


    @Override
    public AbstractOperationEventManager operationEventManager() {
        return bayanihanOperationEventManager;
    }

    @Override
    public BetHelper<BayanihanBetRecordDetailDo, BayanihanGameRecordDo> betHelper() {
        return bayanihanBetHelper;
    }

    @Override
    public BetResultDo<BayanihanBetRecordDetailDo> bet(BetDo betDo) {
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
                if (isCanOperationEventBet(betDo)) {
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
    }

    @Override
    protected long getTotalRewardAmount(List<BayanihanGameRecordDo> gameRecordList, Object extParam) {
        // todo 修改计算方法
        if (CollectionUtil.isEmpty(gameRecordList)) {
            return 0L;
        } else {
            BigDecimal freeWheelFinalMultiple = getFreeWheelFinalMultiple(gameRecordList);
            return gameRecordList.stream()
                    .filter(item -> Objects.nonNull(item.getRewardResult()))
                    .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                    .sum();
        }
    }

    /**
     * 非免费轮投注(正常投注、购买投注)
     */
    private BetResultDo<BayanihanBetRecordDetailDo> betForNonFreeWheel(BetDo betDo) {
        // 1、生成游戏记录
        long betRecordId = generateBetRecordId();
        List<BayanihanGameRecordDo> list = super.play(betDo, betRecordId, bayanihanConfig.getPayoutAmountLimitConfig(), null);
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        List<BayanihanGameRecordDo> firstBatchList = bayanihanBetHelper.getFirstBatchGameRecordList(list);
        // 2、构建投注记录
        BetRecordDo<BayanihanGameRecordDo> betRecord = bayanihanBetHelper.buildBetRecord(betRecordId, betDo, list);
        SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betDo, betRecord, bayanihanBetHelper.hasRewardFreeWheel(firstBatchList));
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        return bayanihanBetHelper.handleBetResult(betRecord, resultDo);
    }

    /**
     * 免费轮投注
     */
    private BetResultDo<BayanihanBetRecordDetailDo> betForFreeWheelQuery(BetDo betDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        List<BayanihanGameRecordDo> gameRecordList = betRecordManager().listGameRecord(
                GameRecordQuery.builder()
                        .betRecordId(betDo.getBetRecordId())
                        .playerId(userInfo.getPlayerId())
                        .batchId(betDo.getGameRecordId())
                        .build(), gameContext().gameInfo().getGameType());
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_PARAM);
        // 按照index升序排列
        gameRecordList = Optional.ofNullable(gameRecordList).orElse(Collections.emptyList()).stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(BayanihanGameRecordDo::getIndex))
                .collect(Collectors.toList());
        //// 合计中奖总金额
        long overallRewardAmount = gameRecordList.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            BayanihanGameRecordDo lastGameRecord = gameRecordList.stream().filter(item -> BooleanUtils.isTrue(item.isLastGameFlag())).findFirst().orElse(null);
            if (Objects.nonNull(lastGameRecord)) {
                // 后处理-免费轮
                freeWheelAfterHandle(lastGameRecord.getBetRecordId());
            }
        }
        return BetResultDo.<BayanihanBetRecordDetailDo>builder()
                .betRecordId(betDo.getBetRecordId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .overallRewardAmount(overallRewardAmount)
                .totalPayoutAmount(0L)
                .resultDetail(BayanihanBetRecordDetailDo.builder()
                        .allWheelRewardSubType(RewardHelper.calculateRewardSubType(betDo.getBetAmount(), overallRewardAmount))
                        .gameRecordList(gameRecordList)
                        .build())
                .build();
    }

    public void freeWheelAfterHandle(Long betRecordId) {
        // do nothing
    }

}
