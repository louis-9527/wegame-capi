package com.wegame.capi.biz.module.mangkukulam.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MangkukulamBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
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
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.dao.repository.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.builder.GamePlayBuilder.build;
import static com.wegame.capi.client.enums.GamePlayTypeEnum.NORMAL_PLAY;

/**
 * Mangkukulam游戏投注抽象接口
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
        if (!BetSubTypeEnum.isFreeWheelQuery(betDo.getBetSubType().getCode())) {
            return betForNonFreeWheel(betDo);
        } else {
            // 免费轮投注, 走查询
            return betForFreeWheelQuery(betDo);
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
            // 2、构建投注记录
            BetRecordDo<MangkukulamGameRecordDo> betRecord = mangkukulamBetHelper.buildBetRecord(betRecordId, betDo, list);
            // 3、投注并派彩
            SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betRecord, betHelper().hasRewardFreeWheel(list));
            AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
            return mangkukulamBetHelper.handleBetResult(betRecord, resultDo);
        } else if (BetSubTypeEnum.isSecondStep(betDo.getBetSubType().getCode())) {
            // 1、查询投注记录
            AssertUtil.assertNotNull(betDo.getParentBetRecordId(), ErrorCode.ERR_PARAM);
            BetRecordDo<MangkukulamGameRecordDo> preBetRecord = mangkukulamBetRecordManager.getBetRecord(betDo.getParentBetRecordId(), GameTypeEnum.MANGKUKULAM);
            AssertUtil.assertNotNull(preBetRecord, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(Objects.equals(preBetRecord.getStatus(), BetStatusEnum.SUCCESS.getCode()), ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(preBetRecord.getContinueStatus(), ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(StringUtils.equals(preBetRecord.getPlayerId(), UserInfoContext.getUserInfo().getPlayerId()), ErrorCode.ERR_PARAM);
            // 1、继续玩（完成波动选择后续部分）
            gamePlayDo.setContinuePlayContext(buildContinuePlayContext(preBetRecord, betDo));
            List<MangkukulamGameRecordDo> list = gameContext().play(gamePlayDo);
            AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
            // 2、构建投注记录
            BetRecordDo<MangkukulamGameRecordDo> betRecord = mangkukulamBetHelper.buildBetRecord(betRecordId, betDo, list);
            betRecord.setContinueStatus(Boolean.FALSE);
            // 3、投注并派彩
            SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betRecord, true);
            AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
            // 4、异步更新投注原始记录 todo，id和parentId加唯一索引
            preBetRecord.setContinueStatus(Boolean.FALSE);
            mangkukulamBetRecordManager.asyncUpdateBetRecord(preBetRecord, GameTypeEnum.MANGKUKULAM);
            // 5、构建返回结果
            Long lastGameRecordId = gamePlayDo.getContinuePlayContext().getParentGameRecordId();
            return mangkukulamBetHelper.handleBetResult(betRecord, list, resultDo.getCredit(), lastGameRecordId);
        } else {
            throw new BusinessException(ErrorCode.ERR_PARAM);
        }
    }

    private GamePlayDo.ContinuePlayContext buildContinuePlayContext(BetRecordDo<MangkukulamGameRecordDo> preBetRecord, BetDo betDo) {
        // 1、使用正常投注中的投注金额
        betDo.setBetAmount(preBetRecord.getBetAmount());
        // 2、组装继续玩儿上下文
        // 2.1、查询中免费轮的投注游戏记录
        Sort sort = Sort.by(Sort.Direction.DESC, "index");
        GameRecordQuery gameRecordQuery = GameRecordQuery.builder().betRecordId(betDo.getParentBetRecordId()).sort(sort).build();
        MangkukulamGameRecordDo lastGameRecord = mangkukulamBetRecordManager.findOneGameRecord(gameRecordQuery, GameTypeEnum.MANGKUKULAM);
        AssertUtil.assertNotNull(lastGameRecord, ErrorCode.ERR_INTER);
        // 3、构建返回结果
        int rewardFreeWheelTimes = lastGameRecord.getRewardResult().getRewardFreeWheelTimes();
        return GamePlayDo.ContinuePlayContext.builder()
                .parentBetRecordId(betDo.getParentBetRecordId())
                .parentGameRecordId(lastGameRecord.getId())
                .totalPayoutAmount(preBetRecord.getPayOutAmount())
                .freeWheelTimes(rewardFreeWheelTimes)
                .build();
    }

    /**
     * 免费轮投注
     */
    private BetResultDo<MangkukulamBetRecordDetailDo> betForFreeWheelQuery(BetDo betDo) {
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


    public void freeWheelAfterHandle(Long betRecordId) {
        // do nothing
    }

}
