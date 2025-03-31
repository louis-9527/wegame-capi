package com.wegame.capi.biz.service.promotion;

import com.wegame.base.domain.PageDo;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.promotion.*;
import com.wegame.capi.api.promotion.BagApi;
import com.wegame.capi.biz.builder.req.GetBagCurrentRewardReqBuilder;
import com.wegame.capi.biz.builder.req.GetBagOtherRewardReqBuilder;
import com.wegame.capi.biz.builder.req.GetHistoryMessageReqBuilder;
import com.wegame.capi.biz.builder.req.GetRewardDetailReqBuilder;
import com.wegame.capi.biz.converter.promotion.BagOtherRewardDetailConverter;
import com.wegame.capi.biz.converter.promotion.BagRewardConverter;
import com.wegame.capi.biz.converter.promotion.HistoryMessageConverter;
import com.wegame.capi.biz.converter.promotion.RewardDetailConverter;
import com.wegame.capi.biz.domain.biz.promotion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.wegame.capi.biz.builder.req.RedeemRewardReqBuilder.buildReq;

/**
 * 背包service
 */
@Component
public class BagService {

    @Autowired
    BagApi bagApi;

    public List<BagCurrentRewardDetailDo> listCurrentRewordRecord() {
        GetBagCurrentRewardRes res = bagApi.getBagCurrentReward(GetBagCurrentRewardReqBuilder.buildReq());
        return BagRewardConverter.toDoList(res.getRewardBag());
    }

    public List<BagOtherRewardDetailDo> listOtherRewordRecord() {
        GetBagOtherRewardRes res = bagApi.getBagOtherReward(GetBagOtherRewardReqBuilder.buildReq());
        return BagOtherRewardDetailConverter.toDoList(res.getRewardBag());
    }

    public RewardDetailDo getRewardDetail(String rewardConfigId) {
        GetRewardDetailRes res = bagApi.getRewardDetail(GetRewardDetailReqBuilder.buildReq(rewardConfigId));
        return RewardDetailConverter.toDo(res);
    }

    public PageDo<HistoryMessageDo> getHistoryMessage(Integer pageNo, Integer pageSize) {
        GetHistoryMessageRes res = bagApi.getHistoryMessage(GetHistoryMessageReqBuilder.buildReq(pageNo, pageSize));
        return PageDo.<HistoryMessageDo>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .total(res.getTotalCount())
                .list(HistoryMessageConverter.resToDoList(res.getMessages()))
                .build();

    }

    /**
     * 核销-使用星卡
     */
    public RedeemRewardResultDo use(String rewardId, Long payoutAmount, Long betWithPayoutRefId) {
        CommonRes<RedeemRewardRes> res = bagApi.redeemReward(buildReq(rewardId, payoutAmount, betWithPayoutRefId));
        return RedeemRewardResultDo.builder()
                .success(res.isSuc())
                .status(res.getStatus())
                .message(res.getMessage())
                .build();
    }
}
