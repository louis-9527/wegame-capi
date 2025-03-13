package com.wegame.capi.application.controller;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.RewardInfoConverter;
import com.wegame.capi.biz.service.RewardService;
import com.wegame.capi.client.domain.vo.RewardInfoVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 中奖模块
 */
@Slf4j
@RestController
@RequestMapping("reward")
@Api(value = "中奖模块", tags = {"中奖模块"})
public class RewardController extends AbstractController {

    @Autowired
    RewardService rewardService;

    @LoginRequired
    @ApiOperation(value = "查询大奖信息", httpMethod = "GET")
    @GetMapping("/get")
    public CommonResponse<RewardInfoVo> get(HttpServletResponse response) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GameTypeEnum gameType = GameTypeEnum.valueOfId(userInfo.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        noCacheControl(response);
        return CommonResponse.build(RewardInfoConverter.doToVo(rewardService.getWithCache(gameType)));
    }
}