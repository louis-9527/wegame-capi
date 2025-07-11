package com.wegame.capi.biz.module.mangkukulam.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.MangkukulamConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MangkukulamBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.MangkukulamBetRecordManager;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.MangkukulamOperationEventManager;
import com.wegame.capi.biz.module.base.RewardHelper;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractCommonBetStrategy;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.biz.module.mangkukulam.MangkukulamCalcUtil;
import com.wegame.capi.biz.module.mangkukulam.bet.MangkukulamBetHelper;
import com.wegame.capi.biz.module.mangkukulam.game.MangkukulamGameContext;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.repository.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.builder.GamePlayBuilder.build;
import static com.wegame.capi.biz.module.base.AmountHelper.maxAmountControl;
import static com.wegame.capi.client.enums.GamePlayTypeEnum.NORMAL_PLAY;

/**
 * lamang游戏投注抽象接口
 */
@Slf4j
public abstract class AbstractMangkukulamBetStrategy extends AbstractCommonBetStrategy<MangkukulamGameRecordDo,
        MangkukulamBetRecordDetailDo> implements MangkukulamBetStrategy<MangkukulamBetRecordDetailDo> {

    @Autowired
    MangkukulamGameContext<MangkukulamGameRecordDo> mangkukulamGameContext;
    @Autowired
    MangkukulamBetRecordManager mangkukulamBetRecordManager;

    @Autowired
    @Qualifier("mangkukulamOperationEventManager")
    private MangkukulamOperationEventManager mangkukulamOperationEventManager;

    @Autowired
    private MangkukulamBetHelper mangkukulamBetHelper;
    @Autowired
    MangkukulamConfig mangkukulamConfig;

    @Override
    public AbstractGameContext<MangkukulamGameRecordDo> gameContext() {
        return mangkukulamGameContext;
    }

    @Override
    public AbstractBetRecordManager<MangkukulamGameRecordDo> betRecordManager() {
        return mangkukulamBetRecordManager;
    }

    @Override
    public AbstractOperationEventManager operationEventManager() {
        return mangkukulamOperationEventManager;
    }

    @Override
    public BetHelper<MangkukulamBetRecordDetailDo, MangkukulamGameRecordDo> betHelper() {
        return mangkukulamBetHelper;
    }

    @Override
    public BetResultDo<MangkukulamBetRecordDetailDo> bet(BetDo betDo) {
//        if (!BetSubTypeEnum.isFreeWheel(betDo.getBetSubType().getCode())) {
//            // 非免费轮投注流程
//            //// 1、是否使用星卡
//            if (isCanStarCardBet(betDo)) {
//                // 1.1 是，则星卡投注流程
//                return betForStartCard(betDo);
//            } else if (isCanFreeSpinBet(betDo)) {
//                // 1.1 是，则预定投注流程
//                return betForFreeSpin(betDo);
//            } else {
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
        if (!BetSubTypeEnum.isFreeWheel(betDo.getBetSubType().getCode())) {
            return betForNonFreeWheel(betDo);
        } else {
            // 免费轮投注, 走查询
            return betForFreeWheel(betDo);
        }

    }

    /**
     * 非免费轮投注(正常投注、购买投注)
     */
    private BetResultDo<MangkukulamBetRecordDetailDo> betForNonFreeWheel(BetDo betDo) {
        // 1、生成游戏记录
        long betRecordId = generateBetRecordId();
        GamePlayDo gamePlayDo = build(NORMAL_PLAY, betDo, betRecordId, null);
        if (BetSubTypeEnum.isFirstStep(betDo.getBetSubType().getCode())) {
            // 1、play
            List<MangkukulamGameRecordDo> list = gameContext().play(gamePlayDo);
            AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
            List<MangkukulamGameRecordDo> firstBatchList = getFirstBatchGameRecordList(list);
            // 2、构建投注记录
            BetRecordDo<MangkukulamGameRecordDo> betRecord = mangkukulamBetHelper.buildBetRecord(betRecordId, betDo, list);
            MangkukulamGameRecordDo lastGameRecord = firstBatchList.get(firstBatchList.size() - 1);
            betRecord.setContinueStatus(lastGameRecord.isLastGameFlag());
            // 3、投注并派彩
            SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betRecord, betHelper().hasRewardFreeWheel(firstBatchList));
            AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
            return mangkukulamBetHelper.handleBetResult(betRecord, resultDo);
        } else {
            // 1、查询投注记录
            BetRecordDo preBetRecord = mangkukulamBetRecordManager.getBetRecord(betDo.getParentBetRecordId(), GameTypeEnum.MANGKUKULAM);
            if (BooleanUtils.isFalse(preBetRecord.getContinueStatus()))  {
                

                // 1、查询game record
                return betForFreeWheel(betDo);
            } else {
                // 1、继续玩（完成波动选择后续部分）
                gamePlayDo.setContinuePlayContext(buildContinuePlayContext(betDo));
                List<MangkukulamGameRecordDo> list = gameContext().play(gamePlayDo);
                AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
                List<MangkukulamGameRecordDo> firstBatchList = getFirstBatchGameRecordList(list);
                // 2、构建投注记录
                BetRecordDo<MangkukulamGameRecordDo> betRecord = mangkukulamBetHelper.buildBetRecord(betRecordId, betDo, list);
                betRecord.setContinueStatus(Boolean.FALSE);
                // 3、投注并派彩
                SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betRecord, betHelper().hasRewardFreeWheel(firstBatchList));
                AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
                // 4、异步更新投注记录
                mangkukulamBetRecordManager.asyncUpdateBetRecord(betRecord, GameTypeEnum.MANGKUKULAM);
                // 5、构建返回结果
                MangkukulamGameRecordDo lastGameRecord = firstBatchList.get(firstBatchList.size() - 1);
                long overallRewardAmount = MangkukulamCalcUtil.calcTotalRewardAmount(firstBatchList);
                return BetResultDo.<MangkukulamBetRecordDetailDo>builder()
                        .betRecordId(betRecord.getId())
                        .gameId(betRecord.getGameId())
                        .betType(BetTypeEnum.valueOfCode(betRecord.getBetType()))
                        .betSubType(BetSubTypeEnum.valueOfCode(betRecord.getBetSubType()))
                        .credit(resultDo.getCredit())
                        .overallRewardAmount(maxAmountControl(overallRewardAmount, betRecord.getBetAmount(), mangkukulamConfig.getRewardMaxMultiple()))
                        .totalPayoutAmount(betRecord.getPayOutAmount())
                        .resultDetail(MangkukulamBetRecordDetailDo.builder()
                                .allWheelRewardSubType(RewardHelper.calculateRewardSubType(betRecord.getBetAmount(), overallRewardAmount))
                                .gameRecordList(null)
                                .volatilityGameRecordIds(lastGameRecord.isLastGameFlag() ? null : lastGameRecord.getGameRecordIds())
                                .build())
                        .build();
            }
        }
    }

    private GamePlayDo.ContinuePlayContext buildContinuePlayContext(BetDo betDo) {
        if (Objects.nonNull(betDo.getParentBetRecordId())) {
            // 查询中免费轮的投注游戏记录
            GameRecordQuery gameRecordQuery = GameRecordQuery.builder().betRecordId(betDo.getParentBetRecordId()).build();
            List<MangkukulamGameRecordDo> gameRecordList = mangkukulamBetRecordManager.listGameRecord(gameRecordQuery, GameTypeEnum.MANGKUKULAM);
            AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_INTER);

            // 1.获取累计中奖金额
            Long totalRewardAmount = gameRecordList.stream().map(MangkukulamGameRecordDo::getRewardResult).map(RewardResultDo::getTotalRewardAmount).mapToLong(Long::longValue).sum();

            // 2.获取获得的免费轮次数
            MangkukulamGameRecordDo lastGameRecord = gameRecordList.get(gameRecordList.size() - 1);
            int rewardFreeWheelTimes = lastGameRecord.getRewardResult().getRewardFreeWheelTimes();

            return GamePlayDo.ContinuePlayContext.builder()
                    .parentBetRecordId(betDo.getParentBetRecordId())
                    .parentGameRecordId(lastGameRecord.getId())
                    .totalPayoutAmount(totalRewardAmount)
                    .freeWheelTimes(rewardFreeWheelTimes)
                    .build();
        }
        return null;
    }

    /**
     * 免费轮投注
     */
    private BetResultDo<MangkukulamBetRecordDetailDo> betForFreeWheel(BetDo betDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        GameRecordQuery gameRecordQuery = GameRecordQuery.builder().betRecordId(betDo.getBetRecordId()).playerId(userInfo.getPlayerId()).batchId(betDo.getGameRecordId()).build();
        List<MangkukulamGameRecordDo> gameRecordList = betRecordManager().listGameRecord(gameRecordQuery, gameContext().gameInfo().getGameType());
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_PARAM);
        // 按照index升序排列
        gameRecordList = Optional.ofNullable(gameRecordList)
                .orElse(Collections.emptyList())
                .stream().filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(MangkukulamGameRecordDo::getIndex))
                .collect(Collectors.toList());
        //// 合计中奖总金额
        long overallRewardAmount = MangkukulamCalcUtil.calcTotalRewardAmount(gameRecordList);
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            MangkukulamGameRecordDo lastGameRecord = gameRecordList.stream()
                    .filter(item -> BooleanUtils.isTrue(item.isLastGameFlag()))
                    .findFirst()
                    .orElse(null);
            if (Objects.nonNull(lastGameRecord)) {
                // 后处理-免费轮
                freeWheelAfterHandle(lastGameRecord.getBetRecordId());
            }
        }
        return BetResultDo.<MangkukulamBetRecordDetailDo>builder()
                .betRecordId(betDo.getBetRecordId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .overallRewardAmount(overallRewardAmount)
                .totalPayoutAmount(0L)
                .resultDetail(MangkukulamBetRecordDetailDo
                        .builder()
                        .allWheelRewardSubType(RewardHelper.calculateRewardSubType(betDo.getBetAmount(), overallRewardAmount))
                        .gameRecordList(gameRecordList).build())
                .build();
    }

    private List<MangkukulamGameRecordDo> getFirstBatchGameRecordList(List<MangkukulamGameRecordDo> list) {
        Long batchId = list.get(0).getBatchId();
        return list.stream().filter(item -> Objects.equals(batchId, item.getBatchId())).collect(Collectors.toList());
    }

    public void freeWheelAfterHandle(Long betRecordId) {
        // do nothing
    }

}
