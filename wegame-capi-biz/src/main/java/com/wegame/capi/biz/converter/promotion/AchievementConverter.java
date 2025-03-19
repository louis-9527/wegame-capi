package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetAchievementRes;
import com.wegame.capi.client.domain.vo.promotion.AchievementVo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class AchievementConverter {

    public static List<AchievementVo> toVoList(List<GetAchievementRes.Achievement> achievementList) {
        return Optional.ofNullable(achievementList)
                .orElse(Collections.emptyList())
                .stream()
                .map(AchievementConverter::toVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static AchievementVo toVo(GetAchievementRes.Achievement achievement) {
        if (Objects.isNull(achievement)) {
            return null;
        }
        return AchievementVo.builder()
                .id(achievement.getId())
                .name(achievement.getName())
                .description(achievement.getDescription())
                .iconUrl(achievement.getIconUrl())
                .progressList(AchievementVo.ProgressVo.builder()
                        .target(achievement.getProgressList().getTarget())
                        .current(achievement.getProgressList().getCurrent())
                        .build())
                .completeAt(Objects.isNull(achievement.getCompleteAt()) ? null : achievement.getCompleteAt().getTime())
                .status(achievement.getStatus())
                .redeemId(achievement.getRedeemId())
                .rewards(achievementRewardToVoList(achievement.getRewards()))
                .build();
    }

    public static List<AchievementVo.RewardVo> achievementRewardToVoList(List<GetAchievementRes.Reward> rewardList) {
        return Optional.ofNullable(rewardList)
                .orElse(Collections.emptyList())
                .stream()
                .map(AchievementConverter::achievementRewardToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static AchievementVo.RewardVo achievementRewardToVo(GetAchievementRes.Reward reward) {
        if (Objects.isNull(reward)) {
            return null;
        }
        return AchievementVo.RewardVo.builder()
                .rewardConfigId(reward.getRewardConfigId())
                .name(reward.getName())
                .count(reward.getCount())
                .starLevel(reward.getStarLevel())
                .imgUrl(reward.getImgUrl())
                .build();
    }
}
