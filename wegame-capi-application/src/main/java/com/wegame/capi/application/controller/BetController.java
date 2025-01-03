package com.wegame.capi.application.controller;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.domain.CommonResponse;

import com.wegame.capi.biz.context.GameBetContext;
import com.wegame.capi.client.domain.request.query.QueryFreeSpinRequest;
import com.wegame.capi.client.domain.request.update.BetRequest;
import com.wegame.capi.client.domain.vo.GameBetBaseVo;
import com.wegame.capi.client.domain.vo.GameBetNormalVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.LengthOf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("bet")
@Api(value = "投注", tags = {"bet模块"})
public class BetController {

    @Autowired
    GameBetContext gameBetContext;


//    @LoginRequired  todo fix
    @ApiOperation(value = "投注", httpMethod = "POST")
    @PostMapping("/bet")
    public CommonResponse<GameBetBaseVo<Object>> bet(@RequestBody BetRequest<Object> betRequest) {
        // todo fix
        Operator operator = new Operator();
        operator.setPlayerId("1000");
        operator.setName("张三");
        operator.setUserId(1000L);
        OperatorContext.setOperator(operator);

        betRequest.validate();
        GameBetBaseVo<Object> betResult = gameBetContext.bet(betRequest);

        return CommonResponse.build(betResult);
    }

    @ApiOperation(value = "获取免费轮投注结果", httpMethod = "POST")
    @PostMapping("/bet/queryFreeSpinDetail")
    public CommonResponse<GameBetNormalVo> queryFreeSpinDetail(@RequestBody QueryFreeSpinRequest request) {
        GameBetNormalVo normalVo = gameBetContext.queryFreeSpinDetail(request);
        return CommonResponse.build(normalVo);
    }


}
