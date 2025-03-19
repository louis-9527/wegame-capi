package com.wegame.capi.application.controller.promotion;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.converter.promotion.AchievementConverter;
import com.wegame.capi.biz.converter.promotion.RedeemAchievementConverter;
import com.wegame.capi.biz.domain.biz.promotion.AchievementDo;
import com.wegame.capi.biz.domain.biz.promotion.RedeemAchievementDo;
import com.wegame.capi.biz.service.promotion.AchievementService;
import com.wegame.capi.client.domain.request.promotion.RedeemAchievementRequest;
import com.wegame.capi.client.domain.vo.promotion.AchievementVo;
import com.wegame.capi.client.domain.vo.promotion.RedeemAchievementVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.wegame.base.domain.CommonResponse.build;

@Slf4j
@RestController
@RequestMapping("achievement")
@Api(value = "营销成就模块", tags = {"营销成就模块"})
public class AchievementController {

    @Autowired
    AchievementService achievementService;

    @LoginRequired
    @ApiOperation(value = "成就列表", httpMethod = "POST")
    @PostMapping("/getAchievement")
    public CommonResponse<List<AchievementVo>> getAchievement() {
        List<AchievementDo.Achievement> achievement = achievementService.getAchievement();
        return build(AchievementConverter.toVoList(achievement));
    }

    @LoginRequired
    @ApiOperation(value = "领取成就奖励", httpMethod = "POST")
    @PostMapping("/redeemAchievement")
    public CommonResponse<RedeemAchievementVo> redeemAchievement(@RequestBody RedeemAchievementRequest request) {
        request.validate();
        RedeemAchievementDo redeemAchievementDo = achievementService.redeemAchievement(request.getAchievementId(), request.getRedeemId());
        return build(RedeemAchievementConverter.toVo(redeemAchievementDo));
    }
}
