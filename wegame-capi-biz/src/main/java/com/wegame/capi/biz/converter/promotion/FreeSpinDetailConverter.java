package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.FreeSpinDetailRes;
import com.wegame.capi.biz.domain.biz.promotion.FreeSpinDetailDo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FreeSpinDetailConverter {

    // FreeSpinDetailRes to FreeSpinDetailDo
    public static FreeSpinDetailDo toDo(FreeSpinDetailRes freeSpinDetailRes) {
        return FreeSpinDetailDo.builder()
                .refId(freeSpinDetailRes.getRefId())
                .config(toDoList(freeSpinDetailRes.getConfig()))
                .build();
    }
    private static List<FreeSpinDetailDo.Config> toDoList(List<FreeSpinDetailRes.Config> configList) {
        return Optional.ofNullable(configList)
                .orElse(Collections.emptyList())
                .stream()
                .map(FreeSpinDetailConverter::toDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
