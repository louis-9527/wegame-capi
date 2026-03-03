package com.wegame.capi.biz.module.bakunawa.game;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.game.BakunawaGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.bakunawa.reward.BakunawaRewardHandler;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.service.operation.OperationEventConfigRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.enums.MultiplePieTypeEnum;
import com.wegame.capi.common.utils.TtlUtil;
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
        AssertUtil.assertNotNull(configRecordDo.getExtInfo(), ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(JSONUtil.isTypeJSON(configRecordDo.getExtInfo().toString()), ErrorCode.ERR_PARAM);
        WheelDo<SymbolDo> wheel = convertWheel(configRecordDo.getWheel());
        RewardHandleDo<SymbolDo> rewardHandleDo = build(GAME_INFO, gamePlay, wheel, BigDecimal.ZERO);

        JSONObject multipleJsonObject = JSON.parseObject(JSON.toJSONString(configRecordDo.getExtInfo()));
        MultiplePieTypeEnum multiplePieType = MultiplePieTypeEnum.valueOf(multipleJsonObject.getString("multiplePieType"));
        AssertUtil.assertNotNull(multiplePieType, ErrorCode.ERR_PARAM);
        rewardHandleDo.setMultiplePieType(multiplePieType);
        rewardHandleDo.setCurrentMultiple(new BigDecimal(multipleJsonObject.getString("multiplePie")));
        RewardResultDo<SymbolDo> rewardResult = bakunawaRewardHandler.handle(rewardHandleDo);
        long overallRewardAmount = rewardResult.getTotalRewardAmount();
        gamePlay.setOverallRewardAmount(overallRewardAmount);
        return Lists.newArrayList(this.buildGameRecord(userInfo, generateGameRecordId(), gamePlay.getBetRecordId(), wheel, rewardResult, overallRewardAmount));
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
                .creator(userInfo.getPlayerId())
                .createTime(DateUtil.getCurrentMilli())
                .updater(userInfo.getPlayerId())
                .updateTime(DateUtil.getCurrentMilli())
                .expireAt(TtlUtil.calculateExpiredAt(DateUtil.getCurrentMilli()))
                .build();
    }

    private WheelDo<SymbolDo> convertWheel(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<SymbolDo>>() {
        });
    }
}
