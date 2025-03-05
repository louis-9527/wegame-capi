package com.wegame.capi.application.controller.promotion;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.service.promotion.TreasureService;
import com.wegame.capi.client.domain.request.promotion.QueryTreasureRequest;
import com.wegame.capi.client.domain.request.promotion.UseTreasureRequest;
import com.wegame.capi.client.domain.vo.promotion.TreasureVo;
import com.wegame.capi.client.domain.vo.promotion.UseTreasureVo;
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
@RequestMapping("treasure")
@Api(value = "营销宝箱模块", tags = {"营销宝箱模块"})
public class TreasureController {

    @Autowired
    TreasureService treasureService;

    @LoginRequired
    @ApiOperation(value = "获取宝箱", httpMethod = "POST")
    @PostMapping("/queryTreasure")
    public CommonResponse<TreasureVo> queryTreasureList(@RequestBody QueryTreasureRequest request) {
        request.validate();
        // BetResultDo betResultDo = betService.bet(BetConverter.requestToDo(betRequest));
        return CommonResponse.build(null);
    }

    public CommonResponse<UseTreasureVo> useTreasure(@RequestBody UseTreasureRequest request) {
        request.validate();
        return CommonResponse.build(null);
    }

}
