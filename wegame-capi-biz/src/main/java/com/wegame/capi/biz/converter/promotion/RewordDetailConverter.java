package com.wegame.capi.biz.converter.promotion;

import com.wegame.capi.api.domain.res.promotion.GetRewardDetailRes;
import com.wegame.capi.biz.domain.biz.promotion.RewordDetailDo;
import com.wegame.capi.client.domain.vo.promotion.RewardDetailVo;
import com.wegame.capi.common.utils.AmountUtil;


public class RewordDetailConverter {
    public static RewordDetailDo resToDo(GetRewardDetailRes.RewardDetail rewardDetail) {
        if (rewardDetail == null) {
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

    public static RewardDetailVo doToVo(RewordDetailDo detailDo) {
        if (detailDo == null) {
            return null;
        }
        RewardDetailVo detailVo = new RewardDetailVo();
        detailVo.setName(detailDo.getName());
        detailVo.setDescription(detailDo.getDescription());
        detailVo.setBalanceRequired(detailDo.getBalanceRequired());
        detailVo.setImgUrl(detailDo.getImgUrl());
        detailVo.setSkipGameId(detailDo.getGameId());
        detailVo.setGameId(detailDo.getGameCode());
        detailVo.setGameName(detailDo.getGameName());
        detailVo.setRewardType(detailDo.getRewardType());
        detailVo.setBetValue(detailDo.getBetValue());
        detailVo.setStarLevel(detailDo.getStarLevel());
        detailVo.setPeriodDay(detailDo.getPeriodDay());
        detailVo.setPeriodHour(detailDo.getPeriodHour());

        return detailVo;
    }
}
