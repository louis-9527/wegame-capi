package com.wegame.capi.application.controller;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.manager.GameNotifyManager;
import com.wegame.capi.client.domain.vo.GameNotifyVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通知模块
 */
@Slf4j
@RestController
@RequestMapping("notify")
@Api(value = "通知模块", tags = {"通知模块"})
public class GameNotifyController {

    @Autowired
    GameNotifyManager gameNotifyManager;

    @LoginRequired
    @ApiOperation(value = "查询通知", httpMethod = "GET")
    @GetMapping("/query")
    public CommonResponse<GameNotifyVo> query() {
        return CommonResponse.build(gameNotifyManager.query());
    }
}