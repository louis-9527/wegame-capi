package com.wegame.capi.biz.module.mangkukulam.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.MangkukulamConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.mangkukulam.reward.MangkukulamRewardHandler;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.common.enums.RewardTypeEnum;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper.MANGKUKULAM_GAME_INFO;
import static com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper.MANGKUKULAM_SYMBOL_PLACEHOLDER;

@Slf4j
@Component("mangkukulamGameNormalPlay")
public class MangkukulamGameNormalPlay extends AbstractGame<MangkukulamGameRecordDo> {


    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo GAME_INFO = MANGKUKULAM_GAME_INFO;

    // todo 删除
    @Autowired(required = false)
    MangkukulamConfig mangkukulamConfig;

    @Autowired
    MangkukulamRewardHandler mangkukulamRewardHandler;

    @Override
    public List<MangkukulamGameRecordDo> play(GamePlayDo gamePlay) {
        List<MangkukulamGameRecordDo> gameRecordResult = new ArrayList<>();
        LinkedList<FreeWheelHandleDo> freeWheelList = new LinkedList<>();
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        // todo 执行
        if (BetSubTypeEnum.isFirstStep(gamePlay.getBetSubType().getCode()) && Objects.nonNull(gamePlay.getVolatility())) {
            // 执行完普通轮和免费轮
            generateFirstBatch(gamePlay, gameRecordResult, totalRewardAmount, freeWheelList);
            generateFreeBatch(gamePlay, gameRecordResult, totalRewardAmount, freeWheelList, null);
            setLastGameFlag(gameRecordResult);
        } else if (BetSubTypeEnum.isFirstStep(gamePlay.getBetSubType().getCode()) && Objects.isNull(gamePlay.getVolatility())) {
            // 执行首轮
            generateFirstBatch(gamePlay, gameRecordResult, totalRewardAmount, freeWheelList);
            MangkukulamGameRecordDo lastGame = gameRecordResult.get(gameRecordResult.size() - 1);
            if (Objects.nonNull(lastGame) && !lastGame.getRewardResult().isHasRewardFreeWheel()) {
                setLastGameFlag(gameRecordResult);
            }
        } else if (Objects.equals(BetSubTypeEnum.FREE_WHEEL_NORMAL_BET.getCode(), gamePlay.getBetSubType().getCode()) && Objects.nonNull(gamePlay.getVolatility())) {
            // 执行免费轮
            GamePlayDo.ContinuePlayContext context = gamePlay.getContinuePlayContext();
            AssertUtil.assertNotNull(gamePlay.getContinuePlayContext(), ErrorCode.ERR_INTER);
            loadContinuePlayContext(context, freeWheelList, totalRewardAmount);
            generateFreeBatch(gamePlay, gameRecordResult, totalRewardAmount, freeWheelList, context.getParentBetRecordId());
            setLastGameFlag(gameRecordResult);
        }
        return gameRecordResult;
    }

