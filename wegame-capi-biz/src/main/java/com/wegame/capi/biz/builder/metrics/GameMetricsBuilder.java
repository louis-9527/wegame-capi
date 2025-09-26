package com.wegame.capi.biz.builder.metrics;


import com.wegame.capi.biz.domain.metrics.GameMetricsDo;
import com.wegame.capi.client.enums.innerdos.GameInnerDosTypeEnum;
import com.wegame.capi.common.utils.RtpUtil;


public class GameMetricsBuilder {

    public static GameMetricsDo buildDefault(String pId, String channelId, String gameId) {
        return GameMetricsDo.builder()
                .pId(pId)
                .channelId(channelId)
                .gameId(gameId)
                .currentRtp(RtpUtil.calculateRtp(0L, 0L))
                .totalBetAmount(0L)
                .totalPayoutAmount(0L)
                .version(0L)
                .inUseNormalDosType(GameInnerDosTypeEnum.NORMAL_DOS.getCode())
                .build();
    }

}
