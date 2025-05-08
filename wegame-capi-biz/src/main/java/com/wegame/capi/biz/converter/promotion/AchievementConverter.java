package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetAchievementRes;
import com.wegame.capi.biz.domain.biz.promotion.AchievementDo;
import com.wegame.capi.client.domain.vo.promotion.AchievementVo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class AchievementConverter {
    public static List<AchievementDo.Achievement> toDoList(List<GetAchievementRes.Achievement> achievementList) {
        return Optional.ofNullable(achievementList)
                .orElse(Collections.emptyList())
                .stream()
                .map(AchievementConverter::toDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    private static AchievementDo.Achievement toDo(GetAchievementRes.Achievement achievement) {
        if (Objects.isNull(achievement)) {
            return null;
        }
        return AchievementDo.Achievement.builder()
                .id(achievement.getId())
                .name(achievement.getName())
                .description(achievement.getDescription())
                .iconUrl(achievement.getIconUrl())
                .progressList(AchievementDo.Progress.builder()
                        .target(achievement.getProgressList().getTarget())
                        .current(achievement.getProgressList().getCurrent())
                        .build())
                .completeAt(achievement.getCompleteAt())
                .status(achievement.getStatus())
                .redeemId(achievement.getRedeemId())
                .rewards(achievementRewardToDoList(achievement.getRewards()))
                .build();
    }
    public static List<AchievementDo.Reward> achievementRewardToDoList(List<GetAchievementRes.Reward> rewardList) {
        return Optional.ofNullable(rewardList)
                .orElse(Collections.emptyList())
                .stream()
                .map(AchievementConverter::achievementRewardToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    public static AchievementDo.Reward achievementRewardToDo(GetAchievementRes.Reward reward) {
        if (Objects.isNull(reward)) {
            return null;
        }
        return AchievementDo.Reward.builder()
                .rewardConfigId(reward.getRewardConfigId())
                .name(reward.getName())
                .count(reward.getCount())
                .starLevel(reward.getStarLevel())
                .imgUrl(reward.getImgUrl())
                .build();
    }


    public static List<AchievementVo> toVoList(List<AchievementDo.Achievement> achievementList) {
        return Optional.ofNullable(achievementList)
                .orElse(Collections.emptyList())
                .stream()
                .map(AchievementConverter::toVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    private static AchievementVo toVo(AchievementDo.Achievement achievement) {
        if (Objects.isNull(achievement)) {
            return null;
        }
        return AchievementVo.builder()
                .id(achievement.getId())
                .name(achievement.getName())
                .description(achievement.getDescription())
                .iconUrl(achievement.getIconUrl())
                .progressList(AchievementVo.Progress.builder()
                        .target(achievement.getProgressList().getTarget())
                        .current(achievement.getProgressList().getCurrent())
                        .build())
                .completeAt(Objects.isNull(achievement.getCompleteAt()) ? null : achievement.getCompleteAt().getTime())
                .status(achievement.getStatus())
                .redeemId(achievement.getRedeemId())
                .rewards(rewardToVoList(achievement.getRewards()))
                .build();
    }
    public static List<AchievementVo.Reward> rewardToVoList(List<AchievementDo.Reward> rewardList) {
        return Optional.ofNullable(rewardList)
                .orElse(Collections.emptyList())
                .stream()
                .map(AchievementConverter::rewardToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    public static AchievementVo.Reward rewardToVo(AchievementDo.Reward reward) {
        if (Objects.isNull(reward)) {
            return null;
        }
        return AchievementVo.Reward.builder()
                .rewardConfigId(reward.getRewardConfigId())
                .name(reward.getName())
                .count(reward.getCount())
                .starLevel(reward.getStarLevel())
                .imgUrl(reward.getImgUrl())
                .build();
    }
}
