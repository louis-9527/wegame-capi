package com.wegame.capi.application.controller.promotion;

import com.wegame.base.domain.CommonResponse;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.service.promotion.MissionService;
import com.wegame.capi.client.domain.request.promotion.RedeemAchievementRequest;
import com.wegame.capi.client.domain.vo.promotion.AchievementVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("achievement")
@Api(value = "营销成就模块", tags = {"营销成就模块"})
public class AchievementController {

    @Autowired
    MissionService missionService;

    @ApiOperation(value = "成就列表", httpMethod = "POST")
    @GetMapping("/getAchievement")
    public CommonResponse<List<AchievementVo>> getAchievement() {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GameTypeEnum gameType = GameTypeEnum.valueOfId(userInfo.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);

        return CommonResponse.build(null);
    }

    @ApiOperation(value = "领取成就奖励", httpMethod = "POST")
    @PostMapping("/redeemAchievement")
    public CommonResponse<Boolean> redeemAchievement(RedeemAchievementRequest request) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GameTypeEnum gameType = GameTypeEnum.valueOfId(userInfo.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);

        return CommonResponse.build(true);
    }
}
