package com.wegame.capi.biz.service.promotion;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.domain.res.promotion.GetDailyMissionRes;
import com.wegame.capi.api.promotion.MissionApi;
import com.wegame.capi.biz.builder.req.MissionReqBuilder;
import com.wegame.capi.biz.converter.promotion.MissionConverter;
import com.wegame.capi.client.domain.vo.promotion.DailyMissionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 任务service
 */
@Component
public class MissionService {

    @Autowired
    MissionApi missionApi;

    public DailyMissionVo getDailyMission() {
        GetDailyMissionRes getDailyMissionRes = missionApi.getDailyMission(MissionReqBuilder.buildGetDailyMissionReq());
        AssertUtil.assertNotNull(getDailyMissionRes.getDailyMissionList(), ErrorCode.ERR_PARAM);
        return MissionConverter.toVoList(getDailyMissionRes);
    }
}
