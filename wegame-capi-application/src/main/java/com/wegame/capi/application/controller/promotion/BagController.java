package com.wegame.capi.application.controller.promotion;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.domain.PageDo;
import com.wegame.base.domain.PageInfoVo;
import com.wegame.capi.biz.converter.promotion.BagOtherRewardDetailConverter;
import com.wegame.capi.biz.converter.promotion.BagRewardConverter;
import com.wegame.capi.biz.domain.biz.promotion.BagCurrentRewardDetailDo;
import com.wegame.capi.biz.domain.biz.promotion.HistoryMessageDo;
import com.wegame.capi.biz.service.promotion.BagService;
import com.wegame.capi.client.domain.request.promotion.QueryBagCurrentGameRewardRequest;
import com.wegame.capi.client.domain.request.promotion.QueryBagOtherGameRewardRequest;
import com.wegame.capi.client.domain.request.promotion.QueryHistoryMessageRequest;
import com.wegame.capi.client.domain.request.promotion.QueryRewardDetailRequest;
import com.wegame.capi.client.domain.vo.promotion.BagCurrentRewardVo;
import com.wegame.capi.client.domain.vo.promotion.BagOtherRewardVo;
import com.wegame.capi.client.domain.vo.promotion.HistoryMessageVo;
import com.wegame.capi.client.domain.vo.promotion.RewardDetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.wegame.base.domain.CommonResponse.build;
import static com.wegame.capi.biz.converter.promotion.HistoryMessageConverter.doToVoList;
import static com.wegame.capi.biz.converter.promotion.RewardDetailConverter.doToVo;

@Slf4j
@RestController
@RequestMapping("bag")
@Api(value = "营销背包模块", tags = {"营销背包模块"})
public class BagController {

    @Autowired
    BagService bagService;

    @LoginRequired
    @ApiOperation(value = "获取背包列表(当前游戏)", httpMethod = "POST")
    @PostMapping("/queryBagCurrentGameReward")
    public CommonResponse<BagCurrentRewardVo> queryBagCurrentGameReward(@RequestBody QueryBagCurrentGameRewardRequest request) {
        request.validate();
        List<BagCurrentRewardDetailDo> detailDos = bagService.listCurrentRewordRecord();
        return build(BagCurrentRewardVo.builder()
                .rewardDetails(BagRewardConverter.toVoList(detailDos))
                .build());
    }

    @LoginRequired
    @ApiOperation(value = "获取背包列表(其他游戏)", httpMethod = "POST")
    @PostMapping("/queryBagOtherGameReward")
    public CommonResponse<BagOtherRewardVo> queryBagOtherGameReward(@RequestBody QueryBagOtherGameRewardRequest request) {
        request.validate();
        return build(BagOtherRewardVo.builder()
                .rewardDetails(BagOtherRewardDetailConverter.toVoList(bagService.listOtherRewordRecord()))
                .build());
    }

    @LoginRequired
    @ApiOperation(value = "获取物品详情", httpMethod = "POST")
    @PostMapping("/getRewardDetail")
    public CommonResponse<RewardDetailVo> getRewardStarCardDetail(@RequestBody QueryRewardDetailRequest request) {
        request.validate();
        return build(doToVo(bagService.getRewardDetail(request.getRewardConfigId())));
    }

    @LoginRequired
    @ApiOperation(value = "获取历史消息", httpMethod = "POST")
    @PostMapping("/getHistoryMessage")
    public CommonResponse<PageInfoVo<HistoryMessageVo>> getHistoryMessage(@RequestBody QueryHistoryMessageRequest request) {
        request.validate();
        PageDo<HistoryMessageDo> messageDoPageDo = bagService.getHistoryMessage(request.getPageNo(), request.getPageSize());
        return build(PageInfoVo.build(request.getPageNo(),
                request.getPageSize(),
                doToVoList(messageDoPageDo.getList()),
                messageDoPageDo.getTotal()));
    }

}
