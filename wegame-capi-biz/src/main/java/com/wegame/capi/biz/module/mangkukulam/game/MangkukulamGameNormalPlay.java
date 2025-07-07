package com.wegame.capi.biz.module.mangkukulam.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.MangkukulamConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper.MANGKUKULAM_GAME_INFO;

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
        return gameRecordResult;
    }

    private void generateFirstBatch(GamePlayDo gamePlay) {

    }

    private WheelDo<MangkukulamSymbolDo> generateFirstWheel() {
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
        freeWheelList.add(FreeWheelHandleDo.builder()
                .freeWheelTimes(context.getFreeWheelTimes())
                .parentGameRecordId(context.getParentGameRecordId())
                .build());
        totalRewardAmount.getAndAdd(context.getTotalPayoutAmount());
    }


    @Data
    @SuperBuilder
    private static class FreeWheelHandleDo {
        private long parentGameRecordId;
        private int freeWheelTimes;
    }
}