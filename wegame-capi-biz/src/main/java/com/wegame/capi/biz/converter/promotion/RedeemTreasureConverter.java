package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.RedeemTreasureRes;
import com.wegame.capi.client.domain.vo.promotion.RedeemTreasureVo;

import java.util.Objects;

public class RedeemTreasureConverter {

    public static RedeemTreasureVo toVo(RedeemTreasureRes res) {
        if (Objects.isNull(res)) {
            return null;
        }
        return RedeemTreasureVo.builder()
                .treasureId(res.getTreasureId())
                .playerName(res.getPlayerName())
                .channelId(res.getChannelId())
                .pId(res.getPId())
                .level(res.getLevel())
                .experience(res.getExperience())
                .refId(res.getRefId())
                .reward(treasureRewardToVo(res.getReward()))
                .build();
    }

    private static RedeemTreasureVo.TreasureRewardVo treasureRewardToVo(RedeemTreasureRes.Reward reward) {
        if (Objects.isNull(reward)) {
            return null;
        }
        return RedeemTreasureVo.TreasureRewardVo.builder()
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
