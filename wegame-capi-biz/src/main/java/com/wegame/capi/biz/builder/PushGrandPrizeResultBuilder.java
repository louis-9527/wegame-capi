package com.wegame.capi.biz.builder;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardNotifyInfoDo;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;

import java.util.Objects;

public class PushGrandPrizeResultBuilder {

    public RewardNotifyInfoDo build(BetRecordDo normalBetRecordDo, BetRecordDo freeSpinsBetRecordDo){
        if (Objects.isNull(normalBetRecordDo) || Objects.isNull(freeSpinsBetRecordDo)){
            return null;
        }
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);

        return RewardNotifyInfoDo.builder()



                .build();




    }


}