    /**
     * 生成首轮
     * scatter & wild 不会跟特殊wild在同一列
     * 特殊wild只会在2 3 4 列
     * wild & 火框 只会在 2 3 4 5 列
     */
    private void generateFirstBatch(GamePlayDo gamePlay, List<MangkukulamGameRecordDo> result, AtomicLong totalRewardAmount, LinkedList<FreeWheelHandleDo> freeWheelList) {
        WheelDo<MangkukulamSymbolDo> wheel = generateFirstWheel(gamePlay, result, totalRewardAmount, freeWheelList);
        RewardResultDo<MangkukulamSymbolDo> rewardResult = mangkukulamRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, null, totalRewardAmount.get()));
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        // 组装-游戏记录
        long gameRecordId = generateGameRecordId();
        // 计算抽奖次数
        MangkukulamGameRecordDo gameRecord = buildGameRecord(gameRecordId, RECORD_DEFAULT_PARENT_ID, gamePlay.getBetRecordId(), null,
                gameRecordId, wheel, rewardResult);
        result.add(gameRecord);
        totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
        loopPlayOnPreGameRecord(gamePlay, result, totalRewardAmount, gameRecord, freeWheelList, null, false);
    }

    /**
     * 生成免费轮
     */
    private void generateFreeBatch(GamePlayDo gamePlay, List<MangkukulamGameRecordDo> result, AtomicLong totalRewardAmount, LinkedList<FreeWheelHandleDo> freeWheelList, Long parentBetRecordId) {
        while (!freeWheelList.isEmpty()) {
            FreeWheelHandleDo freeWheel = freeWheelList.removeFirst();
            MangkukulamGameRecordDo parentGameRecord = result.stream().filter(item -> Objects.equals(freeWheel.getParentGameRecordId(), item.getId())).findFirst().orElse(null);
            WheelDo<MangkukulamSymbolDo> wheel = generateFirstWheel(gamePlay, result, totalRewardAmount, freeWheelList);
            RewardResultDo<MangkukulamSymbolDo> rewardResult = mangkukulamRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, null, totalRewardAmount.get()));
            long gameRecordId = generateGameRecordId();
            MangkukulamGameRecordDo gameRecord = buildGameRecord(gameRecordId, freeWheel.getParentGameRecordId(), gamePlay.getBetRecordId(), parentBetRecordId, gameRecordId, wheel, rewardResult);
            result.add(gameRecord);
            totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
            if (Objects.nonNull(parentGameRecord)) {
                parentGameRecord.addGameRecordId(gameRecordId);
            }
            loopPlayOnPreGameRecord(gamePlay, result, totalRewardAmount, gameRecord, freeWheelList, parentBetRecordId, true);
        }
    }

    private void loopPlayOnPreGameRecord(GamePlayDo gamePlay,
                                         List<MangkukulamGameRecordDo> ret,
                                         AtomicLong totalRewardAmount,
                                         MangkukulamGameRecordDo preGameRecord,
                                         LinkedList<FreeWheelHandleDo> freeWheelList,
                                         Long parentBetRecordId,
                                         boolean freeWheelFlag) {
        if (canContinuePlayOnPreWheel(gamePlay, preGameRecord.getRewardResult(), totalRewardAmount)) {
            WheelDo<MangkukulamSymbolDo> wheel = generateFromPreWheel(preGameRecord);
            RewardResultDo<MangkukulamSymbolDo> rewardResult = mangkukulamRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, preGameRecord.getRewardResult().getTotalMultiple(), totalRewardAmount.get()));
            long gameRecordId = generateGameRecordId();
            MangkukulamGameRecordDo gameRecord = buildGameRecord(gameRecordId, preGameRecord.getId(), gamePlay.getBetRecordId(), parentBetRecordId, preGameRecord.getBatchId(), wheel, rewardResult);
            ret.add(gameRecord);
            totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
            loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeWheelList, parentBetRecordId, freeWheelFlag);
        } else {
            if (!preGameRecord.getRewardResult().getAccumulateReachMaxMultiple()) {
                if (preGameRecord.getRewardResult().isHasRewardFreeWheel()) {
                    addFreeWheelTimes(freeWheelList, preGameRecord.getRewardResult().getRewardFreeWheelTimes(), preGameRecord.getId());
                }
            } else {
                // help gc
                freeWheelList.clear();
            }
        }
    }

    private WheelDo<MangkukulamSymbolDo> generateFirstWheel(GamePlayDo gamePlay, List<MangkukulamGameRecordDo> result, AtomicLong totalRewardAmount, LinkedList<FreeWheelHandleDo> freeWheelList) {
        //todo

        // 0. 初始化盘面，填充占位符
        MangkukulamSymbolDo[][] symbolArr = initWheel();
        // 1. 轮带表生成初始盘面
        // 2. 生成火框
        // 3. 生成特殊wild倍率
        return null;
    }

    private WheelDo<MangkukulamSymbolDo> generateFromPreWheel(MangkukulamGameRecordDo preGameRecord) {
        //todo

        //0. 准备阶段 复制盘面
        //1. 消除中奖元素
        //2. 生成
        return null;
    }

    private void getRandomConfig(GamePlayDo gamePlay, boolean freeWheelFlag) {
        
    }

    private void loadContinuePlayContext(GamePlayDo.ContinuePlayContext context, LinkedList<FreeWheelHandleDo> freeWheelList, AtomicLong totalRewardAmount) {
        addFreeWheelTimes(freeWheelList, context.getFreeWheelTimes(), context.getParentGameRecordId());
        totalRewardAmount.getAndAdd(context.getTotalPayoutAmount());
    }

    private MangkukulamSymbolDo[][] initWheel() {
        MangkukulamSymbolDo[][] symbolArr = new MangkukulamSymbolDo[MANGKUKULAM_GAME_INFO.getRow()][MANGKUKULAM_GAME_INFO.getColumn()];
        int lastRow = MANGKUKULAM_GAME_INFO.getRow() - 1;
        int lastColumn = MANGKUKULAM_GAME_INFO.getColumn() - 1;
        symbolArr[lastRow][0] = MANGKUKULAM_SYMBOL_PLACEHOLDER;
        symbolArr[lastRow][lastColumn] = MANGKUKULAM_SYMBOL_PLACEHOLDER;
        return symbolArr;
    }


    private MangkukulamGameRecordDo buildGameRecord(Long id,
                                                    Long parentId,
                                                    Long betRecordId,
                                                    Long parentBetRecordId,
                                                    Long batchId,
                                                    WheelDo<MangkukulamSymbolDo> wheel,
                                                    RewardResultDo<MangkukulamSymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return MangkukulamGameRecordDo.builder()
                .id(id)
                .parentId(parentId)
                .betRecordId(betRecordId)
                .parentBetRecordId(parentBetRecordId)
                .batchId(batchId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .index(wheel.getIndex())
                .freeWheelFlag(wheel.isFreeFlag())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .gameRecordIds(null)
                .lastGameFlag(false)
                .build();
    }

    private boolean canContinuePlayOnPreWheel(GamePlayDo gamePlay, RewardResultDo<MangkukulamSymbolDo> rewardResult, AtomicLong totalRewardAmount) {
        if (Objects.isNull(rewardResult)) {
            return false;
        }
        if (Objects.equals(rewardResult.getRewardType(), RewardTypeEnum.NONE)) {
            return false;
        }
        if (rewardResult.getAccumulateReachMaxMultiple()) {
            log.warn("reach max reward multiple bet. totalRewardAmount: {}, payAmount: {}", totalRewardAmount.get(), gamePlay.getPayAmount());
            return false;
        }
        return CollectionUtils.isNotEmpty(rewardResult.getRewardLineDetailList());
    }


    /**
     * 增加免费轮次数
     */
    private void addFreeWheelTimes(LinkedList<FreeWheelHandleDo> freeWheelList, int addTimes, Long parentGameRecordId) {
        if (addTimes < 1) {
            return;
        }
        for (int times = 1; times <= addTimes; times++) {
            freeWheelList.addLast(FreeWheelHandleDo.builder()
                    .parentGameRecordId(parentGameRecordId)
                    .build());
        }
    }

    private void setLastGameFlag(List<MangkukulamGameRecordDo> gameRecordResult) {
        if (CollectionUtils.isNotEmpty(gameRecordResult)) {
            gameRecordResult.get(gameRecordResult.size() - 1).setLastGameFlag(true);
        }
    }

    @Data
    @SuperBuilder
    private static class FreeWheelHandleDo {
        private long parentGameRecordId;
    }
}