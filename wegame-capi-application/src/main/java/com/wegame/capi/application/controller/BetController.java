package com.wegame.capi.application.controller;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.converter.BetConverter;
import com.wegame.capi.biz.converter.BetResultConverter;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.service.BetService;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.domain.vo.BetResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("bet")
@Api(value = "投注模块", tags = {"投注模块"})
public class BetController {

    @Autowired
    BetService betService;

    @LoginRequired
    @ApiOperation(value = "投注", httpMethod = "POST")
    @PostMapping("/bet")
    public CommonResponse<BetResultVo> bet(@RequestBody BetRequest betRequest) {
        betRequest.validate();
        BetResultDo betResultDo = betService.bet(BetConverter.requestToDo(betRequest));
        return CommonResponse.build(BetResultConverter.doToVo(betResultDo));
    }
}
