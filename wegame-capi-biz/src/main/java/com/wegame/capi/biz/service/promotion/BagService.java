package com.wegame.capi.biz.service.promotion;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.promotion.GetBagCurrentRewardReq;
import com.wegame.capi.api.domain.req.promotion.GetBagOtherRewardReq;
import com.wegame.capi.api.domain.req.promotion.GetBagRewardByIdReq;
import com.wegame.capi.api.domain.req.promotion.GetHistoryMessageReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.promotion.*;
import com.wegame.capi.api.promotion.BagApi;
import com.wegame.capi.biz.converter.promotion.BagOtherRewardDetailConverter;
import com.wegame.capi.biz.converter.promotion.BagRewardConverter;
import com.wegame.capi.biz.converter.promotion.HistoryMessageConverter;
import com.wegame.capi.biz.converter.promotion.RewardDetailConverter;
import com.wegame.capi.biz.domain.biz.promotion.*;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.domain.request.promotion.QueryHistoryMessageRequest;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.wegame.capi.biz.builder.req.RedeemRewardReqBuilder.buildReq;

/**
 * 背包service
 */
@Component
public class BagService {

    private static final Integer BAG_CURRENT_REWORD_TYPE = 1;
    @Autowired
    BagApi bagApi;

    public List<BagCurrentRewardDetailDo> listCurrentRewordRecord() {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GetBagCurrentRewardReq currentRewardReq = GetBagCurrentRewardReq.builder()
                .type(BAG_CURRENT_REWORD_TYPE)
                .playerName(userInfo.getPlayerName())
                .gameCode(userInfo.getGameId())
                .pId(userInfo.getPId())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .channelId(userInfo.getChannelId())
                .createTime(DateUtil.toUTC(new Date()))
                .build();
        GetBagCurrentRewardRes rewardBagRes = bagApi.getBagCurrentReward(currentRewardReq);
        return BagRewardConverter.toDoList(rewardBagRes.getRewardBag());
    }

    public List<BagOtherRewardDetailDo> listOtherRewordRecord() {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GetBagOtherRewardReq currentRewardReq = GetBagOtherRewardReq.builder()
                .playerName(userInfo.getPlayerName())
                .gameCode(userInfo.getGameId())
                .pId(userInfo.getPId())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .channelId(userInfo.getChannelId())
                .createTime(DateUtil.toUTC(new Date()))
                .build();
        GetBagOtherRewardRes rewardBagRes = bagApi.getBagOtherReward(currentRewardReq);
        return BagOtherRewardDetailConverter.toDoList(rewardBagRes.getRewardBag());
    }

    public RewardDetailDo getByRewardConfigId(String rewardConfigId) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GetBagRewardByIdReq rewardByIdReq = GetBagRewardByIdReq.builder()
                .playerName(userInfo.getPlayerName())
                .pId(userInfo.getPId())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .channelId(userInfo.getChannelId())
                .createTime(DateUtil.toUTC(new Date()))
                .rewardConfigId(rewardConfigId)
                .build();
        GetRewardDetailRes rewardBagRes = bagApi.getRewardById(rewardByIdReq);
        return RewardDetailConverter.toDo(rewardBagRes);
    }

    public List<HistoryMessageDo> getHistoryMessageList(QueryHistoryMessageRequest reqeust) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GetHistoryMessageReq messageReq = GetHistoryMessageReq.builder()
                .playerName(userInfo.getPlayerName())
                .pId(userInfo.getPId())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .channelId(userInfo.getChannelId())
                .createTime(DateUtil.toUTC(new Date()))
                .page(reqeust.getPageNo())
                .limit(reqeust.getOffset())
                .build();
        GetHistoryMessageRes messageRes = bagApi.getHistoryMessage(messageReq);
        return HistoryMessageConverter.toDoList(messageRes.getMessages());
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
