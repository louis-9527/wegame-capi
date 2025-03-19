package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.RedeemAchievementRes;
import com.wegame.capi.biz.domain.biz.promotion.RedeemAchievementDo;
import com.wegame.capi.client.domain.vo.promotion.RedeemAchievementVo;

import java.util.Objects;

public class RedeemAchievementConverter {

    public static RedeemAchievementDo toDo(RedeemAchievementRes res) {
        if (Objects.isNull(res)) {
            return null;
        }
        return RedeemAchievementDo.builder()
                .playerName(res.getPlayerName())
                .channelId(res.getChannelId())
                .pId(res.getPId())
                .refId(res.getRefId())
                .reward(rewardToDo(res.getReward()))
                .build();
    }
    public static RedeemAchievementDo.Reward rewardToDo(RedeemAchievementRes.Reward reward) {
        if (Objects.isNull(reward)) {
            return null;
        }
        return RedeemAchievementDo.Reward.builder()
                .rewardConfigId(reward.getRewardConfigId())
                .name(reward.getName())
                .count(reward.getCount())
                .imgUrl(reward.getImgUrl())
                .betValue(reward.getBetValue())
                .balanceRequired(reward.getBalanceRequired())
                .starLevel(reward.getStarLevel())
                .build();
    }

    public static RedeemAchievementVo toVo(RedeemAchievementDo redeemAchievementDo) {
        if (Objects.isNull(redeemAchievementDo)) {
            return null;
        }
        return RedeemAchievementVo.builder()
                .playerName(redeemAchievementDo.getPlayerName())
                .channelId(redeemAchievementDo.getChannelId())
                .pId(redeemAchievementDo.getPId())
                .refId(redeemAchievementDo.getRefId())
                .reward(rewardToVo(redeemAchievementDo.getReward()))
                .build();
    }
    public static RedeemAchievementVo.RedeemAchievementRewardVo rewardToVo(RedeemAchievementDo.Reward reward) {
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
