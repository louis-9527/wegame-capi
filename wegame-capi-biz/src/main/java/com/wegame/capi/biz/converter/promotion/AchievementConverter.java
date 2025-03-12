package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetAchievementRes;
import com.wegame.capi.api.domain.res.promotion.RedeemAchievementRes;
import com.wegame.capi.client.domain.vo.promotion.AchievementVo;
import com.wegame.capi.client.domain.vo.promotion.RedeemAchievementVo;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AchievementConverter {

    public static List<AchievementVo> getAchievementToVo(List<GetAchievementRes.Achievement> achievementList) {
        if (CollectionUtils.isEmpty(achievementList)) {
            return Collections.emptyList();
        }
        return achievementList.stream().map(AchievementConverter::getAchievementToVo).collect(Collectors.toList());
    }

    public static AchievementVo getAchievementToVo(GetAchievementRes.Achievement achievement) {
        if (Objects.isNull(achievement)) {
            return null;
        }
        return AchievementVo.builder()
                .id(achievement.getId())
                .name(achievement.getName())
                .description(achievement.getDescription())
                .iconUrl(achievement.getIconUrl())
                .progressVoList(Objects.isNull(achievement) ? null : AchievementVo.ProgressVo.builder().target(achievement.getProgressList().getTarget()).current(achievement.getProgressList().getCurrent()).build())
                .completeAt(Objects.isNull(achievement.getCompleteAt()) ? null : achievement.getCompleteAt().getTime())
                .status(achievement.getStatus())
                .redeemId(achievement.getRedeemId())
                .rewards(getAchievementRewardToVo(achievement.getRewards()))
                .build();
    }

    public static List<AchievementVo.RewardVo> getAchievementRewardToVo(List<GetAchievementRes.Reward> rewardList) {
        if (CollectionUtils.isEmpty(rewardList)) {
            return Collections.emptyList();
        }
        return rewardList.stream().map(AchievementConverter::getAchievementRewardToVo).collect(Collectors.toList());
    }

    public static AchievementVo.RewardVo getAchievementRewardToVo(GetAchievementRes.Reward reward) {
        if (Objects.isNull(reward)) {
            return null;
        }
        return AchievementVo.RewardVo.builder()
                .rewardConfigId(reward.getRewardConfigId())
                .name(reward.getName())
                .count(reward.getCount())
                .starLevel(reward.getStarLevel())
                .iconUrl(reward.getIconUrl())
                .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static RedeemAchievementVo redeemAchievementRewardToVo(RedeemAchievementRes res) {
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

    public static List<RedeemAchievementVo.RedeemAchievementRewardVo> redeemAchievementRewardToVo(List<RedeemAchievementRes.RedeemAchievementReward> rewardList) {
        if (CollectionUtils.isEmpty(rewardList)) {
            return null;
        }
        return rewardList.stream().map(AchievementConverter::redeemAchievementRewardToVo).collect(Collectors.toList());
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
