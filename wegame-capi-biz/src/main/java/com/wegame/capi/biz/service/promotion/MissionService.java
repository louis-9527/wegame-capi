package com.wegame.capi.biz.service.promotion;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.promotion.GetDailyMissionReq;
import com.wegame.capi.api.domain.res.promotion.GetDailyMissionRes;
import com.wegame.capi.api.promotion.MissionApi;
import com.wegame.capi.biz.builder.req.MissionReqBuilder;
import com.wegame.capi.biz.converter.promotion.MissionConverter;
import com.wegame.capi.client.domain.vo.promotion.DailyMissionVo;
import com.wegame.capi.common.context.UserInfoContext;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务service
 */
@Component
public class MissionService {

    @Autowired
    MissionApi missionApi;

    public List<DailyMissionVo> getDailyMission() {
        GetDailyMissionRes getDailyMissionRes = missionApi.getDailyMission(MissionReqBuilder.build());
        AssertUtil.assertNotNull(getDailyMissionRes.getDailyMissionList(), ErrorCode.ERR_PARAM);
        return getDailyMissionRes.getDailyMissionList().stream().map(MissionConverter::build).collect(Collectors.toList());
    }
}
