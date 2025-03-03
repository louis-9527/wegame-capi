package com.wegame.capi.biz.service.promotion;

import com.wegame.capi.api.promotion.MissionApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 任务service
 */
@Component
public class MissionService {
    @Autowired
    MissionApi missionApi;

}
