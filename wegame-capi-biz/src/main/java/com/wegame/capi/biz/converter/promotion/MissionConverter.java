package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetDailyMissionRes;
import com.wegame.capi.client.domain.vo.promotion.DailyMissionVo;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class MissionConverter {

    public static DailyMissionVo toVo(GetDailyMissionRes missionRes) {
        return DailyMissionVo.builder()
                .refreshTime(Objects.isNull(missionRes.getRefreshTime()) ? null : missionRes.getRefreshTime().getTime())
                .dailyMissionList(toVoList(missionRes.getDailyMissionList()))
                .build();
    }

    private static List<DailyMissionVo.DailyMissionItemVo> toVoList(List<GetDailyMissionRes.DailyMission> dailyMissionList) {
        return Optional.ofNullable(dailyMissionList)
                .orElse(Collections.emptyList())
                .stream()
                .map(MissionConverter::toVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static DailyMissionVo.DailyMissionItemVo toVo(GetDailyMissionRes.DailyMission dailyMission) {
        if (Objects.isNull(dailyMission)) {
            return null;
        }
        return DailyMissionVo.DailyMissionItemVo.builder()
                .dailyMissionId(dailyMission.getDailyMissionId())
                .description(dailyMission.getDescription())
                .experience(dailyMission.getExperience())
                .target(dailyMission.getTarget())
                .status(dailyMission.getStatus())
                .starLevel(dailyMission.getStarLevel())
                .build();
    }
}
