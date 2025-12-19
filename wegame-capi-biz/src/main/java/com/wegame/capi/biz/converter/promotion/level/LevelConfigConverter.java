package com.wegame.capi.biz.converter.promotion.level;

import com.wegame.capi.api.domain.res.promotion.RedeemTreasureRes;
import com.wegame.capi.api.domain.res.promotion.level.LevelConfigRes;
import com.wegame.capi.biz.converter.promotion.AchievementConverter;
import com.wegame.capi.biz.domain.biz.promotion.RedeemTreasureDo;
import com.wegame.capi.biz.domain.biz.promotion.level.LevelConfigDo;
import com.wegame.capi.client.domain.vo.promotion.RedeemTreasureVo;
import com.wegame.capi.client.domain.vo.promotion.level.LevelConfigVo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class LevelConfigConverter {

    public static LevelConfigDo resToDo(LevelConfigRes res) {
        if (Objects.isNull(res)) {
            return null;
        }
        return LevelConfigDo.builder()
                .refId(res.getRefId())
                .levelConfig(configDetailToDoList(res.getLevelConfig()))
                .build();
    }

    private static LevelConfigDo.LevelConfigDetail configDetailToDo(LevelConfigRes.LevelConfigDetail detail) {
        if (Objects.isNull(detail)) {
            return null;
        }
        return LevelConfigDo.LevelConfigDetail.builder()
                .experience(detail.getExperience())
                .level(detail.getLevel())
                .name(detail.getName())
                .build();
    }

    private static List<LevelConfigDo.LevelConfigDetail> configDetailToDoList(List<LevelConfigRes.LevelConfigDetail> details) {

        return Optional.ofNullable(details)
                .orElse(Collections.emptyList())
                .stream()
                .map(LevelConfigConverter::configDetailToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static LevelConfigVo doToVo(LevelConfigDo levelConfigDo) {
        if (Objects.isNull(levelConfigDo)) {
            return null;
        }
        return LevelConfigVo.builder()
                .refId(levelConfigDo.getRefId())
                .levelConfig(configDetailToVoList(levelConfigDo.getLevelConfig()))
                .build();
    }

    private static LevelConfigVo.LevelConfigDetail configDetailToVo(LevelConfigDo.LevelConfigDetail detail) {
        if (Objects.isNull(detail)) {
            return null;
        }
        return LevelConfigVo.LevelConfigDetail.builder()
                .experience(detail.getExperience())
                .level(detail.getLevel())
                .name(detail.getName())
                .build();
    }

    private static List<LevelConfigVo.LevelConfigDetail> configDetailToVoList(List<LevelConfigDo.LevelConfigDetail> details) {
        return Optional.ofNullable(details)
                .orElse(Collections.emptyList())
                .stream()
                .map(LevelConfigConverter::configDetailToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
