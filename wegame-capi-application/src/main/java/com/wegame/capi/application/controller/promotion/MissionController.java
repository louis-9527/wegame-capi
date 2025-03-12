package com.wegame.capi.application.controller.promotion;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.service.promotion.MissionService;
import com.wegame.capi.client.domain.vo.promotion.DailyMissionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("mission")
@Api(value = "营销任务模块", tags = {"营销任务模块"})
public class MissionController {

    @Autowired
    MissionService missionService;

    @LoginRequired
    @ApiOperation(value = "任务列表", httpMethod = "POST")
    @PostMapping("/getDailyMission")
    public CommonResponse<DailyMissionVo> getDailyMission() {
        return CommonResponse.build(missionService.getDailyMission());
    }

}
