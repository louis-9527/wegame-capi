package com.wegame.capi.application.controller;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.converter.BetConverter;
import com.wegame.capi.biz.converter.BetResultConverter;
import com.wegame.capi.biz.domain.BetResultDo;
import com.wegame.capi.biz.service.BetService;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.domain.request.query.QueryFreeSpinRequest;
import com.wegame.capi.client.domain.vo.BetResultVo;
import com.wegame.capi.client.domain.vo.GameBetNormalVo;
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
@Api(value = "投注", tags = {"bet模块"})
public class BetController {

    @Autowired
    BetService betService;

    //    @LoginRequired todo fix
    @ApiOperation(value = "投注", httpMethod = "POST")
    @PostMapping("/bet")
    public CommonResponse<BetResultVo> bet(@RequestBody BetRequest betRequest) {
        // todo fix
        Operator operator = new Operator();
        operator.setPlayerId("1000");
        operator.setName("张三");
        operator.setUserId(1000L);
        OperatorContext.setOperator(operator);

        betRequest.validate();
        BetResultDo betResultDo = betService.bet(BetConverter.requestToDo(betRequest));
        return CommonResponse.build(BetResultConverter.doToVo(betResultDo));
    }

    @ApiOperation(value = "获取免费轮投注结果", httpMethod = "POST")
    @PostMapping("/bet/queryFreeSpinDetail")
    public CommonResponse<GameBetNormalVo> queryFreeSpinDetail(@RequestBody QueryFreeSpinRequest request) {
        GameBetNormalVo normalVo = betService.queryFreeSpinDetail(request);
        return CommonResponse.build(normalVo);
    }
}
