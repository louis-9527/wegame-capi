package com.wegame.capi.biz.service.promotion;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.domain.req.promotion.GetBagCurrentRewardReq;
import com.wegame.capi.api.domain.req.promotion.GetBagOtherRewardReq;
import com.wegame.capi.api.domain.res.promotion.GetBagCurrentRewardRes;
import com.wegame.capi.api.domain.res.promotion.GetBagOtherRewardRes;
import com.wegame.capi.api.promotion.BagApi;
import com.wegame.capi.biz.converter.promotion.BagRewordConverter;
import com.wegame.capi.biz.domain.biz.promotion.BagCurrentRewordDetailDo;
import com.wegame.capi.biz.domain.biz.promotion.BagOtherRewordDetailDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 背包service
 */
@Component
public class BagService {

    @Autowired
    private BagApi bagApi;
    @Autowired
    public SnowflakeIdGenerator snowflakeIdGenerator;


    private static final Integer BAG_CURRENT_REWORD_TYPE = 1;

    public List<BagCurrentRewordDetailDo> ListCurrentRewordRecord() {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GetBagCurrentRewardReq currentRewardReq = GetBagCurrentRewardReq.builder()
                .type(BAG_CURRENT_REWORD_TYPE)
                .playerName(userInfo.getPlayerName())
                .gameCode(userInfo.getGameId())
                .pId(userInfo.getPId())
                .refId(String.valueOf(snowflakeIdGenerator.nextId()))
                .channelId(userInfo.getChannelId())
                .createTime(DateUtil.toUTC(new Date()))
                .build();
        GetBagCurrentRewardRes rewardBagRes = bagApi.getBagCurrentReward(currentRewardReq);
        return BagRewordConverter.currentResToDoList(rewardBagRes.getRewardBag());
    }

    public List<BagOtherRewordDetailDo> ListOtherRewordRecord(){
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        GetBagOtherRewardReq currentRewardReq = GetBagOtherRewardReq.builder()
                .playerName(userInfo.getPlayerName())
                .gameCode(userInfo.getGameId())
                .pId(userInfo.getPId())
                .refId(String.valueOf(snowflakeIdGenerator.nextId()))
                .channelId(userInfo.getChannelId())
                .createTime(DateUtil.toUTC(new Date()))
                .build();
        GetBagOtherRewardRes rewardBagRes = bagApi.getBagOtherReward(currentRewardReq);
        return BagRewordConverter.otherResToDoList(rewardBagRes.getRewardBag());
    }


}
