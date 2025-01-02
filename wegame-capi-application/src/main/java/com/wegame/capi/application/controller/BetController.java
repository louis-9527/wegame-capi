package com.wegame.capi.application.controller;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.domain.CommonResponse;

import com.wegame.capi.biz.context.GameBetContext;
import com.wegame.capi.client.domain.request.update.BetBaseRequest;
import com.wegame.capi.client.domain.vo.GameBetBaseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("bet")
@Api(value = "投注", tags = {"bet模块"})
public class BetController {

    @Autowired
    GameBetContext gameBetContext;

    @ApiOperation(value = "bet", httpMethod = "POST")
    @PostMapping("/bet")
    public CommonResponse<GameBetBaseVo<Object>> bet(@RequestBody BetBaseRequest<Object> betRequest) {
        Operator operator = new Operator();
        operator.setPlayerId("1000");
        operator.setName("张三");
        operator.setUserId(1000L);
        OperatorContext.setOperator(operator);
        GameBetBaseVo<Object> betResult = gameBetContext.bet(betRequest);

        return CommonResponse.build(betResult);
    }


}
