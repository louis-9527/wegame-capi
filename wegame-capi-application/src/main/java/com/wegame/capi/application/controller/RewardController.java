package com.wegame.capi.application.controller;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.manager.RewardManager;
import com.wegame.capi.client.domain.vo.RewardInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 中奖模块
 */
@Slf4j
@RestController
@RequestMapping("reward")
@Api(value = "中奖模块", tags = {"中奖模块"})
public class RewardController {

    @Autowired
    RewardManager rewardManager;

    @LoginRequired
    @ApiOperation(value = "查询大奖结果", httpMethod = "GET")
    @GetMapping("/list")
    public CommonResponse<RewardInfoVo> list() {
        return CommonResponse.build(rewardManager.list());
    }
}