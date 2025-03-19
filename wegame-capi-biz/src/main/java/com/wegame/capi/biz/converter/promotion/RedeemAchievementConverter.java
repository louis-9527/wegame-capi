package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.RedeemAchievementRes;
import com.wegame.capi.client.domain.vo.promotion.RedeemAchievementVo;

import java.util.Objects;

public class RedeemAchievementConverter {

    public static RedeemAchievementVo toVo(RedeemAchievementRes res) {
        if (Objects.isNull(res)) {
            return null;
        }
        return RedeemAchievementVo.builder()
                .playerName(res.getPlayerName())
                .channelId(res.getChannelId())
                .pId(res.getPId())
                .refId(res.getRefId())
                .reward(redeemAchievementRewardToVo(res.getReward()))
                .build();
    }

    public static RedeemAchievementVo.RedeemAchievementRewardVo redeemAchievementRewardToVo(RedeemAchievementRes.RedeemAchievementReward reward) {
        if (Objects.isNull(reward)) {
            return null;
        }
        return RedeemAchievementVo.RedeemAchievementRewardVo.builder()
                .rewardConfigId(reward.getRewardConfigId())
                .name(reward.getName())
                .count(reward.getCount())
                .imgUrl(reward.getImgUrl())
                .betValue(reward.getBetValue())
                .balanceRequired(reward.getBalanceRequired())
                .starLevel(reward.getStarLevel())
                .build();
    }
}
