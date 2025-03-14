package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetDailyMissionRes;
import com.wegame.capi.client.domain.vo.promotion.DailyMissionVo;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MissionConverter {
    public static DailyMissionVo buildDailyMissionItemList(GetDailyMissionRes res) {
        return DailyMissionVo.builder()
                .refreshTime(Objects.isNull(res.getRefreshTime()) ? null : res.getRefreshTime().getTime())
                .dailyMissionList(buildDailyMissionItemList(res.getDailyMissionList()))
                .build();
    }

    private static List<DailyMissionVo.DailyMissionItemVo> buildDailyMissionItemList(List<GetDailyMissionRes.DailyMission> dailyMissionList) {
        if (CollectionUtils.isEmpty(dailyMissionList)) {
            return null;
        }
        return dailyMissionList.stream().map(MissionConverter::buildDailyMissionItem).collect(Collectors.toList());
    }

    private static DailyMissionVo.DailyMissionItemVo buildDailyMissionItem(GetDailyMissionRes.DailyMission dailyMission) {
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
