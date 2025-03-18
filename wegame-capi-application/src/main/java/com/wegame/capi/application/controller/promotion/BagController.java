package com.wegame.capi.application.controller.promotion;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.domain.PageInfoVo;
import com.wegame.capi.biz.converter.promotion.BagRewordConverter;
import com.wegame.capi.biz.converter.promotion.RewordDetailConverter;
import com.wegame.capi.biz.domain.biz.promotion.BagCurrentRewordDetailDo;
import com.wegame.capi.biz.domain.biz.promotion.BagOtherRewordDetailDo;
import com.wegame.capi.biz.domain.biz.promotion.HistoryMessageDo;
import com.wegame.capi.biz.domain.biz.promotion.RewordDetailDo;
import com.wegame.capi.biz.service.promotion.BagService;
import com.wegame.capi.client.domain.request.promotion.QueryBagCurrentGameRewardRequest;
import com.wegame.capi.client.domain.request.promotion.QueryBagOtherGameRewardRequest;
import com.wegame.capi.client.domain.request.promotion.QueryHistoryMessageRequest;
import com.wegame.capi.client.domain.request.promotion.QueryRewardDetailRequest;
import com.wegame.capi.client.domain.vo.promotion.BagCurrentRewordVo;
import com.wegame.capi.client.domain.vo.promotion.BagOtherRewordVo;
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

@Slf4j
@RestController
@RequestMapping("bag")
@Api(value = "营销背包模块", tags = {"营销背包模块"})
public class BagController {

    @Autowired
    private BagService bagService;

    @LoginRequired
    @ApiOperation(value = "获取背包列表(当前游戏)", httpMethod = "POST")
    @PostMapping("/queryBagCurrentGameReward")
    public CommonResponse<BagCurrentRewordVo> queryBagCurrentGameReward(@RequestBody QueryBagCurrentGameRewardRequest bagRequest) {
        bagRequest.validate();
        List<BagCurrentRewordDetailDo> detailDos = bagService.listCurrentRewordRecord();
        return CommonResponse.build(BagCurrentRewordVo.builder()
                .rewardDetails(BagRewordConverter.currentDoToVoList(detailDos))
                .build());
    }

    @LoginRequired
    @ApiOperation(value = "获取背包列表(其他游戏)", httpMethod = "POST")
    @PostMapping("/queryBagOtherGameReward")
    public CommonResponse<BagOtherRewordVo> queryBagOtherGameReward(@RequestBody QueryBagOtherGameRewardRequest bagRequest) {
        bagRequest.validate();
        List<BagOtherRewordDetailDo> detailDos = bagService.listOtherRewordRecord();
        return CommonResponse.build(BagOtherRewordVo.builder()
                .rewardDetails(BagRewordConverter.otherDoToVoList(detailDos))
                .build());
    }

    @LoginRequired
    @ApiOperation(value = "获取物品详情", httpMethod = "POST")
    @PostMapping("/getRewardDetail")
    public CommonResponse<RewardDetailVo> getRewardStarCardDetail(@RequestBody QueryRewardDetailRequest detailRequest) {
        detailRequest.validate();
        RewordDetailDo byRewardConfigId = bagService.getByRewardConfigId(detailRequest.getRewardConfigId());
        return CommonResponse.build(RewordDetailConverter.doToVo(byRewardConfigId));
    }

    @LoginRequired
    @ApiOperation(value = "获取历史消息", httpMethod = "POST")
    @PostMapping("/getHistoryMessage")
    public CommonResponse<PageInfoVo<HistoryMessageVo>> getHistoryMessage(@RequestBody QueryHistoryMessageRequest request) {
        request.validate();
        List<HistoryMessageDo> messageDoList = bagService.getHistoryMessageList(request);
        return CommonResponse.build(PageInfoVo.build(request.getPageNo(),
                request.getPageSize(),
                BagRewordConverter.messageDoToVos(messageDoList),
                0L));
    }

}
