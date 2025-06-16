package com.wegame.capi.biz.module.lamang.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.LamangBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.LamangGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.LamangBetRecordManager;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.LamangOperationEventManager;
import com.wegame.capi.biz.module.base.RewardHelper;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractCommonBetStrategy;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.biz.module.lamang.bet.LamangBetHelper;
import com.wegame.capi.biz.module.lamang.game.LamangGameContext;
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
import static com.wegame.capi.client.enums.GamePlayTypeEnum.NORMAL_PLAY;

/**
 * lamang游戏投注抽象接口
 */
@Slf4j
public abstract class AbstractLamangBetStrategy
        extends AbstractCommonBetStrategy<LamangGameRecordDo, LamangBetRecordDetailDo>
        implements LamangBetStrategy<LamangBetRecordDetailDo> {

    @Autowired
    LamangGameContext<LamangGameRecordDo> lamangGameContext;
    @Autowired
    LamangBetRecordManager lamangBetRecordManager;
    
    @Autowired
    @Qualifier("lamangOperationEventManager")
    private LamangOperationEventManager lamangOperationEventManager;

    @Autowired
    private LamangBetHelper lamangBetHelper;

    @Override
    public AbstractGameContext<LamangGameRecordDo> gameContext() {
        return lamangGameContext;
    }

    @Override
    public AbstractBetRecordManager<LamangGameRecordDo> betRecordManager() {
        return lamangBetRecordManager;
    }

    @Override
    public AbstractOperationEventManager operationEventManager() {
        return lamangOperationEventManager;
    }

    @Override
    public BetHelper<LamangBetRecordDetailDo, LamangGameRecordDo> betHelper() {
        return lamangBetHelper;
    }

    @Override
    public BetResultDo<LamangBetRecordDetailDo> bet(BetDo betDo) {
//        if (!BetSubTypeEnum.isFreeWheel(betDo.getBetSubType().getCode())) {
//            // 非免费轮投注流程
//            //// 1、是否使用星卡
//            if (isCanStarCardBet(betDo)) {
//                // 1.1 是，则星卡投注流程
//                return betForStartCard(betDo);
//            } else if (isCanFreeSpinBet(betDo)) {
//                // 1.1 是，则预定投注流程
//                return betForFreeSpin(betDo);
//            }  else {
//                // 1.2 否，则走其他投注流程
//                // 1.2.1、判断能否走干预事件投注
//                if (isCanOperationEventBet(betDo)) {
//                    // 1.2.11 命中干预事件：走干预事件投注
//                    return betForOperationEvent(betDo);
//                } else {
//                    // 1.2.12 未命中干预事件：走正常投注。
//                    return betForNonFreeWheel(betDo);
//                }
//            }
//        } else {
//            // 免费轮投注, 走查询
//            return betForFreeWheel(betDo);
//        }
        if (!BetSubTypeEnum.isFreeWheel(betDo.getBetSubType().getCode())) {
            return betForNonFreeWheel(betDo);
        } else {
            return betForFreeWheel(betDo);
        }
    }

    /**
     * 非免费轮投注(正常投注、购买投注)
     */
    private BetResultDo<LamangBetRecordDetailDo> betForNonFreeWheel(BetDo betDo) {
        // 1、生成游戏记录
        long betRecordId = generateBetRecordId();
        List<LamangGameRecordDo> list = gameContext().play(build(NORMAL_PLAY, betDo, betRecordId, null));
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        List<LamangGameRecordDo> firstBatchList = getFirstBatchGameRecordList(list);
        // 2、构建投注记录
        BetRecordDo<LamangGameRecordDo> betRecord = lamangBetHelper.buildBetRecord(betRecordId, betDo, list);
        SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betRecord, betHelper().hasRewardFreeWheel(firstBatchList));
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        return lamangBetHelper.handleBetResult(betRecord, resultDo);
    }

    /**
     * 免费轮投注
     */
    private BetResultDo<LamangBetRecordDetailDo> betForFreeWheel(BetDo betDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        List<LamangGameRecordDo> gameRecordList = betRecordManager().listGameRecord(
                GameRecordQuery.builder()
                        .betRecordId(betDo.getBetRecordId())
                        .playerId(userInfo.getPlayerId())
                        .batchId(betDo.getGameRecordId())
                        .build(), gameContext().gameInfo().getGameType());
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_PARAM);
        // 按照index升序排列
        gameRecordList = Optional.ofNullable(gameRecordList).orElse(Collections.emptyList()).stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(LamangGameRecordDo::getIndex))
                .collect(Collectors.toList());
        //// 合计中奖总金额
        long overallRewardAmount = gameRecordList.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            LamangGameRecordDo lastGameRecord = gameRecordList.stream().filter(item -> BooleanUtils.isTrue(item.isLastGameFlag())).findFirst().orElse(null);
            if (Objects.nonNull(lastGameRecord)) {
                // 后处理-免费轮
                freeWheelAfterHandle(lastGameRecord.getBetRecordId());
            }
        }
        return BetResultDo.<LamangBetRecordDetailDo>builder()
                .betRecordId(betDo.getBetRecordId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .overallRewardAmount(overallRewardAmount)
                .totalPayoutAmount(0L)
                .resultDetail(LamangBetRecordDetailDo.builder()
                        .allWheelRewardSubType(RewardHelper.calculateRewardSubType(betDo.getBetAmount(), overallRewardAmount))
                        .gameRecordList(gameRecordList)
                        .build())
                .build();
    }

    private List<LamangGameRecordDo> getFirstBatchGameRecordList(List<LamangGameRecordDo> list) {
        Long batchId = list.get(0).getBatchId();
        return list.stream()
                .filter(item -> Objects.equals(batchId, item.getBatchId()))
                .collect(Collectors.toList());
    }

    public void freeWheelAfterHandle(Long betRecordId) {
        // do nothing
    }

}
