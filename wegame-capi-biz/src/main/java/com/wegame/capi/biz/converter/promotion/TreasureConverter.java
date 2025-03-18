package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetTreasureRes;
import com.wegame.capi.api.domain.res.promotion.RedeemTreasureRes;
import com.wegame.capi.client.domain.vo.promotion.RedeemTreasureVo;
import com.wegame.capi.client.domain.vo.promotion.TreasureVo;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TreasureConverter {

    public static List<TreasureVo> toVoList(List<GetTreasureRes.Treasure> treasureList) {
        if (CollectionUtils.isEmpty(treasureList)) {
            return Collections.emptyList();
        }
        return treasureList.stream()
                .map(TreasureConverter::toVo).
                collect(Collectors.toList());
    }

    public static TreasureVo toVo(GetTreasureRes.Treasure treasure) {
        if (Objects.isNull(treasure)) {
            return null;
        }
        return TreasureVo.builder()
                .treasureId(treasure.getTreasureId())
                .name(treasure.getName())
                .currentExperience(treasure.getExperience())
                .targetExperience(treasure.getTarget())
                .status(treasure.getStatus())
                .vipLevel(treasure.getLevel())
                .rewards(getTreasureRewardToVo(treasure.getRewards()))
                .build();
    }

    public static List<TreasureVo.RewardVo> getTreasureRewardToVo(List<GetTreasureRes.Reward> rewardList) {
        if (CollectionUtils.isEmpty(rewardList)) {
            return Collections.emptyList();
        }
        return rewardList.stream()
                .map(TreasureConverter::toVo)
                .collect(Collectors.toList());
    }

    public static TreasureVo.RewardVo toVo(GetTreasureRes.Reward reward) {
        if (Objects.isNull(reward)) {
            return null;
        }
        return TreasureVo.RewardVo.builder()
                .rewardId(reward.getRewardConfigId())
                .rewardName(reward.getName())
                .rewardCount(reward.getCount())
                .starLevel(reward.getStarLevel())
                .imgUrl(reward.getImgUrl())
                .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static RedeemTreasureVo toVo(RedeemTreasureRes redeemTreasureRes) {
        if (Objects.isNull(redeemTreasureRes)) {
            return null;
        }
        return RedeemTreasureVo.builder()
                .treasureId(redeemTreasureRes.getTreasureId())
                .reward(toVo(redeemTreasureRes.getReward()))
                .build();
    }

    public static RedeemTreasureVo.TreasureRewardVo toVo(RedeemTreasureRes.Reward reward) {
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
