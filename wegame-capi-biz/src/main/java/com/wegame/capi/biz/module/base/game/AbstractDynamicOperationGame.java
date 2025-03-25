//package com.wegame.capi.biz.module.base.game;
//
//import com.wegame.capi.biz.converter.ToVoConverter;
//import com.wegame.capi.biz.domain.biz.game.GameEventDo;
//import com.wegame.capi.biz.domain.biz.game.GameMultipleRecordDo;
//import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
//import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
//import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
//import org.apache.commons.collections4.CollectionUtils;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//
//@Component
//public abstract class AbstractDynamicOperationGame<R extends ToVoConverter,WD extends SymbolDo> extends AbstractGame<R> {
//    private static final Long GAME_RECORD_DEFAULT_PARENT_ID = 0L;
//
//    public List<R> play(GamePlayDo gamePlay) {
//        GameEventDo gameEventDo = gamePlay.getGameEventDo();
//        if (gameEventDo == null || !gameEventDo.getEventResult()) {
//            return this.normalPlay(gamePlay);
//        } else {
//            return this.eventPlay(gamePlay, gameEventDo.getRecordDoList());
//        }
//    }
//
//    public abstract List<R> normalPlay(GamePlayDo gamePlay);
//
//
//    public abstract List<R> eventPlay(GamePlayDo gamePlay, List<GameMultipleRecordDo<WD>> recordDoList);
//
//
//
//
//
//
//}
//
//
//
//
//
//
//
