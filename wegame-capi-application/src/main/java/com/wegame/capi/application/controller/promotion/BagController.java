package com.wegame.capi.application.controller.promotion;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.service.promotion.BagService;
import com.wegame.capi.client.domain.request.promotion.QueryRewardBagRequest;
import com.wegame.capi.client.domain.vo.promotion.RewardStarCardVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("bag")
@Api(value = "营销背包模块", tags = {"营销背包模块"})
public class BagController {

    @Autowired
    BagService bagService;

    @LoginRequired
    @ApiOperation(value = "获取背包列表", httpMethod = "POST")
    @PostMapping("/getRewardBag")
    public CommonResponse<List<RewardStarCardVo>> getRewardBag(@RequestBody QueryRewardBagRequest bagRequest) {
        bagRequest.validate();
        // BetResultDo betResultDo = betService.bet(BetConverter.requestToDo(betRequest));
        return CommonResponse.build(new ArrayList<>());
    }

}
