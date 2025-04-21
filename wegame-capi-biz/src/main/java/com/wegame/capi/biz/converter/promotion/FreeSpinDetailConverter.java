package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.FreeSpinDetailRes;
import com.wegame.capi.biz.domain.biz.promotion.FreeSpinDetailDo;

import java.util.Objects;

public class FreeSpinDetailConverter {

    // FreeSpinDetailRes to FreeSpinDetailDo
    public static FreeSpinDetailDo toDo(FreeSpinDetailRes freeSpinDetailRes) {
        return FreeSpinDetailDo.builder()
                .refId(freeSpinDetailRes.getRefId())
                .config(toDo(freeSpinDetailRes.getConfig()))
                .build();
    }
    private static FreeSpinDetailDo.Config toDo(FreeSpinDetailRes.Config config) {
        if (Objects.isNull(config)) {
            return null;
        }
        return FreeSpinDetailDo.Config.builder()
                .campaignId(config.getCampaignId())
                .name(config.getName())
                .pId(config.getPId())
                .channelId(config.getChannelId())
                .status(config.getStatus())
                .maxPlayer(config.getMaxPlayer())
                .betValue(config.getBetValue())
                .totalFreeSpin(config.getTotalFreeSpin())
                .startDate(config.getStartDate())
                .endDate(config.getEndDate())
                .createTime(config.getCreateTime())
                .build();
    }
}
