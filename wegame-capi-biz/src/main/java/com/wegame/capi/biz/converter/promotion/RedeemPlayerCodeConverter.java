package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.RedeemPlayerCodeRes;
import com.wegame.capi.biz.domain.biz.promotion.RedeemPlayerCodeDo;
import com.wegame.capi.client.domain.vo.promotion.RedeemPlayerCodeVo;

import java.util.Objects;

public class RedeemPlayerCodeConverter {

    public static RedeemPlayerCodeDo resToDo(RedeemPlayerCodeRes res) {
        if (Objects.isNull(res)) {
            return null;
        }
        return RedeemPlayerCodeDo.builder()
                .playerName(res.getPlayerName())
                .channelId(res.getChannelId())
                .pId(res.getPId())
                .refId(res.getRefId())
                .code(res.getCode())
                .rewardType(res.getRewardType())
                .reward(res.getReward())
                .build();
    }

    public static RedeemPlayerCodeVo doToVo(RedeemPlayerCodeDo redeemPlayerCodeDo) {
        if (Objects.isNull(redeemPlayerCodeDo)) {
            return null;
        }
        return RedeemPlayerCodeVo.builder()
                .playerName(redeemPlayerCodeDo.getPlayerName())
                .channelId(redeemPlayerCodeDo.getChannelId())
                .pId(redeemPlayerCodeDo.getPId())
                .refId(redeemPlayerCodeDo.getRefId())
                .code(redeemPlayerCodeDo.getCode())
                .rewardType(redeemPlayerCodeDo.getRewardType())
                .reward(redeemPlayerCodeDo.getReward())
                .build();
    }

}
