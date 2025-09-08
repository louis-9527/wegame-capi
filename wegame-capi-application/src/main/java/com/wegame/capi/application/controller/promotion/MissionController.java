package com.wegame.capi.application.controller.promotion;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.domain.biz.promotion.RedeemDailyMissionDo;
import com.wegame.capi.biz.domain.biz.promotion.RedeemTreasureDo;
import com.wegame.capi.biz.service.promotion.MissionService;
import com.wegame.capi.client.domain.request.promotion.RedeemDailyMissionRequest;
import com.wegame.capi.client.domain.vo.promotion.DailyMissionVo;
import com.wegame.capi.client.domain.vo.promotion.RedeemTreasureVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.wegame.base.domain.CommonResponse.build;
import static com.wegame.capi.biz.converter.promotion.MissionConverter.toVo;
import static com.wegame.capi.biz.converter.promotion.RedeemTreasureConverter.doToVo;

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
        return build(toVo(missionService.getDailyMission()));
    }

    @LoginRequired
    @ApiOperation(value = "领取任务", httpMethod = "POST")
    @PostMapping("/redeemDailyMission")
    public CommonResponse<RedeemTreasureVo> redeemDailyMission(@RequestBody RedeemDailyMissionRequest request) {
        request.validate();
        RedeemDailyMissionDo redeemTreasureDo = missionService.redeemDailyMission(request.getDailyMissionId());
        return build(doToVo(null));
    }
}
