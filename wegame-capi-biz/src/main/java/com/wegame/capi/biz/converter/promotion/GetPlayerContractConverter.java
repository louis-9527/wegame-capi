package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.contract.GetPlayerContractRes;
import com.wegame.capi.biz.domain.biz.contract.GetPlayerContractDo;
import com.wegame.capi.biz.domain.biz.promotion.AchievementDo;
import com.wegame.capi.client.domain.vo.contract.GetPlayerContractVo;
import com.wegame.capi.client.domain.vo.promotion.AchievementVo;

import java.util.Objects;

public class GetPlayerContractConverter {
    public static GetPlayerContractDo resToDo(GetPlayerContractRes res) {
        if (Objects.isNull(res)) {
            return null;
        }
        return GetPlayerContractDo.builder()
                .pId(res.getPId())
                .channelId(res.getChannelId())
                .refId(res.getRefId())
                .playerName(res.getPlayerName())
                .createTime(res.getCreateTime())
                .endTime(res.getEndTime())
                .rewardId(res.getRewardId())
                .experience(res.getExperience())
                .target(res.getTarget())
                .status(res.getStatus())
                .imgURL(res.getImgURL())
                .rewardType(res.getRewardType())
                .isEnabled(res.getIsEnabled())
                .build();
    }

    public static GetPlayerContractVo doToVo(GetPlayerContractDo getPlayerContractDo) {
        if (Objects.isNull(getPlayerContractDo)) {
            return null;
        }
        return GetPlayerContractVo.builder()
                .pId(getPlayerContractDo.getPId())
                .channelId(getPlayerContractDo.getChannelId())
                .refId(getPlayerContractDo.getRefId())
                .playerName(getPlayerContractDo.getPlayerName())
                .createTime(getPlayerContractDo.getCreateTime())
                .endTime(getPlayerContractDo.getEndTime())
                .rewardId(getPlayerContractDo.getRewardId())
                .experience(getPlayerContractDo.getExperience())
                .target(getPlayerContractDo.getTarget())
                .status(getPlayerContractDo.getStatus())
                .imgURL(getPlayerContractDo.getImgURL())
                .rewardType(getPlayerContractDo.getRewardType())
                .isEnabled(getPlayerContractDo.getIsEnabled())
                .build();
    }
}
