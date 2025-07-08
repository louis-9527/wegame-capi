package com.wegame.capi.biz.module.mangkukulam.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.MangkukulamConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
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

    private static final GameInfoDo GAME_INFO = MANGKUKULAM_GAME_INFO;

    // todo 删除
    @Autowired(required = false)
    MangkukulamConfig mangkukulamConfig;

    @Override
    public List<MangkukulamGameRecordDo> play(GamePlayDo gamePlay) {
        List<MangkukulamGameRecordDo> gameRecordResult = new ArrayList<>();
        LinkedList<FreeWheelHandleDo> freeWheelList = new LinkedList<>();
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        // todo 执行
        if (BetSubTypeEnum.isFirstStep(gamePlay.getBetSubType().getCode()) && Objects.nonNull(gamePlay.getVolatility())) {
            // 执行完普通轮和免费轮
            generateFirstBatch(gameRecordResult, totalRewardAmount, freeWheelList);
            generateFreeBatch(gameRecordResult, totalRewardAmount, freeWheelList);
            setLastGameFlag(gameRecordResult);
        } else if (BetSubTypeEnum.isFirstStep(gamePlay.getBetSubType().getCode())) {
            // 执行首轮
            generateFirstBatch(gameRecordResult, totalRewardAmount, freeWheelList);
            MangkukulamGameRecordDo lastGame = gameRecordResult.get(gameRecordResult.size() - 1);
            if (Objects.nonNull(lastGame) && !lastGame.getRewardResult().isHasRewardFreeWheel()) {
                setLastGameFlag(gameRecordResult);
            }
        } else if (Objects.equals(BetSubTypeEnum.FREE_WHEEL_NORMAL_BET.getCode(), gamePlay.getBetSubType().getCode()) && Objects.nonNull(gamePlay.getVolatility())) {
            // 执行免费轮
            AssertUtil.assertNotNull(gamePlay.getContinuePlayContext(), ErrorCode.ERR_INTER);
            loadContinuePlayContext(gamePlay, freeWheelList, totalRewardAmount);
            generateFreeBatch(gameRecordResult, totalRewardAmount, freeWheelList);
            setLastGameFlag(gameRecordResult);
        }
        return gameRecordResult;
    }

    /**
     * 生成首轮
     */
    private void generateFirstBatch(List<MangkukulamGameRecordDo> result, AtomicLong totalRewardAmount, LinkedList<FreeWheelHandleDo> freeWheelList) {
        // scatter & wild 不会跟特殊wild在同一列
        // 特殊wild只会在2 3 4 列
        // wild & 火框 只会在 2 3 4 5 列
    }

    /**
     * 生成免费轮
     */
    private void generateFreeBatch(List<MangkukulamGameRecordDo> result, AtomicLong totalRewardAmount, LinkedList<FreeWheelHandleDo> freeWheelList) {
        while (!freeWheelList.isEmpty()) {
            FreeWheelHandleDo freeWheel = freeWheelList.removeFirst();
        }
    }

    private WheelDo<MangkukulamSymbolDo> generateFirstWheel() {
        // 0. 初始化盘面，填充占位符
        MangkukulamSymbolDo[][] symbolArr = initWheel();
        // 1. 轮带表生成初始盘面
        // 2. 生成火框
        // 3. 生成特殊wild倍率
        return null;
    }

    private WheelDo<MangkukulamSymbolDo> generateFromPreWheel(MangkukulamGameRecordDo preGameRecord) {
        //0. 准备阶段 复制盘面
        //1. 消除中奖元素
        //2. 生成
        return null;
    }

    private void loadContinuePlayContext(GamePlayDo gamePlay, LinkedList<FreeWheelHandleDo> freeWheelList, AtomicLong totalRewardAmount) {
        AssertUtil.assertNotNull(gamePlay.getContinuePlayContext(), ErrorCode.ERR_INTER);
        GamePlayDo.ContinuePlayContext context = gamePlay.getContinuePlayContext();
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