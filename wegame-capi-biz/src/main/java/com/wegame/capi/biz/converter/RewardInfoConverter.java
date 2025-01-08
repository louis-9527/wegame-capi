package com.wegame.capi.biz.converter;

import com.wegame.capi.biz.domain.biz.reward.RewardInfoDo;
import com.wegame.capi.client.domain.vo.RewardInfoVo;

import java.util.Objects;

public class RewardInfoConverter {

    public static RewardInfoVo doToVo(RewardInfoDo rewardInfoDo) {
        if (Objects.isNull(rewardInfoDo)) {
            return null;
        }
        return RewardInfoVo.builder()
                .rewardMessageList(rewardInfoDo.getRewardMessageList())
                .userCreditChange(rewardInfoDo.getUserCreditChange())
                .build();
    }
}
