package com.wegame.capi.biz.module.diawatamayari.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.DiwataMayariBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.DiwataMayariGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.DiwataMayariBetRecordManager;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.DiwataMayariOperationEventManager;
import com.wegame.capi.biz.module.base.RewardHelper;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractCommonBetStrategy;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.biz.module.diawatamayari.bet.DiwataMayariBetHelper;
import com.wegame.capi.biz.module.diawatamayari.game.DiwataMayariGameContext;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
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
 * DiwataMayari游戏投注抽象接口
 */
@Slf4j
public abstract class AbstractDiwataMayariBetStrategy
        extends AbstractCommonBetStrategy<DiwataMayariGameRecordDo, DiwataMayariBetRecordDetailDo>
        implements DiwataMayariBetStrategy<DiwataMayariBetRecordDetailDo> {

    @Autowired
    DiwataMayariGameContext<DiwataMayariGameRecordDo> diwataMayariGameContext;
    @Autowired
    DiwataMayariBetRecordManager diwataMayariBetRecordManager;

    @Autowired
    @Qualifier("diwataMayariOperationEventManager")
    private DiwataMayariOperationEventManager diwataMayariOperationEventManager;
    @Autowired
    private DiwataMayariBetHelper diwataMayariBetHelper;

    @Override
    public AbstractGameContext<DiwataMayariGameRecordDo> gameContext() {
        return diwataMayariGameContext;
    }


    @Override
    public AbstractBetRecordManager<DiwataMayariGameRecordDo> betRecordManager() {
        return diwataMayariBetRecordManager;
    }


    @Override
    public AbstractOperationEventManager operationEventManager() {
        return diwataMayariOperationEventManager;
    }

    @Override
    public BetHelper<DiwataMayariBetRecordDetailDo, DiwataMayariGameRecordDo> betHelper() {
        return diwataMayariBetHelper;
    }

    @Override
    public BetResultDo<DiwataMayariBetRecordDetailDo> bet(BetDo betDo) {
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

    public GamePlayTypeEnum getGamePlayType() {
        return GamePlayTypeEnum.TEMPLATE_PLAY;
    }

    /**
     * 非免费轮投注(正常投注、购买投注)
     */
    private BetResultDo<DiwataMayariBetRecordDetailDo> betForNonFreeWheel(BetDo betDo) {
        // 1、生成游戏记录
        long betRecordId = generateBetRecordId();
        List<DiwataMayariGameRecordDo> list = gameContext().play(build(betDo, betRecordId));
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        List<DiwataMayariGameRecordDo> firstBatchList = getFirstBatchGameRecordList(list);
        // 2、构建投注记录
        BetRecordDo<DiwataMayariGameRecordDo> betRecord = diwataMayariBetHelper.buildBetRecord(betRecordId, betDo, list);
        SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betDo, betRecord, diwataMayariBetHelper.hasRewardFreeWheel(firstBatchList));
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        return diwataMayariBetHelper.handleBetResult(betRecord, resultDo);

    }

    /**
     * 免费轮投注
     */
    private BetResultDo<DiwataMayariBetRecordDetailDo> betForFreeWheelQuery(BetDo betDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        //        List<DiwataMayariGameRecordDo> gameRecordList = betRecordManager().listGameRecord(
        //                GameRecordQuery.builder()
        //                        .betRecordId(betDo.getBetRecordId())
        //                        .playerId(userInfo.getPlayerId())
        //                        .batchId(betDo.getGameRecordId())
        //                        .build(), gameContext().gameInfo().getGameType());
        GameRecordQuery query = GameRecordQuery.builder()
                .betRecordId(betDo.getBetRecordId())
                .cacheKey(String.format("%s_%s", betDo.getBetRecordId(), userInfo.getPlayerId()))
                .fromPrimary(true)
                .build();
        List<DiwataMayariGameRecordDo> gameRecordList = betRecordManager().listGameRecordWithCache(query,
                gameContext().gameInfo().getGameType());
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_PARAM);
        // 按照index升序排列
        gameRecordList = Optional.ofNullable(gameRecordList).orElse(Collections.emptyList()).stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(DiwataMayariGameRecordDo::getIndex))
                .collect(Collectors.toList());
        //// 合计中奖总金额
        long overallRewardAmount = gameRecordList.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            DiwataMayariGameRecordDo lastGameRecord = gameRecordList.stream().filter(item -> BooleanUtils.isTrue(item.isLastGameFlag())).findFirst().orElse(null);
            if (Objects.nonNull(lastGameRecord)) {
                // 后处理-免费轮
                freeWheelAfterHandle(lastGameRecord.getBetRecordId(), gameRecordList);
                betRecordManager().asyncEvictGameRecordCache(query, gameContext().gameInfo().getGameType());
            }
        }
        return BetResultDo.<DiwataMayariBetRecordDetailDo>builder()
                .betRecordId(betDo.getBetRecordId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .overallRewardAmount(overallRewardAmount)
                .totalPayoutAmount(0L)
                .resultDetail(DiwataMayariBetRecordDetailDo.builder()
                        .allWheelRewardSubType(RewardHelper.calculateRewardSubType(betDo.getBetAmount(), overallRewardAmount))
                        .gameRecordList(gameRecordList)
                        .build())
                .build();
    }

    private List<DiwataMayariGameRecordDo> getFirstBatchGameRecordList(List<DiwataMayariGameRecordDo> list) {
        Long batchId = list.get(0).getBatchId();
        return list.stream()
                .filter(item -> Objects.equals(batchId, item.getBatchId()))
                .collect(Collectors.toList());
    }

    public void freeWheelAfterHandle(Long betRecordId, List<DiwataMayariGameRecordDo> list) {
        // do nothing
    }

}
