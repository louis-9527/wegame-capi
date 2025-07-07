package com.wegame.capi.biz.module.mangkukulam.game;

import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
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


    @Override
    public List<MangkukulamGameRecordDo> play(GamePlayDo gamePlay) {
        List<MangkukulamGameRecordDo> gameRecordResult = new ArrayList<>();
        LinkedList<FreeWheelHandleDo> freeWheelList = new LinkedList<>();
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        // todo
        return gameRecordResult;
    }


    @Data
    @SuperBuilder
    private static class FreeWheelHandleDo {
        private long parentGameRecordId;
        private int freeWheelTimes;
    }
}