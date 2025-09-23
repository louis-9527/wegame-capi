package com.wegame.capi.test.biz.innerdos.metrics;

import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.metrics.MetricsCacheKeyDo;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.constants.MetricsConstants;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;

public class MetricsManagerTestMocker {
    public static final MetricsCacheKeyDo metricsCacheKeyDo_normal = MetricsCacheKeyDo.builder()
            .pId("50")
            .channelId("1050")
            .playerId("test001")
            .playerName("test001")
            .build();
    public static final SingleWalletBetResultDo success_betResult = SingleWalletBetResultDo.builder()
            .betStatus(BetStatusEnum.SUCCESS)
            .build();

    public static BetRecordDo betRecordDo_normal(GameTypeEnum gameType) {
        BetRecordDo<Object> betRecordDo = BetRecordDo.builder()
                .pId(metricsCacheKeyDo_normal.getPId())
                .channelId(metricsCacheKeyDo_normal.getChannelId())
                .playerId(metricsCacheKeyDo_normal.getPlayerId())
                .playerName(metricsCacheKeyDo_normal.getPlayerName())
                .gameId(gameType.getId())
                .betAmount(10000L)
                .payAmount(10000L)
                .payOutAmount(10000L)
                .betId("testBetId00001")
                .betPromotionType(BetPromotionTypeEnum.OPERATION_EVENT.getCode())
                .betSerialNo("testBetSerialNo001")
                .betSubType(BetSubTypeEnum.NORMAL_BET.getCode())
                .betTime(System.currentTimeMillis())
                .betType(BetTypeEnum.NORMAL_BET.getCode())
                .creator("creator")
                .createTime(System.currentTimeMillis()).build();
        betRecordDo.addExtInfoMapValue(MetricsConstants.EXT_MAP_KEY_GAME_PLAY_TYPE, GamePlayTypeEnum.TEMPLATE_PLAY.getCode());
        return betRecordDo;
    }

    public static BetRecordDo betRecordDo_cannotMetrics(GameTypeEnum gameType) {
        BetRecordDo<Object> betRecordDo = BetRecordDo.builder()
                .pId(metricsCacheKeyDo_normal.getPId())
                .channelId(metricsCacheKeyDo_normal.getChannelId())
                .playerId(metricsCacheKeyDo_normal.getPlayerId())
                .playerName(metricsCacheKeyDo_normal.getPlayerName())
                .gameId(gameType.getId())
                .betAmount(11000L)
                .payAmount(11000L)
                .payOutAmount(0L)
                .betId("testBetId00001")
                .betPromotionType(BetPromotionTypeEnum.OPERATION_EVENT.getCode())
                .betSerialNo("testBetSerialNo001")
                .betSubType(BetSubTypeEnum.NORMAL_BET.getCode())
                .betTime(System.currentTimeMillis())
                .betType(BetTypeEnum.NORMAL_BET.getCode())
                .creator("creator")
                .createTime(System.currentTimeMillis()).build();
        betRecordDo.addExtInfoMapValue(MetricsConstants.EXT_MAP_KEY_GAME_PLAY_TYPE, GamePlayTypeEnum.NORMAL_PLAY.getCode());
        return betRecordDo;
    }

}
