package com.wegame.capi.biz.module.ibongadarna.game;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.IbongAdarnaGameRecordDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.IbongAdarnaSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.biz.service.operation.OperationEventConfigRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.wegame.capi.biz.builder.RewardHandlerBuilder.build;
import static com.wegame.capi.biz.module.ibongadarna.IbongAdarnaGameHelper.IBONG_ADARNA_GAME_INFO;
import static com.wegame.capi.client.enums.OperationEventTypeEnum.IBONG_ADARNA;

@Slf4j
@Component("ibongAdarnaGameEventPlay")
public class IbongAdarnaGameEventPlay extends AbstractGame<IbongAdarnaGameRecordDo> {

    private static final GameInfoDo GAME_INFO = IBONG_ADARNA_GAME_INFO;

    @Autowired
    RewardHandler<IbongAdarnaSymbolDo> ibongAdarnaRewardHandler;
    @Autowired
    OperationEventConfigRecordService operationEventConfigRecordService;

    @Override
    public List<IbongAdarnaGameRecordDo> play(GamePlayDo gamePlay) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        List<OperationEventConfigRecordDo> configRecordList = operationEventConfigRecordService.listByBetRecordId(gamePlay.getOperationEventConfigBetRecordId(), IBONG_ADARNA);
        AssertUtil.assertNotEmpty(configRecordList, ErrorCode.ERR_INTER, "configRecordList is empty");
        OperationEventConfigRecordDo configRecordDo = configRecordList.get(0);
        AssertUtil.assertNotNull(configRecordDo.getExtInfo(), ErrorCode.ERR_INTER, "extInfo is null");
        AssertUtil.assertTrue(NumberUtil.isNumber(configRecordDo.getExtInfo().toString()), ErrorCode.ERR_INTER, "extInfo is not number");
        WheelDo<IbongAdarnaSymbolDo> wheel = convertWheel(configRecordDo.getWheel());
        RewardResultDo<IbongAdarnaSymbolDo> firstWheelRewardResult = ibongAdarnaRewardHandler.handle(build(GAME_INFO, gamePlay, wheel, new BigDecimal(configRecordDo.getExtInfo().toString())));
        long overallRewardAmount = firstWheelRewardResult.getTotalRewardAmount();
        List<IbongAdarnaGameRecordDo> ret = Lists.newArrayList(this.buildGameRecord(userInfo, generateGameRecordId(), gamePlay.getBetRecordId(), wheel, firstWheelRewardResult, overallRewardAmount));
        gamePlay.setOverallRewardAmount(overallRewardAmount);
        return ret;
    }

    private IbongAdarnaGameRecordDo buildGameRecord(UserInfo userInfo,
                                                    Long gameRecordId,
                                                    Long betRecordId,
                                                    WheelDo<IbongAdarnaSymbolDo> wheel,
                                                    RewardResultDo<IbongAdarnaSymbolDo> rewardResult,
                                                    Long TotalRewardAmount) {
        return IbongAdarnaGameRecordDo.builder()
                .id(gameRecordId)
                .betRecordId(betRecordId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .overallRewardAmount(TotalRewardAmount)
                .build();
    }

    private WheelDo<IbongAdarnaSymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<IbongAdarnaSymbolDo>>() {
        });
    }
}
