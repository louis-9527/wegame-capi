package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.FreeSpinHistoryOfPlayerRes;
import com.wegame.capi.biz.domain.biz.promotion.FreeSpinHistoryOfPlayerDo;
import com.wegame.capi.client.domain.vo.promotion.FreeSpinHistoryOfPlayerVo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class HistoryFreeSpinConverter {

    // FreeSpinHistoryOfPlayerRes to FreeSpinHistoryOfPlayerDo
    public static FreeSpinHistoryOfPlayerDo toDo(FreeSpinHistoryOfPlayerRes freeSpinOfPlayerRes) {
        return FreeSpinHistoryOfPlayerDo.builder()
                .playerName(freeSpinOfPlayerRes.getPlayerName())
                .refId(freeSpinOfPlayerRes.getRefId())
                .freeSpin(toDoList(freeSpinOfPlayerRes.getFreeSpin()))
                .totalPages(freeSpinOfPlayerRes.getTotalPages())
                .page(freeSpinOfPlayerRes.getPage())
                .build();
    }
    private static List<FreeSpinHistoryOfPlayerDo.FreeSpin> toDoList(List<FreeSpinHistoryOfPlayerRes.FreeSpin> freeSpinList) {
        return Optional.ofNullable(freeSpinList)
                .orElse(Collections.emptyList())
                .stream()
                .map(HistoryFreeSpinConverter::toDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    private static FreeSpinHistoryOfPlayerDo.FreeSpin toDo(FreeSpinHistoryOfPlayerRes.FreeSpin freeSpin) {
        if (Objects.isNull(freeSpin)) {
            return null;
        }
        return FreeSpinHistoryOfPlayerDo.FreeSpin.builder()
                .campaignId(freeSpin.getCampaignId())
                .initCount(freeSpin.getInitCount())
                .remainingCount(freeSpin.getRemainingCount())
                .gameId(freeSpin.getGameId())
                .gameName(freeSpin.getGameName())
                .totalWin(freeSpin.getTotalWin())
                .firstUsedTime(freeSpin.getFirstUsedTime())
                .build();
    }

    // FreeSpinHistoryOfPlayerVo to FreeSpinOfPlayerVo
    public static FreeSpinHistoryOfPlayerVo toVo(FreeSpinHistoryOfPlayerDo freeSpinHistoryOfPlayerDo) {
        return FreeSpinHistoryOfPlayerVo.builder()
                .playerName(freeSpinHistoryOfPlayerDo.getPlayerName())
                .refId(freeSpinHistoryOfPlayerDo.getRefId())
                .freeSpin(toVoList(freeSpinHistoryOfPlayerDo.getFreeSpin()))
                .totalPages(freeSpinHistoryOfPlayerDo.getTotalPages())
                .page(freeSpinHistoryOfPlayerDo.getPage())
                .build();
    }
    private static List<FreeSpinHistoryOfPlayerVo.FreeSpin> toVoList(List<FreeSpinHistoryOfPlayerDo.FreeSpin> freeSpinList) {
        return Optional.ofNullable(freeSpinList)
                .orElse(Collections.emptyList())
                .stream()
                .map(HistoryFreeSpinConverter::toVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    private static FreeSpinHistoryOfPlayerVo.FreeSpin toVo(FreeSpinHistoryOfPlayerDo.FreeSpin freeSpin) {
        if (Objects.isNull(freeSpin)) {
            return null;
        }
        return FreeSpinHistoryOfPlayerVo.FreeSpin.builder()
                .campaignId(freeSpin.getCampaignId())
                .initCount(freeSpin.getInitCount())
                .remainingCount(freeSpin.getRemainingCount())
                .gameId(freeSpin.getGameId())
                .gameName(freeSpin.getGameName())
                .totalWin(freeSpin.getTotalWin())
                .firstUsedTime(freeSpin.getFirstUsedTime())
                .build();
    }
}
