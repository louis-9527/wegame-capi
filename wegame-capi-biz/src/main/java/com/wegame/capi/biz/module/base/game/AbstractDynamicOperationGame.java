package com.wegame.capi.biz.module.base.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.game.GameEventDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.manager.DynamicOperationManager;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.biz.service.DynamicOperationEventService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationEventKindEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationReportStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public abstract class AbstractDynamicOperationGame<R extends ToVoConverter> extends AbstractGame<R> {
    private static final Long GAME_RECORD_DEFAULT_PARENT_ID = 0L;

    public List<R> play(GamePlayDo gamePlay) {
        GameEventDo gameEventDo = gamePlay.getGameEventDo();
        if (gameEventDo == null || CollectionUtils.isEmpty(gameEventDo.getWheelDos())) {
            return this.normalPlay(gamePlay);
        } else {
            return this.eventPlay(gamePlay, gameEventDo.getWheelDos());
        }
    }

    public abstract List<R> normalPlay(GamePlayDo gamePlay);


    public abstract List<R> eventPlay(GamePlayDo gamePlay, List<WheelDo<SymbolDo>> wheelDos);






}







