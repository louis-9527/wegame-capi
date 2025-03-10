package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetDailyMissionRes;
import com.wegame.capi.client.domain.vo.promotion.DailyMissionVo;

public class MissionConverter {
    public static DailyMissionVo build(GetDailyMissionRes.DailyMission item) {
        return DailyMissionVo.builder()
                .dailyMissionId(item.getDailyMissionId())
                .description(item.getDescription())
                .experience(item.getExperience())
                .target(item.getTarget())
                .status(item.getStatus())
                .build();
    }
}
