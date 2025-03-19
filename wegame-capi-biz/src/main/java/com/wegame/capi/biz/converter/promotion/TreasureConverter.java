package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetTreasureRes;
import com.wegame.capi.client.domain.vo.promotion.TreasureVo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TreasureConverter {

    public static List<TreasureVo> toVoList(List<GetTreasureRes.Treasure> treasureList) {
        return Optional.ofNullable(treasureList)
                .orElse(Collections.emptyList())
                .stream()
                .map(TreasureConverter::toVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
                .rewards(treasureRewardToVoList(treasure.getRewards()))
                .build();
    }

    public static List<TreasureVo.RewardVo> treasureRewardToVoList(List<GetTreasureRes.Reward> rewardList) {
        return Optional.ofNullable(rewardList)
                .orElse(Collections.emptyList())
                .stream()
                .map(TreasureConverter::treasureRewardToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static TreasureVo.RewardVo treasureRewardToVo(GetTreasureRes.Reward reward) {
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
}
