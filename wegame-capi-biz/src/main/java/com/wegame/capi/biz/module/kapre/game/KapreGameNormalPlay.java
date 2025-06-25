package com.wegame.capi.biz.module.kapre.game;

import com.wegame.capi.biz.config.domain.KapreConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.KapreGameRecordDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.kapre.KapreGameHelper;
import com.wegame.capi.biz.module.kapre.reward.KapreRewardHandler;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component("kapreGameNormalPlay")
public class KapreGameNormalPlay extends AbstractGame<KapreGameRecordDo> {

    private static final int MAX_SCATTER_NUMBER_IN_REEL = 2;
    private static final int MAX_SCATTER_NUMBER_IN_WHEEL = 6;
    private static final Integer WHEEL_DEFAULT_INDEX = 0;
    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo GAME_INFO = KapreGameHelper.KAPRE_GAME_INFO;

    @Autowired
    KapreConfig kapreConfig;
    @Autowired
    KapreRewardHandler kapreRewardHandler;

    @Override
    public List<KapreGameRecordDo> play(GamePlayDo gamePlay) {
       throw new RuntimeException("todo");
    }

    @Data
    @SuperBuilder
    private static class FreeWheelHandleDo {
        private long parentGameRecordId;
    }
}