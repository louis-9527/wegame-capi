package com.wegame.capi.biz.module.base.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.converter.ToVoConverter;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public abstract class AbstractDynamicOperationGame<R extends ToVoConverter> extends AbstractGame<R> {
    @Autowired
    private DynamicOperationEventService dynamicOperationEventService;
    @Autowired
    private DynamicOperationManager dynamicOperationManager;

    private static final Long GAME_RECORD_DEFAULT_PARENT_ID = 0L;

    public List<R> play(GamePlayDo gamePlay) {
        List wheelDos = gamePlay.getGameEventDo().getWheelDos();
        if (wheelDos != null) {
            return this.eventPlay(gamePlay, wheelDos);
        } else {
            return this.normalPlay(gamePlay);
        }
    }

    /**
     * 获取需要运营的金额,并执行预扣,加锁处理
     *
     * @return 返回null, 则不需要运营
     */
    private List<R> eventPlay(GamePlayDo gamePlay, List<WheelDo<SymbolDo>> wheelDos) {
        //计算派奖金额
        long totalPayOutAmount = 0L;
        List<R> resultList = new ArrayList<>();
        long firstGameRecordId = generateGameRecordId();
        RewardResultDo<SymbolDo> rewardResultDo = rewardHandler().handle(RewardHandlerBuilder.build(this.getGameInfo(), gamePlay, wheelDos.get(0), BigDecimal.ZERO));
        totalPayOutAmount = totalPayOutAmount + rewardResultDo.getTotalRewardAmount();
        if (wheelDos.size() == 1) {
            //正常投注
            resultList.add(this.buildGameRecord(firstGameRecordId,
                    GAME_RECORD_DEFAULT_PARENT_ID,
                    gamePlay.getBetRecordId(),
                    0L,
                    wheelDos.get(0),
                    rewardResultDo,
                    null));
            return resultList;
        } else {
            //免费轮
            List<Long> freeWheelGameRecordIds = getGameRecordIdList(wheelDos.size() - 1);
            resultList.add(this.buildGameRecord(firstGameRecordId,
                    GAME_RECORD_DEFAULT_PARENT_ID,
                    gamePlay.getBetRecordId(),
                    0L,
                    wheelDos.get(0),
                    rewardResultDo,
                    freeWheelGameRecordIds));

            for (int i = 1; i <= wheelDos.size(); i++) {
                RewardResultDo<SymbolDo> symbolDoRewardResultDo = rewardHandler().handle(RewardHandlerBuilder.build(this.getGameInfo(), gamePlay, wheelDos.get(i), BigDecimal.ZERO));
                totalPayOutAmount = totalPayOutAmount + rewardResultDo.getTotalRewardAmount();
                resultList.add(this.buildGameRecord(freeWheelGameRecordIds.get(i - 1),
                        GAME_RECORD_DEFAULT_PARENT_ID,
                        gamePlay.getBetRecordId(),
                        0L,
                        wheelDos.get(i),
                        symbolDoRewardResultDo,
                        null));

            }

        }

        return resultList;


    }

    public abstract RewardHandler rewardHandler();

    public abstract List<R> normalPlay(GamePlayDo gamePlay);

    public abstract DynamicOperationEventKindEnum eventKindEnum(BetTypeEnum betType);

    public abstract TreeMap<Integer, List<List<WheelDo<SymbolDo>>>> normalPayMultipleConfig();

    public abstract TreeMap<Integer, List<List<WheelDo<SymbolDo>>>> freePayMultipleConfig();


    public abstract R buildGameRecord(Long id,
                                      Long parentId,
                                      Long betRecordId,
                                      Long batchId,
                                      WheelDo<SymbolDo> wheel,
                                      RewardResultDo<SymbolDo> rewardResult,
                                      List<Long> freeSpinGameRecordIds);





}







