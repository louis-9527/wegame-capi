package com.wegame.capi.biz.module.bakunawa.game;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.BakunawaGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.bakunawa.reward.BakunawaRewardHandler;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.service.operation.OperationEventConfigRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static com.wegame.capi.biz.builder.RewardHandlerBuilder.build;
import static com.wegame.capi.biz.module.bakunawa.BakunawaGameHelper.BAKUNAWA_GAME_INFO;
import static com.wegame.capi.client.enums.OperationEventTypeEnum.BAKUNAUWA;

@Slf4j
@Component("bakunawaGameEventPlay")
public class BakunawaGameEventPlay extends AbstractGame<BakunawaGameRecordDo> {

    private static final GameInfoDo GAME_INFO = BAKUNAWA_GAME_INFO;
    @Autowired
    BakunawaRewardHandler bakunawaRewardHandler;
    @Autowired
    OperationEventConfigRecordService operationEventConfigRecordService;

    @Override
    public List<BakunawaGameRecordDo> play(GamePlayDo gamePlay) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        List<OperationEventConfigRecordDo> configRecordList = operationEventConfigRecordService.listByBetRecordId(gamePlay.getOperationEventConfigBetRecordId(), BAKUNAUWA);
        AssertUtil.assertNotEmpty(configRecordList, ErrorCode.ERR_INTER, "configRecordList is empty");
        OperationEventConfigRecordDo configRecordDo = configRecordList.get(0);
        AssertUtil.assertNotNull(configRecordDo.getExtInfo(), ErrorCode.ERR_INTER, "extInfo is null");
        AssertUtil.assertTrue(NumberUtil.isNumber(configRecordDo.getExtInfo().toString()), ErrorCode.ERR_INTER, "extInfo is not number");
        WheelDo<SymbolDo> wheel = convertWheel(configRecordDo.getWheel());
        RewardResultDo<SymbolDo> firstWheelRewardResult = bakunawaRewardHandler.handle(build(GAME_INFO, gamePlay, wheel, new BigDecimal(configRecordDo.getExtInfo().toString())));
        long overallRewardAmount = firstWheelRewardResult.getTotalRewardAmount();
        return Lists.newArrayList(this.buildGameRecord(userInfo, generateGameRecordId(), gamePlay.getBetRecordId(), wheel, firstWheelRewardResult, overallRewardAmount));
    }

    private BakunawaGameRecordDo buildGameRecord(UserInfo userInfo,
                                                 Long gameRecordId,
                                                 Long betRecordId,
                                                 WheelDo<SymbolDo> wheel,
                                                 RewardResultDo<SymbolDo> rewardResult,
                                                 Long TotalRewardAmount) {
        return BakunawaGameRecordDo.builder()
                .id(gameRecordId)
                .betRecordId(betRecordId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .overallRewardAmount(TotalRewardAmount)
                .build();
    }

    private WheelDo<SymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<SymbolDo>>() {
        });
    }
}
