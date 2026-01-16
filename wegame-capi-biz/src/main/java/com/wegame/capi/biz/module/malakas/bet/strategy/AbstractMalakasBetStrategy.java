package com.wegame.capi.biz.module.malakas.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MalakasBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.MalakasBetRecordManager;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.MalakasOperationEventManager;
import com.wegame.capi.biz.module.base.RewardHelper;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractCommonBetStrategy;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.biz.module.malakas.bet.MalakasBetHelper;
import com.wegame.capi.biz.module.malakas.game.MalakasGameContext;
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
 * Malakas游戏投注抽象接口
 */
@Slf4j
public abstract class AbstractMalakasBetStrategy
        extends AbstractCommonBetStrategy<MalakasGameRecordDo, MalakasBetRecordDetailDo>
        implements MalakasBetStrategy<MalakasBetRecordDetailDo> {

    @Autowired
    MalakasGameContext<MalakasGameRecordDo> malakasGameContext;
    @Autowired
    MalakasBetRecordManager malakasBetRecordManager;

    @Autowired
    @Qualifier("malakasOperationEventManager")
    private MalakasOperationEventManager malakasOperationEventManager;
    @Autowired
    private MalakasBetHelper malakasBetHelper;

    @Override
    public AbstractGameContext<MalakasGameRecordDo> gameContext() {
        return malakasGameContext;
    }

    @Override
    public AbstractBetRecordManager<MalakasGameRecordDo> betRecordManager() {
        return malakasBetRecordManager;
    }


    @Override
    public AbstractOperationEventManager operationEventManager() {
        return malakasOperationEventManager;
    }

    @Override
    public BetHelper<MalakasBetRecordDetailDo, MalakasGameRecordDo> betHelper() {
        return malakasBetHelper;
    }

    @Override
    public BetResultDo<MalakasBetRecordDetailDo> bet(BetDo betDo) {
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

    /**
     * 非免费轮投注(正常投注、购买投注)
     */
    private BetResultDo<MalakasBetRecordDetailDo> betForNonFreeWheel(BetDo betDo) {
        // 1、生成游戏记录
        long betRecordId = generateBetRecordId();
        List<MalakasGameRecordDo> list = gameContext().play(build(betDo, betRecordId));
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        List<MalakasGameRecordDo> firstBatchList = getFirstBatchGameRecordList(list);
        // 2、构建投注记录
        BetRecordDo<MalakasGameRecordDo> betRecord = malakasBetHelper.buildBetRecord(betRecordId, betDo, list);
        SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betDo, betRecord, malakasBetHelper.hasRewardFreeWheel(firstBatchList));
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        return malakasBetHelper.handleBetResult(betRecord, resultDo);

    }

    /**
     * 免费轮投注
     */
    private BetResultDo<MalakasBetRecordDetailDo> betForFreeWheelQuery(BetDo betDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        List<MalakasGameRecordDo> gameRecordList = betRecordManager().listGameRecord(
                GameRecordQuery.builder()
                        .betRecordId(betDo.getBetRecordId())
                        .playerId(userInfo.getPlayerId())
                        .batchId(betDo.getGameRecordId())
                        .build(), gameContext().gameInfo().getGameType());
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_PARAM);
        // 按照index升序排列
        gameRecordList = Optional.ofNullable(gameRecordList).orElse(Collections.emptyList()).stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(MalakasGameRecordDo::getIndex))
                .collect(Collectors.toList());
        //// 合计中奖总金额
        long overallRewardAmount = gameRecordList.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            MalakasGameRecordDo lastGameRecord = gameRecordList.stream().filter(item -> BooleanUtils.isTrue(item.isLastGameFlag())).findFirst().orElse(null);
            if (Objects.nonNull(lastGameRecord)) {
                // 后处理-免费轮
                freeWheelAfterHandle(lastGameRecord.getBetRecordId());
            }
        }
        return BetResultDo.<MalakasBetRecordDetailDo>builder()
                .betRecordId(betDo.getBetRecordId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .overallRewardAmount(overallRewardAmount)
                .totalPayoutAmount(0L)
                .resultDetail(MalakasBetRecordDetailDo.builder()
                        .allWheelRewardSubType(RewardHelper.calculateRewardSubType(betDo.getBetAmount(), overallRewardAmount))
                        .gameRecordList(gameRecordList)
                        .build())
                .build();
    }

    private List<MalakasGameRecordDo> getFirstBatchGameRecordList(List<MalakasGameRecordDo> list) {
        Long batchId = list.get(0).getBatchId();
        return list.stream()
                .filter(item -> Objects.equals(batchId, item.getBatchId()))
                .collect(Collectors.toList());
    }

    @Override
    protected long getTotalRewardAmount(List<MalakasGameRecordDo> list, Object extParam) {
        // todo
        return 0;
    }

    public void freeWheelAfterHandle(Long betRecordId) {
        // do nothing
    }

}
