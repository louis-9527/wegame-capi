package com.wegame.capi.biz.builder.req;

import com.wegame.capi.api.domain.req.promotion.CreateFreeSpinReq;
import com.wegame.capi.client.domain.request.promotion.CreateFreeSpinRequest;

public class CreateFreeSpinReqBuilder {

    public static CreateFreeSpinReq buildReq(CreateFreeSpinRequest request) {
//        UserInfo userInfo = UserInfoContext.getUserInfo();
//        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        return CreateFreeSpinReq.builder()
                .refId(request.getRefId())
                .providerId(request.getProviderId())
                .platformId(request.getPlatformId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .eventName(request.getEventName())
                .gameIDList(request.getGameIDList())
                .playerList(request.getPlayerList())
                .freeSpinTimes(request.getFreeSpinTimes())
                .betValue(request.getBetValue())
                .maxPlayerCount(request.getMaxPlayerCount())
                .page(request.getPage())
                .build();
    }
}
