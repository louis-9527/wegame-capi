package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetRewardDetailRes;
import com.wegame.capi.biz.domain.biz.promotion.RewordDetailDo;
import com.wegame.capi.client.domain.vo.promotion.RewardDetailVo;
import com.wegame.capi.common.utils.AmountUtil;

import java.util.Objects;


public class RewordDetailConverter {

    public static RewordDetailDo toDo(GetRewardDetailRes.RewardDetail rewardDetail) {
        if (Objects.isNull(rewardDetail)) {
            return null;
        }
        RewordDetailDo rewordDetailDo = new RewordDetailDo();
        rewordDetailDo.setName(rewardDetail.getName());
        rewordDetailDo.setDescription(rewardDetail.getDescription());
        rewordDetailDo.setBalanceRequired(AmountUtil.toLong(rewardDetail.getBalanceRequired()));
        rewordDetailDo.setImgUrl(rewardDetail.getImgUrl());
        rewordDetailDo.setGameId(rewardDetail.getGameId());
        rewordDetailDo.setGameCode(rewardDetail.getGameCode());
        rewordDetailDo.setGameName(rewardDetail.getGameName());
        rewordDetailDo.setRewardType(rewardDetail.getRewardType());
        rewordDetailDo.setBetValue(AmountUtil.toLong(rewardDetail.getBetValue()));
        rewordDetailDo.setStarLevel(rewardDetail.getStarLevel());
        rewordDetailDo.setPeriodDay(rewardDetail.getPeriodDay());
        rewordDetailDo.setPeriodHour(rewardDetail.getPeriodHour());
        return rewordDetailDo;
    }

    public static RewardDetailVo doToVo(RewordDetailDo rewordDetailDo) {
        if (Objects.isNull(rewordDetailDo)) {
            return null;
        }
        RewardDetailVo detailVo = new RewardDetailVo();
        detailVo.setName(rewordDetailDo.getName());
        detailVo.setDescription(rewordDetailDo.getDescription());
        detailVo.setBalanceRequired(rewordDetailDo.getBalanceRequired());
        detailVo.setImgUrl(rewordDetailDo.getImgUrl());
        detailVo.setSkipGameId(rewordDetailDo.getGameId());
        detailVo.setGameId(rewordDetailDo.getGameCode());
        detailVo.setGameName(rewordDetailDo.getGameName());
        detailVo.setRewardType(rewordDetailDo.getRewardType());
        detailVo.setBetValue(rewordDetailDo.getBetValue());
        detailVo.setStarLevel(rewordDetailDo.getStarLevel());
        detailVo.setPeriodDay(rewordDetailDo.getPeriodDay());
        detailVo.setPeriodHour(rewordDetailDo.getPeriodHour());
        return detailVo;
    }
}
