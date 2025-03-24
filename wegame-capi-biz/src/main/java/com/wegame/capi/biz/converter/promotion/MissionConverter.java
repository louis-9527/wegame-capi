package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetDailyMissionRes;
import com.wegame.capi.biz.domain.biz.promotion.DailyMissionDo;
import com.wegame.capi.client.domain.vo.promotion.DailyMissionVo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class MissionConverter {

    public static DailyMissionDo toDo(GetDailyMissionRes missionRes) {
        return com.wegame.capi.biz.domain.biz.promotion.DailyMissionDo.builder()
                .refreshTime(missionRes.getRefreshTime())
                .dailyMissionList(toDoList(missionRes.getDailyMissionList()))
                .build();
    }
    private static List<DailyMissionDo.DailyMission> toDoList(List<GetDailyMissionRes.DailyMission> dailyMissionList) {
        return Optional.ofNullable(dailyMissionList)
                .orElse(Collections.emptyList())
                .stream()
                .map(MissionConverter::toDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    private static DailyMissionDo.DailyMission toDo(GetDailyMissionRes.DailyMission dailyMission) {
        if (Objects.isNull(dailyMission)) {
            return null;
        }
        return DailyMissionDo.DailyMission.builder()
                .dailyMissionId(dailyMission.getDailyMissionId())
                .target(dailyMission.getTarget())
                .reward(dailyMission.getReward())
                .description(dailyMission.getDescription())
                .iconUrl(dailyMission.getIconUrl())
                .experience(dailyMission.getExperience())
                .status(dailyMission.getStatus())
                .starLevel(dailyMission.getStarLevel())
                .rewardConfigId(dailyMission.getRewardConfigId())
                .build();
    }

    public static DailyMissionVo toVo(DailyMissionDo dailyMissionDo) {
        return DailyMissionVo.builder()
                .refreshTime(Objects.isNull(dailyMissionDo.getRefreshTime()) ? null : dailyMissionDo.getRefreshTime().getTime())
                .dailyMissionList(toVoList(dailyMissionDo.getDailyMissionList()))
                .build();
    }
    private static List<DailyMissionVo.DailyMission> toVoList(List<DailyMissionDo.DailyMission> dailyMissionList) {
        return Optional.ofNullable(dailyMissionList)
                .orElse(Collections.emptyList())
                .stream()
                .map(MissionConverter::toVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    private static DailyMissionVo.DailyMission toVo(DailyMissionDo.DailyMission dailyMission) {
        if (Objects.isNull(dailyMission)) {
            return null;
        }
        return DailyMissionVo.DailyMission.builder()
                .dailyMissionId(dailyMission.getDailyMissionId())
                .target(dailyMission.getTarget())
                .reward(dailyMission.getReward())
                .description(dailyMission.getDescription())
                .iconUrl(dailyMission.getIconUrl())
                .experience(dailyMission.getExperience())
                .status(dailyMission.getStatus())
                .starLevel(dailyMission.getStarLevel())
                .rewardConfigId(dailyMission.getRewardConfigId())
                .build();
    }
}
