package com.wegame.capi.biz.module.ibongadarna.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.IbongAdarnaGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.IbongAdarnaSymbolDo;
import com.wegame.capi.biz.domain.biz.template.ibong.IbongAdarnaBetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.innerdos.decider.InnerDosDecideResult;
import com.wegame.capi.biz.innerdos.decider.InnerDosDeciderManager;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.ibongadarna.IbongAdarnaGameHelper;
import com.wegame.capi.biz.module.ibongadarna.reward.IbongAdarnaRewardHandler;
import com.wegame.capi.biz.service.template.es.BetRecordTemplateEsService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.wegame.capi.client.enums.TemplateTypeEnum.IBONG_ADARNA;
import static com.wegame.capi.common.Constants.IBONG_ADARNA_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Slf4j
@Component("ibongAdarnaGameTemplatePlay")
@ConditionalOnProperty(name = IBONG_ADARNA_PROP, havingValue = TRUE_VAL)
public class IbongAdarnaGameTemplatePlay extends AbstractGame<IbongAdarnaGameRecordDo> {

    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo gameInfo = IbongAdarnaGameHelper.IBONG_ADARNA_GAME_INFO;

    @Autowired
    IbongAdarnaRewardHandler ibongAdarnaRewardHandler;
    @Autowired
    IbongAdarnaConfig ibongAdarnaConfig;
    @Autowired
    BetRecordTemplateEsService<IbongAdarnaBetRecordTemplateEsDo> betRecordTemplateEsService;
    @Autowired
    private InnerDosDeciderManager innerDosDeciderManager;

    @Override
    public List<IbongAdarnaGameRecordDo> play(GamePlayDo gamePlay) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        // 内部干预决策逻辑
        TemplateMultipleRange templateMultipleRange = this.getTemplateMultipleRange(gamePlay);

        List<Long> betRecordIdList = betRecordTemplateEsService.listIdByMultipleRange(IBONG_ADARNA, gamePlay.getBetType(), templateMultipleRange.getStart(), templateMultipleRange.getEnd(), IbongAdarnaBetRecordTemplateEsDo.class);
        // todo ibong 模版玩法逻辑
        List<IbongAdarnaGameRecordDo> ret = new ArrayList<>();

        return ret;
    }


    private IbongAdarnaGameRecordDo buildGameRecord(Long id,
                                                    Long parentId,
                                                    Long betRecordId,
                                                    Long batchId,
                                                    WheelDo<IbongAdarnaSymbolDo> wheel,
                                                    RewardResultDo<IbongAdarnaSymbolDo> rewardResult, boolean lastGameFlag) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return IbongAdarnaGameRecordDo.builder()
                .id(id)
                .betRecordId(betRecordId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .build();
    }

    private TemplateMultipleRange getTemplateMultipleRange(GamePlayDo gamePlay) {
        // inner dos 决策
        InnerDosDecideResult decideResult = innerDosDeciderManager.decide(gamePlay.getGameType(), gamePlay);
        AssertUtil.assertNotNull(decideResult, ErrorCode.ERR_INTER, "inner dos decide result is null");
        String templateIdRange = decideResult.getTemplateIdRange();
        AssertUtil.assertStrNotEmpty(templateIdRange, ErrorCode.ERR_INTER);
        return TemplateMultipleRange.build(templateIdRange);
    }
}