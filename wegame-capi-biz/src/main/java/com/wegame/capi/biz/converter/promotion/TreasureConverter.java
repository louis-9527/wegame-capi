package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetTreasureRes;
import com.wegame.capi.biz.domain.biz.promotion.TreasureDo;
import com.wegame.capi.client.domain.vo.promotion.TreasureVo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TreasureConverter {
    public static List<TreasureDo> toDoList(List<GetTreasureRes.Treasure> treasureList) {
        return Optional.ofNullable(treasureList)
                .orElse(Collections.emptyList())
                .stream()
                .map(TreasureConverter::toDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static TreasureDo toDo(GetTreasureRes.Treasure treasure) {
        if (Objects.isNull(treasure)) {
            return null;
        }
        return TreasureDo.builder()
                .treasureId(treasure.getTreasureId())
                .name(treasure.getName())
                .currentExperience(treasure.getExperience())
                .targetExperience(treasure.getTarget())
                .status(treasure.getStatus())
                .vipLevel(treasure.getLevel())
                .rewards(rewardToDoList(treasure.getRewards()))
                .build();
    }

    public static List<TreasureDo.Reward> rewardToDoList(List<GetTreasureRes.Reward> rewardList) {
        return Optional.ofNullable(rewardList)
                .orElse(Collections.emptyList())
                .stream()
                .map(TreasureConverter::rewardToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static TreasureDo.Reward rewardToDo(GetTreasureRes.Reward reward) {
        if (Objects.isNull(reward)) {
            return null;
        }
        return TreasureDo.Reward.builder()
                .rewardId(reward.getRewardConfigId())
                .rewardName(reward.getName())
                .rewardCount(reward.getCount())
                .starLevel(reward.getStarLevel())
                .imgUrl(reward.getImgUrl())
                .build();
    }


    /////////////////////////////////////////
    public static List<TreasureVo> toVoList(List<TreasureDo> treasureDoList) {
        return Optional.ofNullable(treasureDoList)
                .orElse(Collections.emptyList())
                .stream()
                .map(TreasureConverter::toVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static TreasureVo toVo(TreasureDo treasure) {
        if (Objects.isNull(treasure)) {
            return null;
        }
        return TreasureVo.builder()
                .treasureId(treasure.getTreasureId())
                .name(treasure.getName())
                .currentExperience(treasure.getCurrentExperience())
                .targetExperience(treasure.getTargetExperience())
                .status(treasure.getStatus())
                .vipLevel(treasure.getVipLevel())
                .rewards(rewardToVoList(treasure.getRewards()))
                .build();
    }

    public static List<TreasureVo.Reward> rewardToVoList(List<TreasureDo.Reward> rewardList) {
        return Optional.ofNullable(rewardList)
                .orElse(Collections.emptyList())
                .stream()
                .map(TreasureConverter::rewardToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static TreasureVo.Reward rewardToVo(TreasureDo.Reward reward) {
        if (Objects.isNull(reward)) {
            return null;
        }
        return TreasureVo.Reward.builder()
                .rewardId(reward.getRewardId())
                .rewardName(reward.getRewardName())
                .rewardCount(reward.getRewardCount())
                .starLevel(reward.getStarLevel())
                .imgUrl(reward.getImgUrl())
                .build();
    }
}
