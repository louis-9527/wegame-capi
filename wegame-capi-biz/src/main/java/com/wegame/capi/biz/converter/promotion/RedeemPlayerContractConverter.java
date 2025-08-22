package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.RedeemTreasureRes;
import com.wegame.capi.biz.domain.biz.promotion.RedeemTreasureDo;
import com.wegame.capi.client.domain.vo.promotion.RedeemTreasureVo;

import java.util.Objects;

public class RedeemPlayerContractConverter {

    public static RedeemTreasureDo resToDo(RedeemTreasureRes res) {
        if (Objects.isNull(res)) {
            return null;
        }
        return RedeemTreasureDo.builder()
                .treasureId(res.getTreasureId())
                .playerName(res.getPlayerName())
                .channelId(res.getChannelId())
                .pId(res.getPId())
                .level(res.getLevel())
                .experience(res.getExperience())
                .refId(res.getRefId())
                .reward(rewardToDo(res.getReward()))
                .build();
    }

    private static RedeemTreasureDo.Reward rewardToDo(RedeemTreasureRes.Reward reward) {
        if (Objects.isNull(reward)) {
            return null;
        }
        return RedeemTreasureDo.Reward.builder()
                .rewardConfigId(reward.getRewardConfigId())
                .name(reward.getName())
                .count(reward.getCount())
                .imgUrl(reward.getImgUrl())
                .betValue(reward.getBetValue())
                .balanceRequired(reward.getBalanceRequired())
                .starLevel(reward.getStarLevel())
                .build();
    }

    public static RedeemTreasureVo doToVo(RedeemTreasureDo redeemTreasureDo) {
        if (Objects.isNull(redeemTreasureDo)) {
            return null;
        }
        return RedeemTreasureVo.builder()
                .treasureId(redeemTreasureDo.getTreasureId())
                .playerName(redeemTreasureDo.getPlayerName())
                .channelId(redeemTreasureDo.getChannelId())
                .pId(redeemTreasureDo.getPId())
                .level(redeemTreasureDo.getLevel())
                .experience(redeemTreasureDo.getExperience())
                .refId(redeemTreasureDo.getRefId())
                .reward(rewardToVo(redeemTreasureDo.getReward()))
                .build();
    }

    private static RedeemTreasureVo.Reward rewardToVo(RedeemTreasureDo.Reward reward) {
        if (Objects.isNull(reward)) {
            return null;
        }
        return RedeemTreasureVo.Reward.builder()
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
