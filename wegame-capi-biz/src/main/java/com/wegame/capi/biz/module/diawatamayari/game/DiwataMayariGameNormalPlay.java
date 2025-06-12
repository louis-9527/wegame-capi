package com.wegame.capi.biz.module.diawatamayari.game;

import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.domain.biz.game.DiwataMayariGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.diawatamayari.DiwataMayariGameHelper;
import com.wegame.capi.biz.module.diawatamayari.reward.DiwataMayariRewardHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component("diwataMayariGameNormalPlay")
public class DiwataMayariGameNormalPlay extends AbstractGame<DiwataMayariGameRecordDo> {

    private static final int MAX_SCATTER_NUMBER_IN_REEL = 2;
    private static final int MAX_SCATTER_NUMBER_IN_WHEEL = 6;
    private static final Integer WHEEL_DEFAULT_INDEX = 0;
    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo GAME_INFO = DiwataMayariGameHelper.DIWATA_MAYARI_GAME_INFO;

    @Autowired
    MalakasConfig malakasConfig;
    @Autowired
    DiwataMayariRewardHandler diwataMayariRewardHandler;

    @Override
    public List<DiwataMayariGameRecordDo> play(GamePlayDo gamePlay) {
        // todo
        throw new RuntimeException("play todo");
    }
}