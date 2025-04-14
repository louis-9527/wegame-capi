package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.FreeSpinOfPlayerRes;
import com.wegame.capi.biz.domain.biz.promotion.FreeSpinOfPlayerDo;
import com.wegame.capi.client.domain.vo.promotion.FreeSpinOfPlayerVo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FreeSpinConverter {

    // FreeSpinOfPlayerRes to FreeSpinOfPlayerDo
    public static FreeSpinOfPlayerDo toDo(FreeSpinOfPlayerRes freeSpinOfPlayerRes) {
        return FreeSpinOfPlayerDo.builder()
                .playerName(freeSpinOfPlayerRes.getPlayerName())
                .refId(freeSpinOfPlayerRes.getRefId())
                .current(toDoList(freeSpinOfPlayerRes.getCurrent()))
                .others(toDoList(freeSpinOfPlayerRes.getOthers()))
                .build();
    }
    private static List<FreeSpinOfPlayerDo.FreeSpin> toDoList(List<FreeSpinOfPlayerRes.FreeSpin> freeSpinList) {
        return Optional.ofNullable(freeSpinList)
                .orElse(Collections.emptyList())
                .stream()
                .map(FreeSpinConverter::toDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    private static FreeSpinOfPlayerDo.FreeSpin toDo(FreeSpinOfPlayerRes.FreeSpin freeSpin) {
        if (Objects.isNull(freeSpin)) {
            return null;
        }
        return FreeSpinOfPlayerDo.FreeSpin.builder()
                .channelId(freeSpin.getChannelId())
                .campaignId(freeSpin.getCampaignId())
                .initCount(freeSpin.getInitCount())
                .remainingCount(freeSpin.getRemainingCount())
                .expireDate(freeSpin.getExpireDate())
                .gameId(freeSpin.getGameId())
                .gameIdRef(freeSpin.getGameIdRef())
                .gameName(freeSpin.getGameName())
                .build();
    }

    // FreeSpinOfPlayerDo to FreeSpinOfPlayerVo
    public static FreeSpinOfPlayerVo toVo(FreeSpinOfPlayerDo freeSpinOfPlayerDo) {
        return FreeSpinOfPlayerVo.builder()
                .playerName(freeSpinOfPlayerDo.getPlayerName())
                .refId(freeSpinOfPlayerDo.getRefId())
                .current(toVoList(freeSpinOfPlayerDo.getCurrent()))
                .others(toVoList(freeSpinOfPlayerDo.getOthers()))
                .build();
    }
    private static List<FreeSpinOfPlayerVo.FreeSpin> toVoList(List<FreeSpinOfPlayerDo.FreeSpin> freeSpinList) {
        return Optional.ofNullable(freeSpinList)
                .orElse(Collections.emptyList())
                .stream()
                .map(FreeSpinConverter::toVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    private static FreeSpinOfPlayerVo.FreeSpin toVo(FreeSpinOfPlayerDo.FreeSpin freeSpin) {
        if (Objects.isNull(freeSpin)) {
            return null;
        }
        return FreeSpinOfPlayerVo.FreeSpin.builder()
                .channelId(freeSpin.getChannelId())
                .campaignId(freeSpin.getCampaignId())
                .initCount(freeSpin.getInitCount())
                .remainingCount(freeSpin.getRemainingCount())
                .expireDate(freeSpin.getExpireDate())
                .gameId(freeSpin.getGameId())
                .gameIdRef(freeSpin.getGameIdRef())
                .gameName(freeSpin.getGameName())
                .build();
    }
}
