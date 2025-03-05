package com.wegame.capi.application.controller.promotion;

import com.wegame.base.domain.CommonResponse;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.service.promotion.MissionService;
import com.wegame.capi.client.domain.vo.promotion.DailyMissionVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("mission")
@Api(value = "营销任务模块", tags = {"营销任务模块"})
public class MissionController {

    @Autowired
    MissionService missionService;

    @ApiOperation(value = "分页查询", httpMethod = "POST")
    @GetMapping("/getDailyMission")
    public CommonResponse<List<DailyMissionVo>> getDailyMission() {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GameTypeEnum gameType = GameTypeEnum.valueOfId(userInfo.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);

        return CommonResponse.build(null);
    }

}
